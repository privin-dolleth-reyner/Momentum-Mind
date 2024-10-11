package com.privin.data.di

import com.privin.data.GoldRepository
import com.privin.data.GoldRepositoryImpl
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
    fun provideGoldRepository(server: Server): GoldRepository = GoldRepositoryImpl(server)

}