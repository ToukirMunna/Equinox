package com.toukir.equinox.data.remote

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class FirestoreSyncManager(
    private val context: Context,
    private val logDao: LogDao,
    private val todoDao: EmergencyTodoDao,
    private val quoteDao: QuoteDao,
    private val preferencesManager: UserPreferencesManager
) {
    private val auth: FirebaseAuth? = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        null
    }

    private val firestore: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        null
    }

    private val gson = Gson()

    private val _syncEvent = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val syncEvent = _syncEvent.asSharedFlow()

    fun getCurrentUser(): FirebaseUser? {
        return auth?.currentUser
    }

    fun isUserSignedIn(): Boolean {
        return auth?.currentUser != null && !auth.currentUser!!.isAnonymous
    }

    /**
     * Checks whether an existing cloud backup exists under equinox/{userId}/profile/metadata
     */
    suspend fun checkCloudBackupExists(customUserId: String? = null): Boolean {
        val uid = customUserId ?: auth?.currentUser?.uid ?: return false
        val db = firestore ?: return false

        return try {
            val doc = db.collection("equinox").document(uid)
                .collection("profile").document("metadata")
                .get()
                .await()
            doc.exists() && (doc.getLong("streakStartTimestamp") != null)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Delta / Incremental sync: Pushes unsynced logs, updates profile metadata,
     * and triggers the 5-second cloud tick indicator.
     */
    suspend fun syncIncremental(): Result<Int> {
        val user = auth?.currentUser ?: return Result.failure(Exception("Not signed in"))
        val db = firestore ?: return Result.failure(Exception("Cloud service unavailable"))

        return try {
            val userId = user.uid

            // 1. Sync Profile Metadata
            val streakStart = preferencesManager.streakStartTimestamp.first()
            val relationship = preferencesManager.relationshipStatus.first().name
            val ring = preferencesManager.showCircularRing.first()
            val theme = preferencesManager.themeMode.first()
            val bioLock = preferencesManager.isBiometricLockEnabled.first()

            val profileMap = hashMapOf(
                "streakStartTimestamp" to streakStart,
                "relationshipStatus" to relationship,
                "showCircularRing" to ring,
                "themeMode" to theme,
                "isBiometricLockEnabled" to bioLock,
                "lastSyncTimestamp" to System.currentTimeMillis(),
                "appVersion" to "1.0.0"
            )

            db.collection("equinox").document(userId)
                .collection("profile").document("metadata")
                .set(profileMap, SetOptions.merge())
                .await()

            // 2. Sync Unsynced Logs
            val unsyncedLogs = logDao.getUnsyncedLogs()
            val syncedIds = mutableListOf<String>()

            for (log in unsyncedLogs) {
                val logMap = hashMapOf(
                    "id" to log.id,
                    "timestamp" to log.timestamp,
                    "type" to log.type.name,
                    "triggerReason" to log.triggerReason,
                    "notes" to log.notes,
                    "checklistAuditJson" to gson.toJson(log.checklistAudit),
                    "createdAt" to System.currentTimeMillis()
                )

                db.collection("equinox").document(userId)
                    .collection("logs").document(log.id)
                    .set(logMap, SetOptions.merge())
                    .await()

                syncedIds.add(log.id)
            }

            if (syncedIds.isNotEmpty()) {
                logDao.markLogsAsSynced(syncedIds)
            }

            // 3. Sync Custom To-Dos
            val customTodos = todoDao.getAllTodos().first().filter { it.isCustom }
            for (todo in customTodos) {
                val todoMap = hashMapOf(
                    "id" to todo.id,
                    "title" to todo.title,
                    "targetProfile" to todo.targetProfile,
                    "orderIndex" to todo.orderIndex
                )
                db.collection("equinox").document(userId)
                    .collection("custom_todos").document(todo.id)
                    .set(todoMap, SetOptions.merge())
                    .await()
            }

            // 4. Sync Custom Quotes
            val customQuotes = quoteDao.getAllQuotes().first().filter { it.isCustom }
            for (quote in customQuotes) {
                val quoteMap = hashMapOf(
                    "id" to quote.id,
                    "quote" to quote.quote,
                    "author" to quote.author,
                    "targetProfile" to quote.targetProfile
                )
                db.collection("equinox").document(userId)
                    .collection("custom_quotes").document(quote.id)
                    .set(quoteMap, SetOptions.merge())
                    .await()
            }

            val now = System.currentTimeMillis()
            preferencesManager.setLastSyncTimestamp(now)
            _syncEvent.emit(now)

            Result.success(syncedIds.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Overwrites the remote Cloud data with local device data (Case: Keep Device Data).
     */
    suspend fun overwriteCloudWithLocal(): Result<Int> {
        val user = auth?.currentUser ?: return Result.failure(Exception("Not signed in"))
        val db = firestore ?: return Result.failure(Exception("Cloud service unavailable"))

        return try {
            val userId = user.uid

            // 1. Write profile
            val streakStart = preferencesManager.streakStartTimestamp.first()
            val relationship = preferencesManager.relationshipStatus.first().name
            val ring = preferencesManager.showCircularRing.first()
            val theme = preferencesManager.themeMode.first()
            val bioLock = preferencesManager.isBiometricLockEnabled.first()

            val profileMap = hashMapOf(
                "streakStartTimestamp" to streakStart,
                "relationshipStatus" to relationship,
                "showCircularRing" to ring,
                "themeMode" to theme,
                "isBiometricLockEnabled" to bioLock,
                "lastSyncTimestamp" to System.currentTimeMillis(),
                "appVersion" to "1.0.0"
            )

            db.collection("equinox").document(userId)
                .collection("profile").document("metadata")
                .set(profileMap)
                .await()

            // 2. Write all local logs
            val allLogs = logDao.getAllLogs().first()
            for (log in allLogs) {
                val logMap = hashMapOf(
                    "id" to log.id,
                    "timestamp" to log.timestamp,
                    "type" to log.type.name,
                    "triggerReason" to log.triggerReason,
                    "notes" to log.notes,
                    "checklistAuditJson" to gson.toJson(log.checklistAudit),
                    "createdAt" to System.currentTimeMillis()
                )

                db.collection("equinox").document(userId)
                    .collection("logs").document(log.id)
                    .set(logMap)
                    .await()
            }

            if (allLogs.isNotEmpty()) {
                logDao.markLogsAsSynced(allLogs.map { it.id })
            }

            val now = System.currentTimeMillis()
            preferencesManager.setLastSyncTimestamp(now)
            _syncEvent.emit(now)

            Result.success(allLogs.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Overwrites local SQLite DB with remote Cloud data (Case: Keep Cloud Data / Fresh Google Sign-In).
     */
    suspend fun overwriteLocalWithCloud(): Result<Int> {
        val user = auth?.currentUser ?: return Result.failure(Exception("Not signed in"))
        val db = firestore ?: return Result.failure(Exception("Cloud service unavailable"))

        return try {
            val userId = user.uid

            // 1. Pull Profile
            val profileDoc = db.collection("equinox").document(userId)
                .collection("profile").document("metadata")
                .get()
                .await()

            if (profileDoc.exists()) {
                profileDoc.getLong("streakStartTimestamp")?.let {
                    preferencesManager.setStreakStartTimestamp(it)
                }
                profileDoc.getString("relationshipStatus")?.let { statusStr ->
                    try {
                        preferencesManager.setRelationshipStatus(RelationshipStatus.valueOf(statusStr))
                    } catch (e: Exception) {}
                }
                profileDoc.getBoolean("showCircularRing")?.let {
                    preferencesManager.setShowCircularRing(it)
                }
                profileDoc.getString("themeMode")?.let {
                    preferencesManager.setThemeMode(it)
                }
                profileDoc.getBoolean("isBiometricLockEnabled")?.let {
                    preferencesManager.setBiometricLockEnabled(it)
                }
            }

            // 2. Pull Remote Logs
            val remoteLogsSnapshot = db.collection("equinox").document(userId)
                .collection("logs")
                .get()
                .await()

            val restoredLogs = mutableListOf<LogEntryEntity>()

            for (doc in remoteLogsSnapshot.documents) {
                val id = doc.getString("id") ?: doc.id
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                val typeStr = doc.getString("type") ?: EventType.FULL_RELAPSE.name
                val triggerReason = doc.getString("triggerReason") ?: ""
                val notes = doc.getString("notes") ?: ""
                val auditJson = doc.getString("checklistAuditJson")

                val eventType = try {
                    EventType.valueOf(typeStr)
                } catch (e: Exception) {
                    EventType.FULL_RELAPSE
                }

                val checklistAudit = if (!auditJson.isNullOrEmpty()) {
                    try {
                        gson.fromJson(auditJson, Array<ChecklistItemAudit>::class.java).toList()
                    } catch (e: Exception) {
                        emptyList()
                    }
                } else emptyList()

                restoredLogs.add(
                    LogEntryEntity(
                        id = id,
                        timestamp = timestamp,
                        type = eventType,
                        triggerReason = triggerReason,
                        notes = notes,
                        checklistAudit = checklistAudit,
                        isSyncedToCloud = true
                    )
                )
            }

            if (restoredLogs.isNotEmpty()) {
                logDao.clearAllLogs()
                logDao.insertLogs(restoredLogs)
            }

            // 3. Pull Custom To-Dos
            val remoteTodosSnapshot = db.collection("equinox").document(userId)
                .collection("custom_todos")
                .get()
                .await()

            for (doc in remoteTodosSnapshot.documents) {
                val title = doc.getString("title") ?: continue
                val profile = doc.getString("targetProfile") ?: "ALL"
                val todo = EmergencyTodoEntity(
                    id = doc.id,
                    title = title,
                    isCustom = true,
                    targetProfile = profile,
                    orderIndex = (doc.getLong("orderIndex") ?: 100L).toInt()
                )
                todoDao.insertTodo(todo)
            }

            // 4. Pull Custom Quotes
            val remoteQuotesSnapshot = db.collection("equinox").document(userId)
                .collection("custom_quotes")
                .get()
                .await()

            for (doc in remoteQuotesSnapshot.documents) {
                val quote = doc.getString("quote") ?: continue
                val author = doc.getString("author") ?: ""
                val profile = doc.getString("targetProfile") ?: "ALL"
                val quoteEntity = QuoteEntity(
                    id = doc.id,
                    quote = quote,
                    author = author,
                    targetProfile = profile,
                    isCustom = true
                )
                quoteDao.insertQuote(quoteEntity)
            }

            val now = System.currentTimeMillis()
            preferencesManager.setLastSyncTimestamp(now)
            _syncEvent.emit(now)

            Result.success(restoredLogs.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth?.signOut()
    }
}
