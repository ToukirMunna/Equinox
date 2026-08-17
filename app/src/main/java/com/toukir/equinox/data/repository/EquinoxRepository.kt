package com.toukir.equinox.data.repository

import com.toukir.equinox.data.local.dao.EmergencyTodoDao
import com.toukir.equinox.data.local.dao.LogDao
import com.toukir.equinox.data.local.dao.QuoteDao
import com.toukir.equinox.data.local.entity.EmergencyTodoEntity
import com.toukir.equinox.data.local.entity.LogEntryEntity
import com.toukir.equinox.data.local.entity.QuoteEntity
import com.toukir.equinox.data.local.model.ChecklistItemAudit
import com.toukir.equinox.data.local.model.EventType
import com.toukir.equinox.data.local.model.RelationshipStatus
import com.toukir.equinox.data.preferences.UserPreferencesManager
import com.toukir.equinox.data.remote.FirestoreSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

class EquinoxRepository(
    private val logDao: LogDao,
    private val emergencyTodoDao: EmergencyTodoDao,
    private val quoteDao: QuoteDao,
    val preferencesManager: UserPreferencesManager,
    val syncManager: FirestoreSyncManager
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    val syncEvent: SharedFlow<Long> = syncManager.syncEvent

    // --- Preferences & Profile ---
    val isOnboardingCompleted: Flow<Boolean> = preferencesManager.isOnboardingCompleted
    val relationshipStatus: Flow<RelationshipStatus> = preferencesManager.relationshipStatus
    val streakStartTimestamp: Flow<Long> = preferencesManager.streakStartTimestamp
    val themeMode: Flow<String> = preferencesManager.themeMode
    val lastSyncTimestamp: Flow<Long> = preferencesManager.lastSyncTimestamp
    val showCircularRing: Flow<Boolean> = preferencesManager.showCircularRing
    val isBiometricLockEnabled: Flow<Boolean> = preferencesManager.isBiometricLockEnabled

    private fun triggerAutoSync() {
        if (syncManager.isUserSignedIn()) {
            scope.launch {
                try {
                    syncManager.syncIncremental()
                } catch (e: Exception) {
                    // Fail silently, items remain isSyncedToCloud = false for next catch-up
                }
            }
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        preferencesManager.setOnboardingCompleted(completed)
    }

    suspend fun setRelationshipStatus(status: RelationshipStatus) {
        preferencesManager.setRelationshipStatus(status)
        triggerAutoSync()
    }

    suspend fun setStreakStartTimestamp(timestamp: Long) {
        preferencesManager.setStreakStartTimestamp(timestamp)
        triggerAutoSync()
    }

    suspend fun setThemeMode(mode: String) {
        preferencesManager.setThemeMode(mode)
        triggerAutoSync()
    }

    suspend fun setShowCircularRing(show: Boolean) {
        preferencesManager.setShowCircularRing(show)
        triggerAutoSync()
    }

    suspend fun setBiometricLockEnabled(enabled: Boolean) {
        preferencesManager.setBiometricLockEnabled(enabled)
        triggerAutoSync()
    }

    // --- Logs ---
    val allLogs: Flow<List<LogEntryEntity>> = logDao.getAllLogs()
    val urgeVictoryCount: Flow<Int> = logDao.getUrgeVictoryCount()
    val slipCount: Flow<Int> = logDao.getSlipCount()

    suspend fun insertLog(entry: LogEntryEntity) {
        logDao.insertLog(entry)
        triggerAutoSync()
    }

    suspend fun logRelapse(
        timestamp: Long,
        type: EventType,
        trigger: String,
        notes: String,
        checklistAudit: List<ChecklistItemAudit> = emptyList()
    ) {
        val entry = LogEntryEntity(
            id = UUID.randomUUID().toString(),
            timestamp = timestamp,
            type = type,
            triggerReason = trigger,
            notes = notes,
            checklistAudit = checklistAudit,
            isSyncedToCloud = false
        )
        logDao.insertLog(entry)

        if (type == EventType.FULL_RELAPSE) {
            preferencesManager.setStreakStartTimestamp(timestamp)
        }
        triggerAutoSync()
    }

    suspend fun logUrgeVictory(
        notes: String = "",
        checklistAudit: List<ChecklistItemAudit> = emptyList()
    ) {
        val entry = LogEntryEntity(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            type = EventType.URGE_OVERCOME,
            triggerReason = "",
            notes = notes,
            checklistAudit = checklistAudit,
            isSyncedToCloud = false
        )
        logDao.insertLog(entry)
        triggerAutoSync()
    }

    suspend fun logReflection(notes: String) {
        val entry = LogEntryEntity(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            type = EventType.REFLECTION,
            triggerReason = "",
            notes = notes,
            checklistAudit = emptyList(),
            isSyncedToCloud = false
        )
        logDao.insertLog(entry)
        triggerAutoSync()
    }

    suspend fun deleteLog(id: String) {
        logDao.deleteLogById(id)
    }

    // --- Emergency To-Dos ---
    fun getEmergencyTodos(profile: RelationshipStatus): Flow<List<EmergencyTodoEntity>> {
        return emergencyTodoDao.getTodosForProfile(profile.name)
    }

    val allTodos: Flow<List<EmergencyTodoEntity>> = emergencyTodoDao.getAllTodos()

    suspend fun addCustomTodo(title: String, profile: String = "ALL") {
        val todo = EmergencyTodoEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            isCustom = true,
            targetProfile = profile,
            orderIndex = 100
        )
        emergencyTodoDao.insertTodo(todo)
        triggerAutoSync()
    }

    suspend fun deleteTodo(id: String) {
        emergencyTodoDao.deleteTodoById(id)
    }

    // --- Quotes ---
    fun getQuotes(profile: RelationshipStatus): Flow<List<QuoteEntity>> {
        return quoteDao.getQuotesForProfile(profile.name)
    }

    val allQuotes: Flow<List<QuoteEntity>> = quoteDao.getAllQuotes()

    suspend fun addCustomQuote(quote: String, author: String, profile: String = "ALL") {
        val quoteEntity = QuoteEntity(
            id = UUID.randomUUID().toString(),
            quote = quote,
            author = author,
            targetProfile = profile,
            isCustom = true
        )
        quoteDao.insertQuote(quoteEntity)
        triggerAutoSync()
    }

    suspend fun deleteQuote(id: String) {
        quoteDao.deleteQuoteById(id)
    }

    // --- Cloud Sync Operations ---
    suspend fun syncNow(): Result<Int> {
        return syncManager.syncIncremental()
    }

    suspend fun checkCloudBackupExists(userId: String? = null): Boolean {
        return syncManager.checkCloudBackupExists(userId)
    }

    suspend fun overwriteCloudWithLocal(): Result<Int> {
        return syncManager.overwriteCloudWithLocal()
    }

    suspend fun overwriteLocalWithCloud(): Result<Int> {
        return syncManager.overwriteLocalWithCloud()
    }

    suspend fun resetAllLocalData() {
        logDao.clearAllLogs()
        emergencyTodoDao.clearAllTodos()
        quoteDao.clearAllQuotes()
        preferencesManager.clearAllPreferences()
    }
}
