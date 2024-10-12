package com.privin.data

import com.privin.data.models.Quote
import com.privin.data.models.mapToQuote
import com.privin.network.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface QuotesRepository {

    suspend fun getDailyQuote(): Flow<Quote>
}

class QuotesRepositoryImpl(private val server: Server) : QuotesRepository {
    override suspend fun getDailyQuote(): Flow<Quote> {
        return flowOf(server.getDailyQuote().mapToQuote())
    }

}