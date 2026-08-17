package com.toukir.equinox.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toukir.equinox.R
import com.toukir.equinox.data.local.entity.LogEntryEntity
import com.toukir.equinox.data.local.model.EventType
import com.toukir.equinox.data.repository.EquinoxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AnalyticsViewModel(
    private val repository: EquinoxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    private var cachedLogs: List<LogEntryEntity> = emptyList()
    private var cachedStreakStart: Long = System.currentTimeMillis()

    init {
        viewModelScope.launch {
            combine(
                repository.allLogs,
                repository.streakStartTimestamp
            ) { logs, streakStart ->
                cachedLogs = logs
                cachedStreakStart = streakStart
                recalculateAll()
            }.collect {}
        }
    }

    fun onPrevYear() {
        val currentYear = _uiState.value.selectedYear
        _uiState.value = _uiState.value.copy(selectedYear = currentYear - 1, selectedDay = null)
        recalculateAll()
    }

    fun onNextYear() {
        val currentYear = _uiState.value.selectedYear
        _uiState.value = _uiState.value.copy(selectedYear = currentYear + 1, selectedDay = null)
        recalculateAll()
    }

    fun onPrevMonth() {
        var year = _uiState.value.selectedYear
        var month = _uiState.value.selectedMonth - 1
        if (month < 0) {
            month = 11
            year -= 1
        }
        _uiState.value = _uiState.value.copy(selectedYear = year, selectedMonth = month, selectedDay = null)
        recalculateAll()
    }

    fun onNextMonth() {
        var year = _uiState.value.selectedYear
        var month = _uiState.value.selectedMonth + 1
        if (month > 11) {
            month = 0
            year += 1
        }
        _uiState.value = _uiState.value.copy(selectedYear = year, selectedMonth = month, selectedDay = null)
        recalculateAll()
    }

    fun onSelectDay(cell: CalendarDayCell) {
        val currentSelected = _uiState.value.selectedDay
        _uiState.value = _uiState.value.copy(
            selectedDay = if (currentSelected?.dateMillis == cell.dateMillis) null else cell
        )
    }

    private fun recalculateAll() {
        val year = _uiState.value.selectedYear
        val month = _uiState.value.selectedMonth

        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val monthName = monthFormat.format(cal.time)

        val dayCells = generateCalendarCells(year, month, cachedLogs)
        val timeStats = computeTimeOfDayStats(cachedLogs)
        val (weekdayStats, safest, riskiest) = computeDayOfWeekStats(cachedLogs)
        val (avgCycle, totalCycles) = computeCycleLength(cachedLogs, cachedStreakStart)
        val protocolRankings = computeProtocolRankings(cachedLogs)
        val triggerDist = computeTriggerDistribution(cachedLogs)
        val (currentClean, prevClean) = computeMonthCleanRates(year, month, cachedLogs)
        val haltScore = computeHaltScore(cachedLogs)

        _uiState.value = _uiState.value.copy(
            monthName = monthName,
            dayCells = dayCells,
            timeOfDayStats = timeStats,
            dayOfWeekStats = weekdayStats,
            safestDay = safest,
            riskiestDay = riskiest,
            averageCycleDays = avgCycle,
            totalCyclesRecorded = totalCycles,
            protocolRankings = protocolRankings,
            triggerDistribution = triggerDist,
            currentMonthCleanRate = currentClean,
            previousMonthCleanRate = prevClean,
            haltScore = haltScore,
            streakStartTimestamp = cachedStreakStart
        )
    }

    private fun computeMonthCleanRates(year: Int, month: Int, logs: List<LogEntryEntity>): Pair<Double, Double> {
        val currentMonthLogs = logs.filter { log ->
            val cal = Calendar.getInstance().apply { timeInMillis = log.timestamp }
            cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month
        }
        val currentRelapses = currentMonthLogs.count { it.type == EventType.FULL_RELAPSE || it.type == EventType.PORN_ONLY_SLIP }
        val daysInMonth = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
        }.getActualMaximum(Calendar.DAY_OF_MONTH)

        val currentCleanRate = ((daysInMonth - currentRelapses).coerceAtLeast(0).toDouble() / daysInMonth) * 100.0

        var prevYear = year
        var prevMonth = month - 1
        if (prevMonth < 0) {
            prevMonth = 11
            prevYear -= 1
        }
        val prevMonthLogs = logs.filter { log ->
            val cal = Calendar.getInstance().apply { timeInMillis = log.timestamp }
            cal.get(Calendar.YEAR) == prevYear && cal.get(Calendar.MONTH) == prevMonth
        }
        val prevRelapses = prevMonthLogs.count { it.type == EventType.FULL_RELAPSE || it.type == EventType.PORN_ONLY_SLIP }
        val daysInPrevMonth = Calendar.getInstance().apply {
            set(Calendar.YEAR, prevYear)
            set(Calendar.MONTH, prevMonth)
        }.getActualMaximum(Calendar.DAY_OF_MONTH)

        val prevCleanRate = ((daysInPrevMonth - prevRelapses).coerceAtLeast(0).toDouble() / daysInPrevMonth) * 100.0

        return Pair(currentCleanRate, prevCleanRate)
    }

    private fun computeHaltScore(logs: List<LogEntryEntity>): HaltScore {
        val relapses = logs.filter { it.type == EventType.FULL_RELAPSE || it.type == EventType.PORN_ONLY_SLIP }
        if (relapses.isEmpty()) return HaltScore(25, 25, 25, 25)

        var hungry = 0
        var angry = 0
        var lonely = 0
        var tired = 0

        relapses.forEach { log ->
            val reason = log.triggerReason.lowercase()
            when {
                reason.contains("boredom") || reason.contains("idle") || reason.contains("hunger") -> hungry++
                reason.contains("stress") || reason.contains("anxiety") || reason.contains("conflict") || reason.contains("anger") -> angry++
                reason.contains("lonel") || reason.contains("isolation") || reason.contains("social") -> lonely++
                reason.contains("late") || reason.contains("night") || reason.contains("fatigue") || reason.contains("exhaust") -> tired++
                else -> {
                    // Distribute across quadrants based on time of day
                    val hour = Calendar.getInstance().apply { timeInMillis = log.timestamp }.get(Calendar.HOUR_OF_DAY)
                    if (hour in 22..24 || hour in 0..5) tired++ else hungry++
                }
            }
        }

        val total = (hungry + angry + lonely + tired).coerceAtLeast(1)
        return HaltScore(
            hungryPercent = (hungry * 100) / total,
            angryPercent = (angry * 100) / total,
            lonelyPercent = (lonely * 100) / total,
            tiredPercent = (tired * 100) / total
        )
    }

    private fun generateCalendarCells(
        year: Int,
        month: Int,
        logs: List<LogEntryEntity>
    ): List<CalendarDayCell> {
        val cells = mutableListOf<CalendarDayCell>()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val todayCal = Calendar.getInstance()
        val todayYear = todayCal.get(Calendar.YEAR)
        val todayDayOfYear = todayCal.get(Calendar.DAY_OF_YEAR)

        // Monday = 0 ... Sunday = 6
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
        val leadingDays = (firstDayOfWeek - Calendar.MONDAY + 7) % 7

        calendar.add(Calendar.DAY_OF_MONTH, -leadingDays)

        for (i in 0 until 42) {
            val cellYear = calendar.get(Calendar.YEAR)
            val cellMonth = calendar.get(Calendar.MONTH)
            val cellDay = calendar.get(Calendar.DAY_OF_MONTH)
            val isCurrentMonth = (cellMonth == month && cellYear == year)
            val isToday = (cellYear == todayYear && calendar.get(Calendar.DAY_OF_YEAR) == todayDayOfYear)

            val startMillis = calendar.timeInMillis
            val endCal = (calendar.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            val endMillis = endCal.timeInMillis

            val dayLogs = logs.filter { it.timestamp in startMillis..endMillis }
            val relapses = dayLogs.count { it.type == EventType.FULL_RELAPSE }
            val slips = dayLogs.count { it.type == EventType.PORN_ONLY_SLIP }
            val urges = dayLogs.count { it.type == EventType.URGE_OVERCOME }
            val isClean = relapses == 0 && slips == 0

            cells.add(
                CalendarDayCell(
                    dayNumber = cellDay,
                    dateMillis = startMillis,
                    isCurrentMonth = isCurrentMonth,
                    isToday = isToday,
                    isClean = isClean,
                    relapsesCount = relapses,
                    slipsCount = slips,
                    urgesCount = urges,
                    dayLogs = dayLogs
                )
            )

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return cells
    }

    private fun computeTimeOfDayStats(logs: List<LogEntryEntity>): List<TimeOfDayStat> {
        val relapsesAndSlips = logs.filter { it.type == EventType.FULL_RELAPSE || it.type == EventType.PORN_ONLY_SLIP }
        val total = relapsesAndSlips.size.coerceAtLeast(1)

        var morning = 0
        var afternoon = 0
        var evening = 0
        var night = 0

        val cal = Calendar.getInstance()
        relapsesAndSlips.forEach { log ->
            cal.timeInMillis = log.timestamp
            when (cal.get(Calendar.HOUR_OF_DAY)) {
                in 5..11 -> morning++
                in 12..16 -> afternoon++
                in 17..21 -> evening++
                else -> night++
            }
        }

        return listOf(
            TimeOfDayStat(R.string.analytics_time_morning, morning, (morning * 100) / total),
            TimeOfDayStat(R.string.analytics_time_afternoon, afternoon, (afternoon * 100) / total),
            TimeOfDayStat(R.string.analytics_time_evening, evening, (evening * 100) / total),
            TimeOfDayStat(R.string.analytics_time_night, night, (night * 100) / total)
        )
    }

    private fun computeDayOfWeekStats(logs: List<LogEntryEntity>): Triple<List<DayOfWeekStat>, String?, String?> {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val counts = IntArray(7)

        val cal = Calendar.getInstance()
        logs.filter { it.type == EventType.FULL_RELAPSE || it.type == EventType.PORN_ONLY_SLIP }.forEach { log ->
            cal.timeInMillis = log.timestamp
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            val index = (dayOfWeek - Calendar.MONDAY + 7) % 7
            counts[index]++
        }

        val total = counts.sum().coerceAtLeast(1)
        val stats = days.mapIndexed { index, label ->
            DayOfWeekStat(
                dayLabel = label,
                count = counts[index],
                percentage = (counts[index] * 100) / total
            )
        }

        val maxEntry = stats.maxByOrNull { it.count }
        val minEntry = stats.minByOrNull { it.count }

        val riskiest = if ((maxEntry?.count ?: 0) > 0) maxEntry?.dayLabel else null
        val safest = minEntry?.dayLabel

        return Triple(stats, safest, riskiest)
    }

    private fun computeCycleLength(logs: List<LogEntryEntity>, streakStart: Long): Pair<Double, Int> {
        val relapses = logs.filter { it.type == EventType.FULL_RELAPSE }
            .map { it.timestamp }
            .sorted()

        if (relapses.isEmpty()) {
            val currentDays = ((System.currentTimeMillis() - streakStart).coerceAtLeast(0L)) / (1000.0 * 86400)
            return Pair(currentDays, 1)
        }

        val spans = mutableListOf<Double>()
        for (i in 0 until relapses.size - 1) {
            val spanDays = (relapses[i + 1] - relapses[i]) / (1000.0 * 86400)
            spans.add(spanDays)
        }
        val currentSpan = (System.currentTimeMillis() - relapses.last()) / (1000.0 * 86400)
        spans.add(currentSpan)

        val avg = spans.average()
        return Pair(avg, spans.size)
    }

    private fun computeProtocolRankings(logs: List<LogEntryEntity>): List<ProtocolEffectivenessItem> {
        val taskWins = mutableMapOf<String, Int>()
        val taskTotal = mutableMapOf<String, Int>()

        logs.forEach { log ->
            val isWin = (log.type == EventType.URGE_OVERCOME)
            log.checklistAudit.forEach { item ->
                if (item.isCompleted) {
                    taskTotal[item.title] = (taskTotal[item.title] ?: 0) + 1
                    if (isWin) {
                        taskWins[item.title] = (taskWins[item.title] ?: 0) + 1
                    }
                }
            }
        }

        return taskTotal.map { (title, total) ->
            val wins = taskWins[title] ?: 0
            val rate = if (total > 0) (wins * 100) / total else 0
            ProtocolEffectivenessItem(
                title = title,
                winsCount = wins,
                totalCount = total,
                successRate = rate
            )
        }.sortedByDescending { it.successRate }
    }

    private fun computeTriggerDistribution(logs: List<LogEntryEntity>): List<TriggerStatItem> {
        val counts = logs
            .filter { it.type == EventType.FULL_RELAPSE || it.type == EventType.PORN_ONLY_SLIP }
            .map { it.triggerReason.trim() }
            .filter { it.isNotEmpty() }
            .groupingBy { it }
            .eachCount()

        val total = counts.values.sum().coerceAtLeast(1)
        return counts.map { (trigger, count) ->
            TriggerStatItem(
                triggerName = trigger,
                count = count,
                percentage = (count * 100) / total
            )
        }.sortedByDescending { it.count }
    }
}
