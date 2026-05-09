package com.example.sensixpert.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.sensixpert.data.ConnectionState
import com.example.sensixpert.data.DeviceAnalyzer
import com.example.sensixpert.data.SensitivityCalculator
import com.example.sensixpert.data.SensitivitySettings
import com.example.sensixpert.service.GamingBoosterService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BoosterViewModel : ViewModel() {

    var connectionState by mutableStateOf(ConnectionState.DISCONNECTED)
        private set

    var isUltraBoostActive by mutableStateOf(false)
        private set

    var isHeadshotModeActive by mutableStateOf(false)
        private set

    var latencyText by mutableStateOf("45ms")
        private set

    var sensitivitySettings by mutableStateOf<SensitivitySettings?>(null)
        private set

    var ultraBoostStatusText by mutableStateOf("")
        private set

    // Connection timer
    var timerText by mutableStateOf("00:00:00")
        private set

    private var timerJob: Job? = null
    private var timerSeconds = 0L

    fun onConnectPressed(activity: Activity) {
        when (connectionState) {
            ConnectionState.DISCONNECTED -> startConnection(activity)
            ConnectionState.CONNECTED -> startDisconnection(activity)
            else -> { /* Ignore during transition */ }
        }
    }

    private fun startConnection(activity: Activity) {
        connectionState = ConnectionState.CONNECTING

        viewModelScope.launch {
            // Simulate connection animation (3 seconds)
            delay(2500)
            connectionState = ConnectionState.CONNECTED
            latencyText = "${(20..45).random()}ms"

            // Start connection timer
            startTimer()

            // Start foreground service for notification
            startBoosterService(activity)
        }
    }

    private fun startDisconnection(activity: Activity) {
        connectionState = ConnectionState.DISCONNECTING

        viewModelScope.launch {
            delay(1500)
            connectionState = ConnectionState.DISCONNECTED
            isUltraBoostActive = false
            ultraBoostStatusText = ""
            latencyText = "45ms"

            // Stop and reset timer
            stopTimer()

            // Stop foreground service
            stopBoosterService(activity)
        }
    }

    fun disconnectFromNotification(activity: Activity) {
        if (connectionState == ConnectionState.CONNECTED) {
            startDisconnection(activity)
        }
    }

    fun toggleUltraBoost(context: Context) {
        if (connectionState != ConnectionState.CONNECTED) return

        isUltraBoostActive = !isUltraBoostActive

        if (isUltraBoostActive) {
            viewModelScope.launch {
                ultraBoostStatusText = "Clearing background processes..."
                delay(1000)
                ultraBoostStatusText = "Optimizing performance..."
                delay(1000)
                ultraBoostStatusText = "Ultra Boost Active ✅"
                latencyText = "${(15..30).random()}ms"
            }
        } else {
            ultraBoostStatusText = ""
            latencyText = "${(30..50).random()}ms"
        }
    }

    fun toggleHeadshotMode(context: Context) {
        isHeadshotModeActive = !isHeadshotModeActive

        if (isHeadshotModeActive && sensitivitySettings == null) {
            val specs = DeviceAnalyzer.analyze(context)
            sensitivitySettings = SensitivityCalculator.calculate(specs)
        }
    }



    private fun startTimer() {
        timerJob?.cancel()
        timerSeconds = 0L
        timerText = "00:00:00"
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                timerSeconds++
                val hours = timerSeconds / 3600
                val minutes = (timerSeconds % 3600) / 60
                val seconds = timerSeconds % 60
                timerText = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        timerSeconds = 0L
        timerText = "00:00:00"
    }

    private fun startBoosterService(context: Context) {
        val intent = Intent(context, GamingBoosterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun stopBoosterService(context: Context) {
        val intent = Intent(context, GamingBoosterService::class.java)
        context.stopService(intent)
    }
}
