package com.toukir.equinox.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.toukir.equinox.data.local.model.ChecklistItemAudit
import com.toukir.equinox.data.local.model.EventType

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromEventType(value: EventType): String {
        return value.name
    }

    @TypeConverter
    fun toEventType(value: String): EventType {
        return try {
            EventType.valueOf(value)
        } catch (e: Exception) {
            EventType.FULL_RELAPSE
        }
    }

    @TypeConverter
    fun fromChecklistAudit(list: List<ChecklistItemAudit>?): String {
        return gson.toJson(list ?: emptyList<ChecklistItemAudit>())
    }

    @TypeConverter
    fun toChecklistAudit(json: String?): List<ChecklistItemAudit> {
        if (json.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<ChecklistItemAudit>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
