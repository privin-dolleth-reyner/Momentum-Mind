package com.privin.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quote: String,
    val author: String,
    val charCount: String? = null,
    val htmlString: String,
    val isFavorite: Boolean = false
)