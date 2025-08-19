package com.privin.network

import com.privin.network.model.QuoteNetworkData
import javax.inject.Inject

interface MmNetworkDatasource {
    suspend fun getDailyQuote(today: String): QuoteNetworkData
}

class MmNetworkDatasourceImpl @Inject constructor(
    val api: MmApiService
) : MmNetworkDatasource {

    override suspend fun getDailyQuote(today: String): QuoteNetworkData {
        try {
            val response = api.getDailyQuote()
            if (response.isSuccessful) {
                val quoteData = response.body()
                if (quoteData != null) {
                    return quoteData[0]
                }
            }
            throw HttpException(code = response.code(), msg = response.message())
        }catch (e: Exception){
            throw e
        }
    }
}

class HttpException(val code: Int, val msg: String): Exception()