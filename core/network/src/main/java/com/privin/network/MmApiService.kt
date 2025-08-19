package com.privin.network

import com.privin.network.model.QuoteNetworkData
import retrofit2.Response
import retrofit2.http.GET

interface MmApiService {
    @GET("api/today")
    suspend fun getDailyQuote(): Response<List<QuoteNetworkData>>

}