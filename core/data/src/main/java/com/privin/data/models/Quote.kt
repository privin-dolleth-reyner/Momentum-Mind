package com.privin.data.models

import com.privin.database.model.DailyQuoteEntity
import com.privin.database.util.getToday

data class Quote(
    val date: String = getToday(),
    val quote: String,
    val author: String,
    val isFavorite: Boolean = false
) {
    fun mapToDailyQuoteEntity(): DailyQuoteEntity {
        return DailyQuoteEntity(
            date = date,
            quote = quote,
            author = author,
            isFavourite = isFavorite
        )
    }
}
