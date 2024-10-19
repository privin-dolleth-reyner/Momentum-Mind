package com.privin.data

import com.privin.data.mappers.mapToDailyQuoteEntity
import com.privin.data.mappers.mapToQuote
import com.privin.data.models.Quote
import com.privin.database.dao.DailyQuoteDao
import com.privin.database.dao.QuoteDao
import com.privin.database.util.getToday
import com.privin.network.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface QuotesRepository {

    suspend fun getDailyQuote(): Flow<Quote>
    suspend fun addQuoteToFavorites(quote: Quote)
}

class QuotesRepositoryImpl(
    private val server: Server,
    private val dailyQuoteDao: DailyQuoteDao,
    private val quoteDao: QuoteDao
) : QuotesRepository {
    override suspend fun getDailyQuote(): Flow<Quote> {
        val date = getToday()
        if (dailyQuoteDao.isDailyQuoteAvailable(date).not()) {
            val quoteFromServer = server.getDailyQuote()
            dailyQuoteDao.insertDailyQuote(quoteFromServer.mapToDailyQuoteEntity())
        }
        val quote = dailyQuoteDao.getDailyQuoteByDate(date)
        return quote.map { it.mapToQuote() }
    }

    override suspend fun addQuoteToFavorites(quote: Quote) {
        dailyQuoteDao.updateDailyQuote(quote.mapToDailyQuoteEntity())
    }

}