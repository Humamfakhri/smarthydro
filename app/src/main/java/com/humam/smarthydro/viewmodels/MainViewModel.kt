package com.humam.smarthydro.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.humam.smarthydro.data.DataDB
import com.humam.smarthydro.data.DataDao
import com.humam.smarthydro.model.FcmToken
import com.humam.smarthydro.model.WaterLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val dataDao: DataDao = DataDB.getInstance().dao

    private val _error: MutableStateFlow<String?> = MutableStateFlow(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _waterLevels: MutableStateFlow<List<WaterLevel>> = MutableStateFlow(emptyList())
    val waterLevels: StateFlow<List<WaterLevel>> = _waterLevels.asStateFlow()

    suspend fun getWaterLevels() {
        _loading.value = true
        try {
            dataDao.getWaterLevels().collect {
                _waterLevels.value = it
            }
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _loading.value = false
        }
    }

    suspend fun getWaterLevelById(id: Int) {
        _loading.value = true
        try {
            dataDao.getWaterLevelById(id).collect {
                _waterLevels.value = listOf(it)
            }
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _loading.value = false
        }
    }
}