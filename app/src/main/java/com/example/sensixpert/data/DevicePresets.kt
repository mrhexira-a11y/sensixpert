package com.example.sensixpert.data

object DevicePresets {

    private val presets = mapOf(
        "SAMSUNG" to SensitivitySettings(
            general = 170,
            redDot = 162,
            scope2x = 150,
            scope4x = 138,
            awmScope = 124,
            freeLook = 175,
            buttonSize = 72,
            recommendedDpi = 420
        ),
        "XIAOMI" to SensitivitySettings(
            general = 175,
            redDot = 167,
            scope2x = 155,
            scope4x = 142,
            awmScope = 128,
            freeLook = 180,
            buttonSize = 68,
            recommendedDpi = 440
        ),
        "REALME" to SensitivitySettings(
            general = 172,
            redDot = 164,
            scope2x = 152,
            scope4x = 140,
            awmScope = 126,
            freeLook = 178,
            buttonSize = 70,
            recommendedDpi = 430
        ),
        "APPLE" to SensitivitySettings(
            general = 185,
            redDot = 177,
            scope2x = 164,
            scope4x = 150,
            awmScope = 135,
            freeLook = 190,
            buttonSize = 65,
            recommendedDpi = 460
        ),
        "OPPO" to SensitivitySettings(
            general = 168,
            redDot = 160,
            scope2x = 148,
            scope4x = 136,
            awmScope = 122,
            freeLook = 174,
            buttonSize = 71,
            recommendedDpi = 425
        ),
        "VIVO" to SensitivitySettings(
            general = 165,
            redDot = 157,
            scope2x = 145,
            scope4x = 133,
            awmScope = 120,
            freeLook = 172,
            buttonSize = 73,
            recommendedDpi = 415
        ),
        "HUAWEI" to SensitivitySettings(
            general = 172,
            redDot = 164,
            scope2x = 152,
            scope4x = 140,
            awmScope = 126,
            freeLook = 178,
            buttonSize = 69,
            recommendedDpi = 435
        )
    )

    fun getPreset(brand: String): SensitivitySettings {
        return presets[brand.uppercase()]
            ?: presets["SAMSUNG"]!! // Default fallback
    }

    fun getAvailableBrands(): List<String> {
        return presets.keys.toList()
    }
}
