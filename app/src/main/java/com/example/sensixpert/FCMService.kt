package com.example.sensixpert

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        saveFcmToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Notification is auto-displayed by Android when app is in background
        // This handles foreground notifications if needed
        Log.d("FCM", "Message received: ${message.notification?.title}")
    }

    private fun saveFcmToken(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .update("fcmToken", token)
            .addOnSuccessListener { Log.d("FCM", "Token saved for $userId") }
            .addOnFailureListener { Log.e("FCM", "Token save failed", it) }
    }

    companion object {
        fun refreshToken() {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    Log.d("FCM", "Token refreshed: $token")
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userId)
                        .update("fcmToken", token)
                }
        }
    }
}
