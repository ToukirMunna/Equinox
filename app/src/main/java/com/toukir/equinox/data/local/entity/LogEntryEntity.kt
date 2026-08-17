package com.toukir.equinox.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.toukir.equinox.data.local.model.ChecklistItemAudit
import com.toukir.equinox.data.local.model.EventType
import java.util.UUID

@Entity(tableName = "logs_table")
data class LogEntryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val type: EventType,
    val triggerReason: String = "",
    val notes: String = "",
    val checklistAudit: List<ChecklistItemAudit> = emptyList(),
    val isSyncedToCloud: Boolean = false
)
