package com.privin.network

import com.privin.network.model.QuoteNetworkData
import java.net.UnknownHostException
import javax.inject.Inject

interface MmNetworkDatasource {
    suspend fun getDailyQuote(today: String): NetworkResult<QuoteNetworkData>
}

class MmNetworkDatasourceImpl @Inject constructor(
    val api: MmApiService
) : MmNetworkDatasource {

    override suspend fun getDailyQuote(today: String): NetworkResult<QuoteNetworkData> = try {
        val response = api.getDailyQuote()
        if (response.isSuccessful && !response.body().isNullOrEmpty()) {
            val quoteData = response.body()!!
            NetworkResult.Success(quoteData[0])
        }else NetworkResult.Error(NetworkError.ErrorResponse(code = response.code(), error = response.message()))
    }catch (_: UnknownHostException){
        NetworkResult.Error(NetworkError.NoInternet)
    }catch (e: Exception){
        NetworkResult.Error(NetworkError.Unexpected(e.message))
    }

}

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val apiError: NetworkError) : NetworkResult<Nothing>()
}

sealed class NetworkError {
    object NoInternet : NetworkError()
    data class Unexpected(val msg: String? = null) : NetworkError()
    data class ErrorResponse(val code: Int, val error: String? = null) : NetworkError()
}