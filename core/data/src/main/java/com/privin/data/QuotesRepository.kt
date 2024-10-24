package com.privin.data

import android.util.Log
import com.privin.data.mappers.mapToDailyQuoteEntity
import com.privin.data.mappers.mapToQuote
import com.privin.data.models.Quote
import com.privin.database.dao.DailyQuoteDao
import com.privin.database.dao.QuoteDao
import com.privin.database.util.getToday
import com.privin.network.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

interface QuotesRepository {

    suspend fun getDailyQuote(): Flow<Quote>
    suspend fun updateFavorites(quote: Quote)
    suspend fun getFavourites(): Flow<List<Quote>>
}

class QuotesRepositoryImpl(
    private val server: Server,
    private val dailyQuoteDao: DailyQuoteDao,
    private val quoteDao: QuoteDao
) : QuotesRepository {
    override suspend fun getDailyQuote(): Flow<Quote> {
        val date = getToday()
        val quote = if (dailyQuoteDao.isDailyQuoteAvailable(date).not() && syncDailyQuote()) {
            dailyQuoteDao.getDailyQuoteByDate(date)
        } else if (dailyQuoteDao.hasQuotes()) {
            dailyQuoteDao.getLastAvailable()
        } else emptyFlow()
        return quote.map { it.mapToQuote() }
    }

    private suspend fun syncDailyQuote(): Boolean {
        var result = false
        try {
            val quoteFromServer = server.getDailyQuote()
            dailyQuoteDao.insertDailyQuote(quoteFromServer.mapToDailyQuoteEntity())
            result = true
        } catch (e: Exception) {
            Log.e(this::class.simpleName, "syncDailyQuote: ", e)
            result = false
        }
        return result
    }

    override suspend fun updateFavorites(quote: Quote) {
        dailyQuoteDao.updateDailyQuote(quote.mapToDailyQuoteEntity())
    }

    override suspend fun getFavourites(): Flow<List<Quote>> {
        return dailyQuoteDao.getFavourites().map { it.map {entity-> entity.mapToQuote() } }
    }

}