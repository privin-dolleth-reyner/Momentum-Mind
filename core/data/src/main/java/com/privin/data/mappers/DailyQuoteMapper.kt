package com.privin.data.mappers

import com.privin.data.models.Quote
import com.privin.database.model.DailyQuoteEntity
import com.privin.network.model.QuoteData


fun DailyQuoteEntity?.mapToQuote(): Quote {
    if (this == null) return Quote()
    return Quote(
        quote = quote,
        author = author,
        isFavorite = isFavourite
    )
}

fun QuoteData.mapToDailyQuoteEntity(): DailyQuoteEntity {
    return DailyQuoteEntity(
        quote = quote,
        author = author,
        htmlString = htmlString
    )
}