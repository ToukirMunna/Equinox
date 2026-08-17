package com.toukir.equinox.ui.analytics

import com.toukir.equinox.data.local.entity.LogEntryEntity

data class CalendarDayCell(
    val dayNumber: Int,
    val dateMillis: Long,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isClean: Boolean,
    val relapsesCount: Int = 0,
    val slipsCount: Int = 0,
    val urgesCount: Int = 0,
    val dayLogs: List<LogEntryEntity> = emptyList()
)
