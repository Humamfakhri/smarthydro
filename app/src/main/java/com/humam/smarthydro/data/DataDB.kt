package com.humam.smarthydro.data

import com.google.firebase.database.FirebaseDatabase

class DataDB {
    private val database = FirebaseDatabase.getInstance()

    val dao = DataDaoImpl(database)

    companion object {
        const val WATER_LEVELS = "water-level"

        @Volatile
        var INSTANCE: DataDB? = null

        fun getInstance(): DataDB {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = DataDB()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}