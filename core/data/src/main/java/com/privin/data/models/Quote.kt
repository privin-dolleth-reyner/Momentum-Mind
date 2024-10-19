package com.privin.data.models

import com.privin.database.model.DailyQuoteEntity
import com.privin.database.util.getToday

data class Quote(
    val quote: String,
    val author: String,
    val isFavorite: Boolean = false
) {
    fun mapToDailyQuoteEntity(): DailyQuoteEntity {
        return DailyQuoteEntity(
            date = getToday(),
            quote = quote,
            author = author,
        )
    }
}
