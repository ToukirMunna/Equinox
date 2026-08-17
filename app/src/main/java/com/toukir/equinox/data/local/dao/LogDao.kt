package com.toukir.equinox.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.toukir.equinox.data.local.entity.LogEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM logs_table ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<LogEntryEntity>>

    @Query("SELECT * FROM logs_table WHERE type = :type ORDER BY timestamp DESC")
    fun getLogsByType(type: String): Flow<List<LogEntryEntity>>

    @Query("SELECT COUNT(*) FROM logs_table WHERE type = 'URGE_OVERCOME'")
    fun getUrgeVictoryCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM logs_table WHERE type = 'PORN_ONLY_SLIP'")
    fun getSlipCount(): Flow<Int>

    @Query("SELECT * FROM logs_table WHERE isSyncedToCloud = 0")
    suspend fun getUnsyncedLogs(): List<LogEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LogEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<LogEntryEntity>)

    @Query("UPDATE logs_table SET isSyncedToCloud = 1 WHERE id IN (:ids)")
    suspend fun markLogsAsSynced(ids: List<String>)

    @Query("DELETE FROM logs_table WHERE id = :id")
    suspend fun deleteLogById(id: String)

    @Query("DELETE FROM logs_table")
    suspend fun clearAllLogs()
}
