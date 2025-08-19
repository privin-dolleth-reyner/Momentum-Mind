package com.privin.database

import com.privin.database.dao.DailyQuoteDao
import com.privin.database.model.DailyQuoteEntity
import kotlinx.coroutines.flow.Flow

interface MmLocalDatasource {
    suspend fun insertDailyQuote(quote: DailyQuoteEntity)
    suspend fun getDailyQuoteByDate(date: String): Flow<DailyQuoteEntity?>
    suspend fun updateDailyQuote(quote: DailyQuoteEntity)
    suspend fun getFavourites(): Flow<List<DailyQuoteEntity>>
}

internal class MmLocalDatasourceImpl(
    private val dailyQuoteDao: DailyQuoteDao
) : MmLocalDatasource {
    override suspend fun insertDailyQuote(quote: DailyQuoteEntity) {
        dailyQuoteDao.insertDailyQuote(quote)
    }
    override suspend fun getDailyQuoteByDate(date: String): Flow<DailyQuoteEntity?> {
        return dailyQuoteDao.getDailyQuoteByDate(date)
    }
    override suspend fun updateDailyQuote(quote: DailyQuoteEntity) {
        dailyQuoteDao.updateDailyQuote(quote)
    }
    override suspend fun getFavourites(): Flow<List<DailyQuoteEntity>> {
        return dailyQuoteDao.getFavourites()
    }
}