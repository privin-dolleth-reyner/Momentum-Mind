package com.privin.data.mappers

import com.privin.data.models.Quote
import com.privin.database.model.QuoteEntity
import com.privin.network.model.QuoteNetworkData

fun QuoteNetworkData.mapToQuoteEntity(): QuoteEntity {
    return QuoteEntity(
        quote = quote,
        author = author,
        charCount = charCount,
        htmlString = htmlString
    )
}

fun QuoteNetworkData.mapToQuote(): Quote {
    return Quote(
        quote = quote,
        author = author
    )
}

fun QuoteEntity.mapToQuote(): Quote {
    return Quote(
        quote = quote,
        author = author,
    )
}