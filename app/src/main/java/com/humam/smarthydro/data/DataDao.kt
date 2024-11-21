package com.humam.smarthydro.data

import com.humam.smarthydro.model.FcmToken
import com.humam.smarthydro.model.WaterLevel
import kotlinx.coroutines.flow.Flow

interface DataDao {
    suspend fun getWaterLevels(): Flow<List<WaterLevel>>
    suspend fun getWaterLevelById(id: Int): Flow<WaterLevel>
    fun addToken(token: FcmToken)
    suspend fun getAllTokens(): Flow<List<FcmToken>>
}