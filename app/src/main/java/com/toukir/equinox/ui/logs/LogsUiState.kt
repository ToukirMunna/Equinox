package com.toukir.equinox.ui.logs

import com.toukir.equinox.data.local.entity.LogEntryEntity
import com.toukir.equinox.data.local.model.EventType

enum class LogFilter {
    ALL,
    RELAPSES,
    URGES_WON,
    SLIPS,
    REFLECTIONS
}

data class LogsUiState(
    val logs: List<LogEntryEntity> = emptyList(),
    val currentFilter: LogFilter = LogFilter.ALL,
    val searchQuery: String = ""
)
