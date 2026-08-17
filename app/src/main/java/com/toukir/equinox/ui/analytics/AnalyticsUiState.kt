package com.toukir.equinox.ui.analytics

import java.util.Calendar

data class AnalyticsUiState(
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH), // 0-indexed
    val monthName: String = "",
    val dayCells: List<CalendarDayCell> = emptyList(),
    val selectedDay: CalendarDayCell? = null,
    val timeOfDayStats: List<TimeOfDayStat> = emptyList(),
    val dayOfWeekStats: List<DayOfWeekStat> = emptyList(),
    val safestDay: String? = null,
    val riskiestDay: String? = null,
    val averageCycleDays: Double = 0.0,
    val totalCyclesRecorded: Int = 0,
    val protocolRankings: List<ProtocolEffectivenessItem> = emptyList(),
    val triggerDistribution: List<TriggerStatItem> = emptyList(),
    val currentMonthCleanRate: Double = 100.0,
    val previousMonthCleanRate: Double = 100.0,
    val haltScore: HaltScore = HaltScore(0, 0, 0, 0),
    val streakStartTimestamp: Long = System.currentTimeMillis()
)
