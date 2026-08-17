package com.toukir.equinox.ui.home

data class HomeUiState(
    val streakStartTimestamp: Long = System.currentTimeMillis(),
    val days: Long = 0,
    val hours: Long = 0,
    val minutes: Long = 0,
    val seconds: Long = 0,
    val milestoneName: String = "",
    val nextMilestoneName: String = "",
    val milestoneProgress: Float = 0f,
    val slipsCount: Int = 0,
    val urgeVictoryCount: Int = 0,
    val showCircularRing: Boolean = false,
    val bestStreakDays: Long = 0,
    val urgeWinRate: Int = 100,
    val hoursSaved: Long = 0,
    val sevenDaysPulse: List<DayPulseItem> = emptyList(),
    val topTriggerName: String? = null,
    val topTriggerCount: Int = 0,
    val showSyncDone: Boolean = false
)
