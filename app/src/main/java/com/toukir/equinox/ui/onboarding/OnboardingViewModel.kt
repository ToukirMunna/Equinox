package com.toukir.equinox.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toukir.equinox.data.local.model.RelationshipStatus
import com.toukir.equinox.data.repository.EquinoxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val step: Int = 0, // 0: Auth/Mode Choice, 1: Profile & Streak Setup
    val isGoogleUser: Boolean = false,
    val userEmail: String = "",
    val selectedStatus: RelationshipStatus = RelationshipStatus.UNMARRIED,
    val streakStartTimestamp: Long = System.currentTimeMillis(),
    val isCustomStreak: Boolean = false,
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false
)

class OnboardingViewModel(
    private val repository: EquinoxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun chooseOfflineMode() {
        _uiState.value = _uiState.value.copy(step = 1, isGoogleUser = false)
    }

    fun onGoogleSignInSuccess(email: String, onCloudDataRestored: () -> Unit) {
        _uiState.value = _uiState.value.copy(isLoading = true, isGoogleUser = true, userEmail = email)
        viewModelScope.launch {
            val hasBackup = repository.checkCloudBackupExists()
            if (hasBackup) {
                // Cloud backup exists! Restore all data and enter app immediately
                repository.overwriteLocalWithCloud()
                repository.setOnboardingCompleted(true)
                _uiState.value = _uiState.value.copy(isLoading = false, isCompleted = true)
                onCloudDataRestored()
            } else {
                // Fresh cloud user: advance to profile setup
                _uiState.value = _uiState.value.copy(isLoading = false, step = 1)
            }
        }
    }

    fun selectRelationshipStatus(status: RelationshipStatus) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
    }

    fun setStreakStartTimestamp(timestamp: Long) {
        _uiState.value = _uiState.value.copy(
            streakStartTimestamp = timestamp,
            isCustomStreak = true
        )
    }

    fun finishOnboarding() {
        viewModelScope.launch {
            repository.setRelationshipStatus(_uiState.value.selectedStatus)
            repository.setStreakStartTimestamp(_uiState.value.streakStartTimestamp)
            repository.setOnboardingCompleted(true)
            _uiState.value = _uiState.value.copy(isCompleted = true)
        }
    }
}
