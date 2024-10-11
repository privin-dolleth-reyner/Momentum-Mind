package com.privin.data

import com.privin.network.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface GoldRepository {

    suspend fun getTodayPrice(): Flow<Double>
}

class GoldRepositoryImpl(private val server: Server) : GoldRepository {
    override suspend fun getTodayPrice(): Flow<Double> {
        return flowOf(server.getTodayPrice())
    }

}