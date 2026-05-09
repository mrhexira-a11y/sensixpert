package com.example.sensixpert.data

/**
 * Predefined sensitivity settings for each Free Fire gun.
 * All values in range 100–200 for sensitivity, 400–900 for DPI.
 */
data class GunSensitivity(
    val gunName: String,
    val emoji: String,
    val category: String,       // "Pistol", "AR", "SMG", "Shotgun", "Sniper"
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val awmScope: Int,
    val freeLook: Int,
    val dpi: Int
)

object GunSensitivityData {

    val guns: List<GunSensitivity> = listOf(
        // ── Pistol ──
        GunSensitivity(
            gunName = "Desert Eagle",
            emoji = "🔫",
            category = "Pistol",
            general = 185,
            redDot = 170,
            scope2x = 155,
            scope4x = 140,
            awmScope = 120,
            freeLook = 190,
            dpi = 680
        ),

        // ── Assault Rifles ──
        GunSensitivity(
            gunName = "AK",
            emoji = "🔫",
            category = "AR",
            general = 165,
            redDot = 158,
            scope2x = 145,
            scope4x = 130,
            awmScope = 115,
            freeLook = 175,
            dpi = 620
        ),
        GunSensitivity(
            gunName = "Groza",
            emoji = "🔫",
            category = "AR",
            general = 172,
            redDot = 163,
            scope2x = 150,
            scope4x = 135,
            awmScope = 118,
            freeLook = 180,
            dpi = 650
        ),
        GunSensitivity(
            gunName = "AN94",
            emoji = "🔫",
            category = "AR",
            general = 168,
            redDot = 160,
            scope2x = 148,
            scope4x = 132,
            awmScope = 116,
            freeLook = 178,
            dpi = 640
        ),
        GunSensitivity(
            gunName = "M4A1",
            emoji = "🔫",
            category = "AR",
            general = 175,
            redDot = 165,
            scope2x = 152,
            scope4x = 138,
            awmScope = 120,
            freeLook = 182,
            dpi = 660
        ),

        // ── SMGs ──
        GunSensitivity(
            gunName = "MP40",
            emoji = "🔫",
            category = "SMG",
            general = 190,
            redDot = 180,
            scope2x = 165,
            scope4x = 148,
            awmScope = 125,
            freeLook = 195,
            dpi = 720
        ),
        GunSensitivity(
            gunName = "UMP",
            emoji = "🔫",
            category = "SMG",
            general = 182,
            redDot = 174,
            scope2x = 160,
            scope4x = 143,
            awmScope = 122,
            freeLook = 188,
            dpi = 700
        ),
        GunSensitivity(
            gunName = "Vector",
            emoji = "🔫",
            category = "SMG",
            general = 195,
            redDot = 185,
            scope2x = 168,
            scope4x = 150,
            awmScope = 128,
            freeLook = 198,
            dpi = 750
        ),

        // ── Shotguns ──
        GunSensitivity(
            gunName = "M1887",
            emoji = "🔫",
            category = "Shotgun",
            general = 200,
            redDot = 190,
            scope2x = 172,
            scope4x = 155,
            awmScope = 130,
            freeLook = 200,
            dpi = 850
        ),
        GunSensitivity(
            gunName = "M1014",
            emoji = "🔫",
            category = "Shotgun",
            general = 198,
            redDot = 188,
            scope2x = 170,
            scope4x = 152,
            awmScope = 128,
            freeLook = 196,
            dpi = 830
        ),
        GunSensitivity(
            gunName = "MAG-7",
            emoji = "🔫",
            category = "Shotgun",
            general = 192,
            redDot = 184,
            scope2x = 167,
            scope4x = 150,
            awmScope = 126,
            freeLook = 194,
            dpi = 800
        ),

        // ── Snipers ──
        GunSensitivity(
            gunName = "AWM",
            emoji = "🎯",
            category = "Sniper",
            general = 130,
            redDot = 125,
            scope2x = 115,
            scope4x = 105,
            awmScope = 100,
            freeLook = 150,
            dpi = 450
        ),
        GunSensitivity(
            gunName = "Woodpecker",
            emoji = "🎯",
            category = "Sniper",
            general = 145,
            redDot = 138,
            scope2x = 128,
            scope4x = 118,
            awmScope = 108,
            freeLook = 160,
            dpi = 520
        ),
        GunSensitivity(
            gunName = "AC80",
            emoji = "🎯",
            category = "Sniper",
            general = 140,
            redDot = 132,
            scope2x = 122,
            scope4x = 112,
            awmScope = 104,
            freeLook = 155,
            dpi = 490
        ),
        GunSensitivity(
            gunName = "SVD",
            emoji = "🎯",
            category = "Sniper",
            general = 148,
            redDot = 140,
            scope2x = 130,
            scope4x = 120,
            awmScope = 110,
            freeLook = 162,
            dpi = 540
        )
    )
}
