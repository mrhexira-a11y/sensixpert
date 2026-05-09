package com.example.sensixpert.data

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

data class DeviceSpecs(
    val ramGB: Double,
    val screenWidthPx: Int,
    val screenHeightPx: Int,
    val dpi: Int,
    val cpuCores: Int,
    val refreshRate: Float,
    val deviceBrand: String,
    val deviceModel: String
)

object DeviceAnalyzer {

    fun analyze(context: Context): DeviceSpecs {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        val ramGB = memInfo.totalMem / (1024.0 * 1024.0 * 1024.0)

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()

        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)

        val screenWidthPx = displayMetrics.widthPixels
        val screenHeightPx = displayMetrics.heightPixels
        val dpi = displayMetrics.densityDpi

        val cpuCores = Runtime.getRuntime().availableProcessors()

        @Suppress("DEPRECATION")
        val refreshRate = windowManager.defaultDisplay.refreshRate

        val deviceBrand = Build.MANUFACTURER.uppercase()
        val deviceModel = Build.MODEL

        return DeviceSpecs(
            ramGB = ramGB,
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            dpi = dpi,
            cpuCores = cpuCores,
            refreshRate = refreshRate,
            deviceBrand = deviceBrand,
            deviceModel = deviceModel
        )
    }
}
