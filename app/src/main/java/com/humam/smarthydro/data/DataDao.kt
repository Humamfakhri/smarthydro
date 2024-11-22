package com.humam.smarthydro.data

import com.humam.smarthydro.model.WaterLevel
import kotlinx.coroutines.flow.Flow

interface DataDao {
    suspend fun getWaterLevels(): Flow<List<WaterLevel>>
}