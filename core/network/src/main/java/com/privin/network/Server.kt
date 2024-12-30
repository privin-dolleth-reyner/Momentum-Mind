package com.privin.network

import com.privin.network.model.QuoteData
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

interface Server {
    suspend fun getQuotes(): List<QuoteData>
    suspend fun getDailyQuote(): QuoteData
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


    override suspend fun getQuotes(): List<QuoteData> {
        return api.getQuotes()
    }

    override suspend fun getDailyQuote(): QuoteData {
        return api.getDailyQuote().first()
    }

}