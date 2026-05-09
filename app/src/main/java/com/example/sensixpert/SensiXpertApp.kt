package com.example.sensixpert

import android.app.Application
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Application class that initializes OneSignal SDK on app launch.
 * Must be registered in AndroidManifest.xml.
 */
class SensiXpertApp : Application() {

    companion object {
        // OneSignal App ID from dashboard
        const val ONESIGNAL_APP_ID = "e83708b1-ef26-4755-9309-d5aeb64c734e"
    }

    override fun onCreate() {
        super.onCreate()

        // Enable verbose logging for debugging (remove in production)
        OneSignal.Debug.logLevel = LogLevel.VERBOSE

        // Initialize OneSignal with app ID
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)

        // Request push notification permission
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(false)
        }
    }
}
