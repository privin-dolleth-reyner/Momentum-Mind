package com.privin.data.di

import com.privin.data.QuotesRepository
import com.privin.data.QuotesRepositoryImpl
import com.privin.network.Server
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule{
    @Provides
    @Singleton
    fun provideQuotesRepository(server: Server): QuotesRepository = QuotesRepositoryImpl(server)

}