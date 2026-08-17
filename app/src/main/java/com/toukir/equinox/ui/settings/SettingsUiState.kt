package com.toukir.equinox.ui.settings

import com.toukir.equinox.data.local.entity.EmergencyTodoEntity
import com.toukir.equinox.data.local.entity.QuoteEntity
import com.toukir.equinox.data.local.model.RelationshipStatus

data class SettingsUiState(
    val relationshipStatus: RelationshipStatus = RelationshipStatus.UNMARRIED,
    val streakStartTimestamp: Long = System.currentTimeMillis(),
    val themeMode: String = "SYSTEM",
    val lastSyncTimestamp: Long = 0L,
    val isUserSignedIn: Boolean = false,
    val userEmail: String = "",
    val allTodos: List<EmergencyTodoEntity> = emptyList(),
    val allQuotes: List<QuoteEntity> = emptyList(),
    val showCircularRing: Boolean = false,
    val isBiometricLockEnabled: Boolean = false,
    val syncMessage: String? = null
)
