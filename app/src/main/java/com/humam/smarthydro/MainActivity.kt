package com.humam.smarthydro

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.humam.smarthydro.model.WaterLevel
import com.humam.smarthydro.ui.theme.SmartHydroTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("NotificationPermission", "Permission granted")
        } else {
            Log.d("NotificationPermission", "Permission denied")
        }
    }

    private fun requestNotificationPermission() {
        // Check if POST_NOTIFICATIONS permission has been granted
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request permission to use ActivityResultLauncher
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun startListeningForWaterLevelChanges(context: Context) {
        val database =
            FirebaseDatabase.getInstance("https://smarthydro-287a5-default-rtdb.asia-southeast1.firebasedatabase.app")
        val ref = database.getReference("water-level")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Create a list to store valid WaterLevel data
                val waterLevels = mutableListOf<WaterLevel>()

                // Iterate over each child in the snapshot
                snapshot.children.forEach { childSnapshot ->
                    try {
                        // Try to parse each element into the WaterLevel model.
                        val waterLevel = childSnapshot.getValue(WaterLevel::class.java)
                        if (waterLevel != null) {
                            waterLevels.add(waterLevel)
                        }
                    } catch (e: Exception) {
                        Log.e("Firebase", "Invalid data format: ${e.message}")
                    }
                }

                // Check if there is a condition level = 0 and wet = "NO"
                val isAlertCondition = waterLevels.any { it.level == "0" && it.wet == "NO" }

                if (isAlertCondition) {
                    sendNotification(
                        context,
                        "Water Alert!",
                        "One of the water levels is 0 and the area is not wet."
                    )
                }

                Log.d("Firebase", "Valid data: $waterLevels")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Database error: ${error.message}")
            }
        })
    }

    fun sendNotification(context: Context, title: String, message: String) {
        val channelId = "water_level_channel"

        // Intent to open the application
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create a Notification Channel for Android 8.0+ if not already created
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Water Level Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build and display the notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Open app on click
            .setAutoCancel(true) // Remove notification after click

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(1, builder.build())
        } else {
            Log.w("Notification", "Notification permission not granted.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Request permission when app is launched
        requestNotificationPermission()
        getWaterLevelData { waterLevels ->
            setContent {
                SmartHydroTheme() {
                    MainScreen(waterLevels)
                }
            }
        }
        // Start listening for data changes
        startListeningForWaterLevelChanges(this)
    }
}