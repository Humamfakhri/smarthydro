package com.humam.smarthydro.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.humam.smarthydro.model.FcmToken
import com.humam.smarthydro.model.WaterLevel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class DataDaoImpl(val db: FirebaseDatabase) : DataDao {
    override suspend fun getWaterLevels(): Flow<MutableList<WaterLevel>> = callbackFlow {
        val listener = db.getReference(DataDB.WATER_LEVELS)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val waterLevels = mutableListOf<WaterLevel>()
                    for (data in snapshot.children) {
                        val level = data.child("level").getValue(String::class.java) ?: ""
                        val wet = data.child("wet").getValue(String::class.java) ?: ""
                        waterLevels.add(WaterLevel(level, wet))
                    }
                    trySend(waterLevels)

                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

        awaitClose {
            db.getReference(DataDB.WATER_LEVELS).removeEventListener(listener)
        }
    }

    override suspend fun getWaterLevelById(id: Int): Flow<WaterLevel> = callbackFlow {
        val listener = db.getReference(DataDB.WATER_LEVELS).child(id.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val level = snapshot.child("level").getValue(String::class.java) ?: ""
                    val wet = snapshot.child("wet").getValue(String::class.java) ?: ""
                    trySend(WaterLevel(level, wet))
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

        awaitClose {
            db.getReference(DataDB.WATER_LEVELS).child(id.toString()).removeEventListener(listener)
        }
    }

    override fun addToken(token: FcmToken) {
        db.getReference(DataDB.FCM_TOKEN).push().setValue(token)
    }

    override suspend fun getAllTokens(): Flow<MutableList<FcmToken>> = callbackFlow {
        val listener = db.getReference(DataDB.FCM_TOKEN)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tokens = mutableListOf<FcmToken>()
                    for (data in snapshot.children) {
                        val token = data.child("token").getValue(String::class.java) ?: ""
                        tokens.add(FcmToken(token))
                    }
                    trySend(tokens)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

        awaitClose {
            db.getReference(DataDB.FCM_TOKEN).removeEventListener(listener)
        }
    }
}