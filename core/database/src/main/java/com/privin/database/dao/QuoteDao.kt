package com.privin.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.privin.database.model.QuoteEntity

@Dao
interface QuoteDao {
    @Insert
    suspend fun insertQuote(quote: QuoteEntity)

    @Query("SELECT * FROM quotes")
    suspend fun getAllQuotes(): List<QuoteEntity>

}