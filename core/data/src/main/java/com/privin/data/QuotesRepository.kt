package com.privin.data

import com.privin.data.mappers.mapToDailyQuoteEntity
import com.privin.data.mappers.mapToQuote
import com.privin.data.models.Quote
import com.privin.database.MmLocalDatasource
import com.privin.database.util.getToday
import com.privin.network.MmNetworkDatasource
import com.privin.network.model.QuoteNetworkData
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
            .combine(flow<QuoteNetworkData?> {
                try {
                    emit(syncDailyQuote())
                } catch (e: Exception) {
                    emit(null)
                }
            }, { local, network ->
                local?.mapToQuote() ?: network?.mapToQuote()
            })
            .flowOn(ioDispatcher)
            .map {
                if (it == null) {
                    Result.Error(NoInternetException)
                } else Result.Success(it)
            }
    }

    private suspend fun syncDailyQuote() = withContext(ioDispatcher) {
        try {
            return@withContext mmNetworkDatasource.getDailyQuote(getToday()).also {
                mmLocalDatasource.insertDailyQuote(it.mapToDailyQuoteEntity())
            }
        } catch (e: Exception) {
            throw e
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
    data class Error(val exception: Exception) : Result<Nothing>()
}

data object NoInternetException : Exception()