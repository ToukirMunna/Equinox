package com.toukir.equinox.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toukir.equinox.data.local.model.RelationshipStatus
import com.toukir.equinox.data.repository.EquinoxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: EquinoxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.relationshipStatus.collect { status ->
                _uiState.value = _uiState.value.copy(relationshipStatus = status)
            }
        }
        viewModelScope.launch {
            repository.streakStartTimestamp.collect { streak ->
                _uiState.value = _uiState.value.copy(streakStartTimestamp = streak)
            }
        }
        viewModelScope.launch {
            repository.themeMode.collect { theme ->
                _uiState.value = _uiState.value.copy(themeMode = theme)
            }
        }
        viewModelScope.launch {
            repository.lastSyncTimestamp.collect { syncTime ->
                val currentUser = repository.syncManager.getCurrentUser()
                _uiState.value = _uiState.value.copy(
                    lastSyncTimestamp = syncTime,
                    isUserSignedIn = repository.syncManager.isUserSignedIn(),
                    userEmail = currentUser?.email ?: ""
                )
            }
        }
        viewModelScope.launch {
            repository.allTodos.collect { todos ->
                _uiState.value = _uiState.value.copy(allTodos = todos)
            }
        }
        viewModelScope.launch {
            repository.allQuotes.collect { quotes ->
                _uiState.value = _uiState.value.copy(allQuotes = quotes)
            }
        }
        viewModelScope.launch {
            repository.showCircularRing.collect { showRing ->
                _uiState.value = _uiState.value.copy(showCircularRing = showRing)
            }
        }
        viewModelScope.launch {
            repository.isBiometricLockEnabled.collect { isEnabled ->
                _uiState.value = _uiState.value.copy(isBiometricLockEnabled = isEnabled)
            }
        }
    }

    private val backupManager = com.toukir.equinox.data.backup.DataBackupManager(repository)

    fun setBiometricLockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setBiometricLockEnabled(enabled)
        }
    }

    fun exportBackupJson(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = backupManager.createBackupJson()
            onResult(json)
        }
    }

    fun restoreBackupJson(json: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = backupManager.restoreFromJson(json)
            onResult(success)
        }
    }

    fun setShowCircularRing(show: Boolean) {
        viewModelScope.launch {
            repository.setShowCircularRing(show)
        }
    }

    fun setRelationshipStatus(status: RelationshipStatus) {
        viewModelScope.launch {
            repository.setRelationshipStatus(status)
        }
    }

    fun setStreakStartTimestamp(timestamp: Long) {
        viewModelScope.launch {
            repository.setStreakStartTimestamp(timestamp)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            repository.setThemeMode(mode)
        }
    }

    fun addCustomTodo(title: String) {
        if (title.isNotBlank()) {
            viewModelScope.launch {
                repository.addCustomTodo(title.trim(), "ALL")
            }
        }
    }

    fun deleteTodo(id: String) {
        viewModelScope.launch {
            repository.deleteTodo(id)
        }
    }

    fun addCustomQuote(quote: String, author: String) {
        if (quote.isNotBlank()) {
            viewModelScope.launch {
                repository.addCustomQuote(quote.trim(), author.trim(), "ALL")
            }
        }
    }

    fun deleteQuote(id: String) {
        viewModelScope.launch {
            repository.deleteQuote(id)
        }
    }

    fun onGoogleSignInResult(
        email: String,
        onConflictDetected: () -> Unit,
        onDirectlySynced: () -> Unit
    ) {
        viewModelScope.launch {
            val hasBackup = repository.checkCloudBackupExists()
            if (hasBackup) {
                _uiState.value = _uiState.value.copy(
                    isUserSignedIn = true,
                    userEmail = email
                )
                onConflictDetected()
            } else {
                // Fresh cloud account: directly push local data to cloud
                repository.overwriteCloudWithLocal()
                _uiState.value = _uiState.value.copy(
                    isUserSignedIn = true,
                    userEmail = email
                )
                onDirectlySynced()
            }
        }
    }

    fun resolveConflictKeepCloud(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.overwriteLocalWithCloud()
            onComplete()
        }
    }

    fun resolveConflictKeepDevice(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.overwriteCloudWithLocal()
            onComplete()
        }
    }

    fun signOut() {
        repository.syncManager.signOut()
        _uiState.value = _uiState.value.copy(
            isUserSignedIn = false,
            userEmail = ""
        )
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllLocalData()
        }
    }
}
