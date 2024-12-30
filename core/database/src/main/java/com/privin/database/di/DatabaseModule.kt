package com.privin.database.di

import android.content.Context
import androidx.room.Room
import com.privin.database.GPTDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GPTDatabase {
        return Room.databaseBuilder(
            context,
            GPTDatabase::class.java,
            "gpt_database"
        ).fallbackToDestructiveMigration().build()
    }
}