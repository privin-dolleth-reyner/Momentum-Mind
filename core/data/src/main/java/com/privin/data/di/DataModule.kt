package com.privin.data.di

import com.privin.data.QuotesRepository
import com.privin.data.QuotesRepositoryImpl
import com.privin.database.MmLocalDatasource
import com.privin.network.MmNetworkDatasource
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
    fun provideQuotesRepository(
        mmNetworkDatasource: MmNetworkDatasource,
        mmLocalDatasource: MmLocalDatasource
    ): QuotesRepository = QuotesRepositoryImpl(mmNetworkDatasource,mmLocalDatasource)

}