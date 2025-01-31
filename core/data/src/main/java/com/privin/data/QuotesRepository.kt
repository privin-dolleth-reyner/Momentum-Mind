package com.privin.data

import com.privin.data.mappers.mapToDailyQuoteEntity
import com.privin.data.mappers.mapToQuote
import com.privin.data.models.Quote
import com.privin.database.dao.DailyQuoteDao
import com.privin.database.dao.QuoteDao
import com.privin.database.util.getToday
import com.privin.network.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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
        val today = getToday()

        return dailyQuoteDao.getDailyQuoteByDate(today).also {
            if (it.firstOrNull() == null) {
                syncDailyQuote()
            }
        }.map { it.mapToQuote() }
    }

    private suspend fun syncDailyQuote() {
        server.getDailyQuote().also {
            dailyQuoteDao.insertDailyQuote(it.mapToDailyQuoteEntity())
        }
    }

    override suspend fun updateFavorites(quote: Quote) {
        dailyQuoteDao.updateDailyQuote(quote.mapToDailyQuoteEntity())
    }

    override suspend fun getFavourites(): Flow<List<Quote>> {
        return dailyQuoteDao.getFavourites().map { it.map {entity-> entity.mapToQuote() } }
    }

}