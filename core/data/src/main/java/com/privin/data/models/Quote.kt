package com.privin.data.models

data class Quote(
    val quote: String,
    val author: String,
    val isFavorite: Boolean = false
)
