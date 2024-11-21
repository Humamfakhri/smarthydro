package com.humam.smarthydro.model

import com.google.firebase.Timestamp

data class FcmToken(
    val token: String = "",
    val timestamp: Timestamp = Timestamp.now()
)