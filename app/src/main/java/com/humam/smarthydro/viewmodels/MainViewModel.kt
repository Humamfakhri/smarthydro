package com.humam.smarthydro.viewmodels

import androidx.lifecycle.ViewModel
import com.humam.smarthydro.data.DataDB
import com.humam.smarthydro.data.DataDao
import com.humam.smarthydro.model.WaterLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val dataDao: DataDao = DataDB.getInstance().dao

    private val _waterLevels: MutableStateFlow<List<WaterLevel>> = MutableStateFlow(emptyList())
    val waterLevels: StateFlow<List<WaterLevel>> = _waterLevels.asStateFlow()

    suspend fun getWaterLevels() {
        dataDao.getWaterLevels().collect {
            _waterLevels.value = it
        }
    }
}