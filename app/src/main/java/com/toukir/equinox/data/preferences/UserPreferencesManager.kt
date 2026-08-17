package com.toukir.equinox.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.toukir.equinox.data.local.model.RelationshipStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "equinox_user_prefs")

class UserPreferencesManager(private val context: Context) {

    companion object {
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_RELATIONSHIP_STATUS = stringPreferencesKey("relationship_status")
        private val KEY_STREAK_START_TIMESTAMP = longPreferencesKey("streak_start_timestamp")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
        private val KEY_SHOW_CIRCULAR_RING = booleanPreferencesKey("show_circular_ring")
        private val KEY_BIOMETRIC_LOCK = booleanPreferencesKey("biometric_lock")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_ONBOARDING_COMPLETED] ?: false
    }

    val isBiometricLockEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_BIOMETRIC_LOCK] ?: false
    }

    val relationshipStatus: Flow<RelationshipStatus> = context.dataStore.data.map { preferences ->
        val statusString = preferences[KEY_RELATIONSHIP_STATUS] ?: RelationshipStatus.UNMARRIED.name
        try {
            RelationshipStatus.valueOf(statusString)
        } catch (e: Exception) {
            RelationshipStatus.UNMARRIED
        }
    }

    val streakStartTimestamp: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_STREAK_START_TIMESTAMP] ?: System.currentTimeMillis()
    }

    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME_MODE] ?: "SYSTEM"
    }

    val lastSyncTimestamp: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_LAST_SYNC_TIMESTAMP] ?: 0L
    }

    val showCircularRing: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_SHOW_CIRCULAR_RING] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setRelationshipStatus(status: RelationshipStatus) {
        context.dataStore.edit { preferences ->
            preferences[KEY_RELATIONSHIP_STATUS] = status.name
        }
    }

    suspend fun setStreakStartTimestamp(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_STREAK_START_TIMESTAMP] = timestamp
        }
    }

    suspend fun setThemeMode(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = theme
        }
    }

    suspend fun setLastSyncTimestamp(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LAST_SYNC_TIMESTAMP] = timestamp
        }
    }

    suspend fun setShowCircularRing(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SHOW_CIRCULAR_RING] = show
        }
    }

    suspend fun setBiometricLockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BIOMETRIC_LOCK] = enabled
        }
    }

    suspend fun clearAllPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
