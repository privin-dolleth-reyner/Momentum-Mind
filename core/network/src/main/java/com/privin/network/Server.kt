package com.privin.network

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

interface Server {
    suspend fun getTodayPrice(): Double
}

sealed class ApiError : Exception() {
    data object NotFound : ApiError()
}

class ServerImpl @Inject constructor(
    moshi: dagger.Lazy<Moshi>,
    client: dagger.Lazy<OkHttpClient>) : Server {

    private val api by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client.get())
            .addConverterFactory(MoshiConverterFactory.create(moshi.get()))
            .build()
            .create(ApiService::class.java)
    }


    override suspend fun getTodayPrice(): Double {
        return api.getLiveRates().rates["mcx_gold"] ?: throw ApiError.NotFound
    }

}