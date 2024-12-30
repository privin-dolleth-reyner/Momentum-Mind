package com.privin.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuoteData (
    @Json(name = "q") val quote: String, // quotes
    @Json(name = "a") val author: String, // author
    @Json(name = "c") val charCount: String? = null, // char count
    @Json(name = "h") val htmlString: String? = null, // formatted html string
)

