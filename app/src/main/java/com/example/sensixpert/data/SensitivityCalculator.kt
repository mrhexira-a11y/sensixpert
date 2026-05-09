package com.example.sensixpert.data

data class SensitivitySettings(
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val awmScope: Int,
    val freeLook: Int,
    val buttonSize: Int,
    val recommendedDpi: Int
)

object SensitivityCalculator {

    /**
     * Calculate optimal sensitivity based on device specifications.
     *
     * Weighting:
     * - RAM (40%): Higher RAM = smoother rendering = can handle higher sensitivity
     * - DPI (25%): Higher DPI = finer touch resolution = lower sensitivity needed
     * - CPU Cores (20%): More cores = better processing = stable at higher sens
     * - Refresh Rate (15%): Higher refresh = smoother input = can fine-tune sensitivity
     */
    fun calculate(specs: DeviceSpecs): SensitivitySettings {
        // Normalize each factor to a 0.0 – 1.0 scale
        val ramScore = normalizeRange(specs.ramGB, minVal = 1.0, maxVal = 12.0)
        val dpiScore = 1.0 - normalizeRange(specs.dpi.toDouble(), minVal = 160.0, maxVal = 640.0) // inverse
        val cpuScore = normalizeRange(specs.cpuCores.toDouble(), minVal = 2.0, maxVal = 8.0)
        val refreshScore = normalizeRange(specs.refreshRate.toDouble(), minVal = 60.0, maxVal = 120.0)

        // Weighted composite score (0.0 – 1.0)
        val composite = (ramScore * 0.40) + (dpiScore * 0.25) + (cpuScore * 0.20) + (refreshScore * 0.15)

        // Map composite to sensitivity ranges (100–200 range for all)
        val general = mapToSensitivity(composite, baseMin = 130, baseMax = 195)
        val redDot = mapToSensitivity(composite, baseMin = 125, baseMax = 190)
        val scope2x = mapToSensitivity(composite, baseMin = 120, baseMax = 185)
        val scope4x = mapToSensitivity(composite, baseMin = 115, baseMax = 178)
        val awmScope = mapToSensitivity(composite, baseMin = 105, baseMax = 170)
        val freeLook = mapToSensitivity(composite, baseMin = 135, baseMax = 192)

        // Button size: generate values in 55-65% range
        val buttonSize = (65 - (composite * 10).toInt()).coerceIn(55, 65)

        // Recommended DPI
        val recommendedDpi = calculateRecommendedDpi(specs)

        return SensitivitySettings(
            general = general,
            redDot = redDot,
            scope2x = scope2x,
            scope4x = scope4x,
            awmScope = awmScope,
            freeLook = freeLook,
            buttonSize = buttonSize,
            recommendedDpi = recommendedDpi
        )
    }

    fun calculateForPreset(brand: String): SensitivitySettings {
        return DevicePresets.getPreset(brand)
    }

    private fun normalizeRange(value: Double, minVal: Double, maxVal: Double): Double {
        return ((value - minVal) / (maxVal - minVal)).coerceIn(0.0, 1.0)
    }

    private fun mapToSensitivity(composite: Double, baseMin: Int, baseMax: Int): Int {
        return (baseMin + (composite * (baseMax - baseMin))).toInt().coerceIn(100, 200)
    }

    private fun calculateRecommendedDpi(specs: DeviceSpecs): Int {
        // Higher resolution screens benefit from higher DPI; lower RAM devices should use lower
        val resolutionFactor = (specs.screenWidthPx * specs.screenHeightPx).toDouble()
        val normalizedRes = normalizeRange(resolutionFactor, minVal = 720.0 * 1280, maxVal = 1440.0 * 3200)
        val ramFactor = normalizeRange(specs.ramGB, minVal = 2.0, maxVal = 8.0)
        val dpiBase = 350 + ((normalizedRes * 0.6 + ramFactor * 0.4) * 150).toInt()
        return dpiBase.coerceIn(300, 550)
    }
}
