package com.toukir.equinox.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toukir.equinox.data.local.entity.LogEntryEntity
import com.toukir.equinox.data.local.model.EventType
import com.toukir.equinox.data.repository.EquinoxRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val repository: EquinoxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.streakStartTimestamp,
                repository.urgeVictoryCount,
                repository.slipCount,
                repository.showCircularRing,
                repository.allLogs
            ) { startTimestamp, urgeCount, slips, showRing, logs ->
                val (pulse, topTrigger, triggerCount, bestStreak, winRate, hoursSaved) = computeAnalytics(
                    startTimestamp = startTimestamp,
                    urgeCount = urgeCount,
                    slipsCount = slips,
                    logs = logs
                )

                _uiState.value = _uiState.value.copy(
                    streakStartTimestamp = startTimestamp,
                    urgeVictoryCount = urgeCount,
                    slipsCount = slips,
                    showCircularRing = showRing,
                    sevenDaysPulse = pulse,
                    topTriggerName = topTrigger,
                    topTriggerCount = triggerCount,
                    bestStreakDays = bestStreak,
                    urgeWinRate = winRate,
                    hoursSaved = hoursSaved
                )
                calculateTimeBreakdown(startTimestamp)
            }.collect {}
        }

        // Ticker loop down to the second
        viewModelScope.launch {
            while (true) {
                calculateTimeBreakdown(_uiState.value.streakStartTimestamp)
                delay(1000)
            }
        }

        // Ephemeral 5-Second Cloud Sync Done Indicator
        viewModelScope.launch {
            repository.syncEvent.collect {
                _uiState.value = _uiState.value.copy(showSyncDone = true)
                delay(5000)
                _uiState.value = _uiState.value.copy(showSyncDone = false)
            }
        }

        // Catch-up sync on launch/resume
        viewModelScope.launch {
            try {
                repository.syncNow()
            } catch (e: Exception) {}
        }
    }

    private data class AnalyticsResult(
        val pulse: List<DayPulseItem>,
        val topTriggerName: String?,
        val topTriggerCount: Int,
        val bestStreakDays: Long,
        val urgeWinRate: Int,
        val hoursSaved: Long
    )

    private fun computeAnalytics(
        startTimestamp: Long,
        urgeCount: Int,
        slipsCount: Int,
        logs: List<LogEntryEntity>
    ): AnalyticsResult {
        val now = System.currentTimeMillis()
        val currentStreakDays = ((now - startTimestamp).coerceAtLeast(0L)) / (1000 * 86400)

        // 1. Compute 7-day Fortitude Pulse
        val pulseItems = mutableListOf<DayPulseItem>()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("d", Locale.getDefault())

        val calendar = Calendar.getInstance()
        val todayYear = calendar.get(Calendar.YEAR)
        val todayDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        for (i in 6 downTo 0) {
            val dayCal = Calendar.getInstance()
            dayCal.add(Calendar.DAY_OF_YEAR, -i)
            dayCal.set(Calendar.HOUR_OF_DAY, 0)
            dayCal.set(Calendar.MINUTE, 0)
            dayCal.set(Calendar.SECOND, 0)
            dayCal.set(Calendar.MILLISECOND, 0)
            val dayStart = dayCal.timeInMillis

            dayCal.set(Calendar.HOUR_OF_DAY, 23)
            dayCal.set(Calendar.MINUTE, 59)
            dayCal.set(Calendar.SECOND, 59)
            dayCal.set(Calendar.MILLISECOND, 999)
            val dayEnd = dayCal.timeInMillis

            val isToday = (dayCal.get(Calendar.YEAR) == todayYear && dayCal.get(Calendar.DAY_OF_YEAR) == todayDayOfYear)

            val logsInDay = logs.filter { it.timestamp in dayStart..dayEnd }
            val status = when {
                logsInDay.any { it.type == EventType.FULL_RELAPSE } -> DayPulseStatus.RELAPSE
                logsInDay.any { it.type == EventType.PORN_ONLY_SLIP } -> DayPulseStatus.SLIP
                else -> DayPulseStatus.CLEAN
            }

            pulseItems.add(
                DayPulseItem(
                    dayLabel = dayFormat.format(Date(dayStart)),
                    dateNumber = dateFormat.format(Date(dayStart)),
                    isToday = isToday,
                    status = status
                )
            )
        }

        // 2. Compute Best Streak (historical vs current)
        var maxStreakDays = currentStreakDays
        val relapseTimestamps = logs.filter { it.type == EventType.FULL_RELAPSE }
            .map { it.timestamp }
            .sorted()

        if (relapseTimestamps.size > 1) {
            for (j in 0 until relapseTimestamps.size - 1) {
                val diffDays = (relapseTimestamps[j + 1] - relapseTimestamps[j]) / (1000 * 86400)
                if (diffDays > maxStreakDays) {
                    maxStreakDays = diffDays
                }
            }
        }
        val bestStreak = maxStreakDays.coerceAtLeast(currentStreakDays)

        // 3. Compute Urge Win Rate
        val totalUrgesLogged = urgeCount + logs.count { it.type == EventType.FULL_RELAPSE && it.checklistAudit.isNotEmpty() }
        val winRate = if (totalUrgesLogged > 0) {
            ((urgeCount.toDouble() / totalUrgesLogged) * 100).toInt().coerceIn(0, 100)
        } else {
            100
        }

        // 4. Compute Hours Saved (~45 mins per day clean + 45 mins per urge defeated)
        val minutesSaved = (currentStreakDays * 45) + (urgeCount * 45)
        val hoursSaved = (minutesSaved / 60).coerceAtLeast(currentStreakDays)

        // 5. Top Trigger Analysis
        val triggerCounts = logs
            .filter { it.type == EventType.FULL_RELAPSE || it.type == EventType.PORN_ONLY_SLIP }
            .map { it.triggerReason.trim() }
            .filter { it.isNotEmpty() }
            .groupingBy { it }
            .eachCount()

        val topEntry = triggerCounts.maxByOrNull { it.value }
        val topTriggerName = topEntry?.key
        val topTriggerCount = topEntry?.value ?: 0

        return AnalyticsResult(
            pulse = pulseItems,
            topTriggerName = topTriggerName,
            topTriggerCount = topTriggerCount,
            bestStreakDays = bestStreak,
            urgeWinRate = winRate,
            hoursSaved = hoursSaved
        )
    }

    private fun calculateTimeBreakdown(startTimestamp: Long) {
        val now = System.currentTimeMillis()
        val diffMillis = (now - startTimestamp).coerceAtLeast(0L)

        val totalSeconds = diffMillis / 1000
        val days = totalSeconds / 86400
        val hours = (totalSeconds % 86400) / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        val (milestone, nextMilestone, progress) = calculateMilestone(diffMillis)

        _uiState.value = _uiState.value.copy(
            days = days,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            milestoneName = milestone,
            nextMilestoneName = nextMilestone,
            milestoneProgress = progress
        )
    }

    private fun calculateMilestone(diffMillis: Long): Triple<String, String, Float> {
        val hours = diffMillis / (1000 * 3600.0)

        return when {
            hours < 24 -> {
                val progress = (hours / 24.0).toFloat().coerceIn(0f, 1f)
                Triple("Starting Journey", "24 Hours (The First Horizon)", progress)
            }
            hours < 72 -> { // 3 Days
                val progress = ((hours - 24) / (72 - 24).toDouble()).toFloat().coerceIn(0f, 1f)
                Triple("24 Hours Achieved", "3 Days (Dopamine Reset)", progress)
            }
            hours < 168 -> { // 7 Days
                val progress = ((hours - 72) / (168 - 72).toDouble()).toFloat().coerceIn(0f, 1f)
                Triple("3 Days Achieved", "7 Days (Clarity Awakening)", progress)
            }
            hours < 336 -> { // 14 Days
                val progress = ((hours - 168) / (336 - 168).toDouble()).toFloat().coerceIn(0f, 1f)
                Triple("7 Days Achieved", "14 Days (Building Fortitude)", progress)
            }
            hours < 720 -> { // 30 Days
                val progress = ((hours - 336) / (720 - 336).toDouble()).toFloat().coerceIn(0f, 1f)
                Triple("14 Days Achieved", "30 Days (Neural Rewiring)", progress)
            }
            hours < 2160 -> { // 90 Days
                val progress = ((hours - 720) / (2160 - 720).toDouble()).toFloat().coerceIn(0f, 1f)
                Triple("30 Days Achieved", "90 Days (Equinox Mastery)", progress)
            }
            else -> {
                Triple("90+ Days (Transformed)", "Continuous Mastery", 1f)
            }
        }
    }

    fun logRelapse(timestamp: Long, type: EventType, trigger: String, notes: String) {
        viewModelScope.launch {
            repository.logRelapse(
                timestamp = timestamp,
                type = type,
                trigger = trigger,
                notes = notes,
                checklistAudit = emptyList()
            )
        }
    }
}
