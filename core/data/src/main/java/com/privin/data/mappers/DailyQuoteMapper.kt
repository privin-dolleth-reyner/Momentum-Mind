package com.privin.data.mappers

import com.privin.data.models.Quote
import com.privin.database.model.DailyQuoteEntity
import com.privin.network.model.QuoteNetworkData


fun DailyQuoteEntity.mapToQuote(): Quote {
    return Quote(
        quote = quote,
        author = author,
        isFavorite = isFavourite
    )
}

fun QuoteNetworkData.mapToDailyQuoteEntity(): DailyQuoteEntity {
    return DailyQuoteEntity(
        quote = quote,
        author = author,
        htmlString = htmlString
    )
}