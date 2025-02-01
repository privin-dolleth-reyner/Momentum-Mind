package com.privin.database.di

import com.privin.database.MomentumMindDatabase
import com.privin.database.dao.DailyQuoteDao
import com.privin.database.dao.QuoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {

    @Provides
    fun provideQuoteDao(database: MomentumMindDatabase): QuoteDao {
        return database.quoteDao()
    }

    @Provides
    fun provideDailyQuoteDao(database: MomentumMindDatabase): DailyQuoteDao {
        return database.dailyQuoteDao()

    }
}