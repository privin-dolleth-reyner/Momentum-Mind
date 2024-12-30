package com.privin.network

import com.privin.network.model.QuoteData
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("api/{mode}")
    suspend fun getQuotes(
        @Path("mode") mode: String = "quotes",
    ): List<QuoteData>

    @GET("api/{mode}")
    suspend fun getDailyQuote(
        @Path("mode") mode: String = "today",
    ): List<QuoteData>

}