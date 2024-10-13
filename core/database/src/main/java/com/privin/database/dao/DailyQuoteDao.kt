package com.privin.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.privin.database.model.DailyQuoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyQuoteDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDailyQuote(dailyQuote: DailyQuoteEntity)

    @Query("SELECT * FROM daily_quotes WHERE date = :date")
    fun getDailyQuoteByDate(date: String): Flow<DailyQuoteEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM daily_quotes WHERE date = :date)")
    fun isDailyQuoteAvailable(date: String): Boolean


}