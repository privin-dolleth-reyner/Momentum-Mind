package com.privin.database.di

import android.content.Context
import androidx.room.Room
import com.privin.database.MomentumMindDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): MomentumMindDatabase {
        return Room.databaseBuilder(
            context,
            MomentumMindDatabase::class.java,
            "momentum_mind_database"
        ).fallbackToDestructiveMigration().build()
    }
}