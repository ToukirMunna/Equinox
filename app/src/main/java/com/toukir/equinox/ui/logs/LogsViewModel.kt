package com.toukir.equinox.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toukir.equinox.data.local.entity.LogEntryEntity
import com.toukir.equinox.data.local.model.EventType
import com.toukir.equinox.data.repository.EquinoxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LogsViewModel(
    private val repository: EquinoxRepository
) : ViewModel() {

    private val _currentFilter = MutableStateFlow(LogFilter.ALL)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<LogsUiState> = combine(
        repository.allLogs,
        _currentFilter,
        _searchQuery
    ) { allLogs, filter, query ->
        val filteredLogs = allLogs.filter { log ->
            val matchesFilter = when (filter) {
                LogFilter.ALL -> true
                LogFilter.RELAPSES -> log.type == EventType.FULL_RELAPSE
                LogFilter.URGES_WON -> log.type == EventType.URGE_OVERCOME
                LogFilter.SLIPS -> log.type == EventType.PORN_ONLY_SLIP
                LogFilter.REFLECTIONS -> log.type == EventType.REFLECTION
            }
            val matchesQuery = query.isBlank() ||
                    log.notes.contains(query, ignoreCase = true) ||
                    log.triggerReason.contains(query, ignoreCase = true) ||
                    log.checklistAudit.any { it.title.contains(query, ignoreCase = true) }

            matchesFilter && matchesQuery
        }
        LogsUiState(
            logs = filteredLogs,
            currentFilter = filter,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LogsUiState()
    )

    fun setFilter(filter: LogFilter) {
        _currentFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addReflection(notes: String) {
        if (notes.isNotBlank()) {
            viewModelScope.launch {
                repository.logReflection(notes.trim())
            }
        }
    }

    fun deleteLog(id: String) {
        viewModelScope.launch {
            repository.deleteLog(id)
        }
    }
}
