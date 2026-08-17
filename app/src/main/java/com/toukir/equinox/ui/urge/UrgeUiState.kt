package com.toukir.equinox.ui.urge

import com.toukir.equinox.data.local.entity.EmergencyTodoEntity
import com.toukir.equinox.data.local.entity.QuoteEntity

data class TodoCheckState(
    val todo: EmergencyTodoEntity,
    val isChecked: Boolean = false
)

data class UrgeUiState(
    val todos: List<TodoCheckState> = emptyList(),
    val quotes: List<QuoteEntity> = emptyList(),
    val currentQuoteIndex: Int = 0,
    val isVictorious: Boolean = false,
    val isRelapseTriggered: Boolean = false
)
