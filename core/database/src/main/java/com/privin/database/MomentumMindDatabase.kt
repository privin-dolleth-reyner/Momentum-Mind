package com.privin.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.privin.database.dao.DailyQuoteDao
import com.privin.database.dao.QuoteDao
import com.privin.database.model.DailyQuoteEntity
import com.privin.database.model.QuoteEntity
import com.privin.database.util.InstantConverter

@Database(version = 1, entities = [QuoteEntity::class, DailyQuoteEntity::class])
@TypeConverters(InstantConverter::class)
abstract class MomentumMindDatabase: RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
    abstract fun dailyQuoteDao(): DailyQuoteDao
}