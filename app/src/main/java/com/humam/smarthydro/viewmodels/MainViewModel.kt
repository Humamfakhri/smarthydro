package com.humam.smarthydro.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.humam.smarthydro.model.WaterLevel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel : ViewModel() {

    companion object {
        const val WATER_LEVELS = "water-level"
    }

    private val db = FirebaseDatabase.getInstance()

    private val _waterLevels: Flow<List<WaterLevel>> = callbackFlow {
        val dataRef = db.getReference(WATER_LEVELS)
        val listener = dataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(getWaterLevels(snapshot))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose { dataRef.removeEventListener(listener) }
    }

    val waterLevels: StateFlow<List<WaterLevel>> = _waterLevels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    private fun getWaterLevels(snapshot: DataSnapshot): List<WaterLevel> {
        val waterLevels = mutableListOf<WaterLevel>()
        snapshot.children.forEach { data ->
            val waterLevel = data.getValue<WaterLevel>()
            if (waterLevel != null) {
                waterLevels.add(waterLevel)
            }
        }
        return waterLevels
    }
}