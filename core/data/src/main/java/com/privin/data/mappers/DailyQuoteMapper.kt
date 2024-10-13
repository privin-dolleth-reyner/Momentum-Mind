package com.privin.data.mappers

import com.privin.data.models.Quote
import com.privin.database.model.DailyQuoteEntity
import com.privin.network.model.QuoteData


fun DailyQuoteEntity.mapToQuote(): Quote {
    return Quote(
        quote = quote,
        author = author,
    )
}

fun QuoteData.mapToDailyQuoteEntity(): DailyQuoteEntity {
    return DailyQuoteEntity(
        quote = quote,
        author = author,
        htmlString = htmlString
    )
}