package com.privin.data

import com.privin.data.mappers.mapToDailyQuoteEntity
import com.privin.data.mappers.mapToQuote
import com.privin.data.models.Quote
import com.privin.database.MmLocalDatasource
import com.privin.database.util.getToday
import com.privin.network.MmNetworkDatasource
import com.privin.network.NetworkError
import com.privin.network.NetworkResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface QuotesRepository {

    suspend fun getDailyQuote(): Flow<Result<Quote>>
    suspend fun updateFavorites(quote: Quote)
    suspend fun getFavourites(): Flow<List<Quote>>
}

class QuotesRepositoryImpl(
    private val mmNetworkDatasource: MmNetworkDatasource,
    private val mmLocalDatasource: MmLocalDatasource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : QuotesRepository {

    override suspend fun getDailyQuote(): Flow<Result<Quote>> = withContext(ioDispatcher) {
        val today = getToday()
        mmLocalDatasource.getDailyQuoteByDate(today)
            .combine(
                flow<Result<Quote>> {
                    emit(syncDailyQuote())
                }
            ) { local, network ->
                local?.let {
                    return@combine Result.Success(it.mapToQuote())
                }
                network
            }
            .flowOn(ioDispatcher)
    }

    private suspend fun syncDailyQuote(): Result<Quote> = withContext(ioDispatcher) {
        try {
            val response = mmNetworkDatasource.getDailyQuote(getToday()).also {
                if (it is NetworkResult.Success) {
                    mmLocalDatasource.insertDailyQuote(it.data.mapToDailyQuoteEntity())
                }
            }
            if (response is NetworkResult.Success) {
                Result.Success(response.data.mapToQuote())
            } else {
                val error = (response as NetworkResult.Error).apiError
                when(error){
                    is NetworkError.ErrorResponse -> Result.Failure(Error.ErrorResponse(error.code, error.error))
                    is NetworkError.NoInternet -> Result.Failure(Error.NoInternet)
                    is NetworkError.Unexpected -> Result.Failure(Error.Unexpected(error.msg))
                }
            }
        } catch (e: Exception) {
            Result.Failure(Error.Unexpected(e.message))
        }
    }

    override suspend fun updateFavorites(quote: Quote) = withContext(ioDispatcher) {
        mmLocalDatasource.updateDailyQuote(quote.mapToDailyQuoteEntity())
    }

    override suspend fun getFavourites(): Flow<List<Quote>> {
        return mmLocalDatasource.getFavourites()
            .map { it.map { entity -> entity.mapToQuote() } }
    }

}

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val error: Error) : Result<Nothing>()
}

sealed class Error {
    object NoInternet : Error()
    data class Unexpected(val msg: String? = null) : Error()
    data class ErrorResponse(val code: Int, val error: String? = null) : Error()
}