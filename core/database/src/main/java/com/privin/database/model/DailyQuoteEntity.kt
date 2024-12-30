package com.privin.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.privin.database.util.getToday

@Entity(tableName = "daily_quotes")
data class DailyQuoteEntity(
    @PrimaryKey val date: String = getToday(),
    val quote: String,
    val author: String,
    val htmlString: String? = null,
    val isFavourite: Boolean = false,
)