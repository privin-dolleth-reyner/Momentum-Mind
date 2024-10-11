package com.privin.network

import com.privin.network.model.LiveRatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v1/metal/authority")
    suspend fun getLiveRates(
        @Query("api_key") apiKey: String = BuildConfig.API_KEY,
        @Query("authority") authority: String = "mcx",
        @Query("currency") currency: String = "INR",
        @Query("unit") unit: String = "g"
    ): LiveRatesResponse

}