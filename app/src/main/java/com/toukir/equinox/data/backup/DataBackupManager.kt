package com.toukir.equinox.data.backup

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.toukir.equinox.data.local.entity.EmergencyTodoEntity
import com.toukir.equinox.data.local.entity.LogEntryEntity
import com.toukir.equinox.data.local.entity.QuoteEntity
import com.toukir.equinox.data.local.model.RelationshipStatus
import com.toukir.equinox.data.repository.EquinoxRepository
import kotlinx.coroutines.flow.first

data class EquinoxBackupData(
    val version: Int = 1,
    val exportedAtMillis: Long = System.currentTimeMillis(),
    val streakStartTimestamp: Long = 0L,
    val showCircularRing: Boolean = false,
    val relationshipStatus: String = "UNMARRIED",
    val logs: List<LogEntryEntity> = emptyList(),
    val todos: List<EmergencyTodoEntity> = emptyList(),
    val quotes: List<QuoteEntity> = emptyList()
)

class DataBackupManager(
    private val repository: EquinoxRepository
) {
    private val gson = Gson()

    suspend fun createBackupJson(): String {
        val streakStart = repository.streakStartTimestamp.first()
        val isRing = repository.showCircularRing.first()
        val status = repository.relationshipStatus.first().name
        val logs = repository.allLogs.first()
        val todos = repository.allTodos.first()
        val quotes = repository.allQuotes.first()

        val backup = EquinoxBackupData(
            version = 1,
            exportedAtMillis = System.currentTimeMillis(),
            streakStartTimestamp = streakStart,
            showCircularRing = isRing,
            relationshipStatus = status,
            logs = logs,
            todos = todos,
            quotes = quotes
        )

        return gson.toJson(backup)
    }

    suspend fun restoreFromJson(json: String): Boolean {
        return try {
            val type = object : TypeToken<EquinoxBackupData>() {}.type
            val backup: EquinoxBackupData = gson.fromJson(json, type) ?: return false

            // Restore streak
            if (backup.streakStartTimestamp > 0) {
                repository.setStreakStartTimestamp(backup.streakStartTimestamp)
            }
            repository.setShowCircularRing(backup.showCircularRing)

            try {
                repository.setRelationshipStatus(RelationshipStatus.valueOf(backup.relationshipStatus))
            } catch (e: Exception) {}

            // Restore logs
            backup.logs.forEach { log ->
                repository.insertLog(log)
            }

            // Restore custom items
            backup.todos.filter { it.isCustom }.forEach { todo ->
                repository.addCustomTodo(todo.title, todo.targetProfile)
            }
            backup.quotes.filter { it.isCustom }.forEach { quote ->
                repository.addCustomQuote(quote.quote, quote.author, quote.targetProfile)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
