package com.toukir.equinox.ui.home

enum class DayPulseStatus {
    CLEAN,
    SLIP,
    RELAPSE
}

data class DayPulseItem(
    val dayLabel: String,    // e.g. "Mon", "Tue"
    val dateNumber: String,  // e.g. "17"
    val isToday: Boolean,
    val status: DayPulseStatus
)
