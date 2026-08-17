package com.toukir.equinox.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.toukir.equinox.data.local.entity.QuoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes_table")
    fun getAllQuotes(): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes_table WHERE targetProfile IN ('ALL', :profile)")
    fun getQuotesForProfile(profile: String): Flow<List<QuoteEntity>>

    @Query("SELECT COUNT(*) FROM quotes_table")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<QuoteEntity>)

    @Query("DELETE FROM quotes_table WHERE id = :id")
    suspend fun deleteQuoteById(id: String)

    @Query("DELETE FROM quotes_table")
    suspend fun clearAllQuotes()
}
