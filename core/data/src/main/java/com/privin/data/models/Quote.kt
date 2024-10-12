package com.privin.data.models

import com.privin.network.model.QuoteData

data class Quote(
    val quote: String,
    val author: String
)

fun QuoteData.mapToQuote(): Quote {
    return Quote(
        quote = this.quote,
        author = this.author
    )
}