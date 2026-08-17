package com.toukir.equinox.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "quotes_table")
data class QuoteEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val quote: String,
    val author: String = "",
    val targetProfile: String = "ALL", // "ALL", "MARRIED", "UNMARRIED"
    val isCustom: Boolean = false
)
