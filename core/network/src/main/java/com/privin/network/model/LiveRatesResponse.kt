package com.privin.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LiveRatesResponse(
    val status: String,
    val authority: String,
    val currency: String,
    val unit: String,
    val timestamp: String,
    val rates: Map<String, Double>
)