package com.example.sensixpert

import android.app.Application

/**
 * Application class that initializes Firebase Cloud Messaging.
 * Must be registered in AndroidManifest.xml.
 */
class SensiXpertApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // FCM is auto-initialized via google-services.json
        // Token is saved to Firestore via FCMService
    }
}
