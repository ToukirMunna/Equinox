package com.toukir.equinox.ui.urge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toukir.equinox.data.local.model.ChecklistItemAudit
import com.toukir.equinox.data.local.model.EventType
import com.toukir.equinox.data.repository.EquinoxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UrgeViewModel(
    private val repository: EquinoxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UrgeUiState())
    val uiState: StateFlow<UrgeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val status = repository.relationshipStatus.first()

            val todoEntities = repository.getEmergencyTodos(status).first()
            val quoteEntities = repository.getQuotes(status).first()

            _uiState.value = _uiState.value.copy(
                todos = todoEntities.map { TodoCheckState(todo = it, isChecked = false) },
                quotes = quoteEntities
            )
        }
    }

    fun toggleTodo(todoId: String) {
        val updatedTodos = _uiState.value.todos.map {
            if (it.todo.id == todoId) {
                it.copy(isChecked = !it.isChecked)
            } else {
                it
            }
        }
        _uiState.value = _uiState.value.copy(todos = updatedTodos)
    }

    fun nextQuote() {
        if (_uiState.value.quotes.isNotEmpty()) {
            val nextIndex = (_uiState.value.currentQuoteIndex + 1) % _uiState.value.quotes.size
            _uiState.value = _uiState.value.copy(currentQuoteIndex = nextIndex)
        }
    }

    fun getChecklistAudit(): List<ChecklistItemAudit> {
        return _uiState.value.todos.map {
            ChecklistItemAudit(
                id = it.todo.id,
                title = it.todo.title,
                isCompleted = it.isChecked
            )
        }
    }

    fun markUrgeOvercome(notes: String = "") {
        viewModelScope.launch {
            val audit = getChecklistAudit()
            repository.logUrgeVictory(notes = notes, checklistAudit = audit)
            _uiState.value = _uiState.value.copy(isVictorious = true)
        }
    }

    fun logRelapseFromUrge(timestamp: Long, type: EventType, trigger: String, notes: String) {
        viewModelScope.launch {
            val audit = getChecklistAudit()
            repository.logRelapse(
                timestamp = timestamp,
                type = type,
                trigger = trigger,
                notes = notes,
                checklistAudit = audit
            )
            _uiState.value = _uiState.value.copy(isRelapseTriggered = true)
        }
    }
}
