package com.toukir.equinox.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.toukir.equinox.data.local.entity.EmergencyTodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyTodoDao {
    @Query("SELECT * FROM emergency_todos_table ORDER BY orderIndex ASC, title ASC")
    fun getAllTodos(): Flow<List<EmergencyTodoEntity>>

    @Query("SELECT * FROM emergency_todos_table WHERE targetProfile IN ('ALL', :profile) ORDER BY orderIndex ASC")
    fun getTodosForProfile(profile: String): Flow<List<EmergencyTodoEntity>>

    @Query("SELECT COUNT(*) FROM emergency_todos_table")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: EmergencyTodoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodos(todos: List<EmergencyTodoEntity>)

    @Query("DELETE FROM emergency_todos_table WHERE id = :id")
    suspend fun deleteTodoById(id: String)

    @Query("DELETE FROM emergency_todos_table")
    suspend fun clearAllTodos()
}
