package com.toukir.equinox.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "emergency_todos_table")
data class EmergencyTodoEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isCustom: Boolean = false,
    val targetProfile: String = "ALL", // "ALL", "MARRIED", "UNMARRIED"
    val orderIndex: Int = 0
)
