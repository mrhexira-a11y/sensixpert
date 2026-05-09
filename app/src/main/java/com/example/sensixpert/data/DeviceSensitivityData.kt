package com.example.sensixpert.data

/**
 * All mobile brands and their devices with recommended sensitivity settings.
 * Sensitivity: 100–200 | DPI: 400–900
 */
data class DeviceSensiInfo(
    val deviceName: String,
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val awmScope: Int,
    val freeLook: Int,
    val dpi: Int
)

data class BrandInfo(
    val brandName: String,
    val emoji: String,
    val devices: List<DeviceSensiInfo>
)

object DeviceSensitivityData {

    val brands: List<BrandInfo> = listOf(

        // ═══════════════════ SAMSUNG ═══════════════════
        BrandInfo("Samsung", "📱", listOf(
            DeviceSensiInfo("Galaxy S20", 178, 170, 158, 145, 128, 185, 720),
            DeviceSensiInfo("Galaxy S21", 182, 174, 162, 148, 132, 188, 740),
            DeviceSensiInfo("Galaxy S22", 186, 178, 165, 152, 135, 192, 760),
            DeviceSensiInfo("Galaxy S23", 190, 182, 168, 155, 138, 195, 800),
            DeviceSensiInfo("Galaxy S24", 195, 186, 172, 158, 142, 198, 850),
            DeviceSensiInfo("Galaxy A14", 140, 132, 122, 112, 105, 148, 480),
            DeviceSensiInfo("Galaxy A15", 142, 134, 124, 114, 106, 150, 490),
            DeviceSensiInfo("Galaxy A24", 155, 148, 136, 125, 112, 162, 550),
            DeviceSensiInfo("Galaxy A34", 162, 155, 142, 130, 118, 168, 580),
            DeviceSensiInfo("Galaxy A54", 170, 162, 150, 138, 124, 175, 640)
        )),

        // ═══════════════════ XIAOMI ═══════════════════
        BrandInfo("Xiaomi", "📱", listOf(
            DeviceSensiInfo("Redmi 10", 145, 138, 126, 115, 106, 152, 500),
            DeviceSensiInfo("Redmi 11", 148, 140, 128, 118, 108, 155, 510),
            DeviceSensiInfo("Redmi 12", 152, 144, 132, 120, 110, 158, 530),
            DeviceSensiInfo("Redmi 13", 155, 146, 134, 122, 112, 162, 540),
            DeviceSensiInfo("Redmi Note 10", 158, 150, 138, 126, 115, 165, 560),
            DeviceSensiInfo("Redmi Note 11", 162, 154, 142, 130, 118, 168, 580),
            DeviceSensiInfo("Redmi Note 12", 166, 158, 146, 134, 122, 172, 600),
            DeviceSensiInfo("Redmi Note 13", 170, 162, 150, 138, 125, 176, 620)
        )),

        // ═══════════════════ REDMI ═══════════════════
        BrandInfo("Redmi", "📱", listOf(
            DeviceSensiInfo("Redmi 10", 145, 138, 126, 115, 106, 152, 500),
            DeviceSensiInfo("Redmi 11", 148, 140, 128, 118, 108, 155, 510),
            DeviceSensiInfo("Redmi 12", 152, 144, 132, 120, 110, 158, 530),
            DeviceSensiInfo("Redmi 13", 155, 146, 134, 122, 112, 162, 540),
            DeviceSensiInfo("Redmi Note 10", 158, 150, 138, 126, 115, 165, 560),
            DeviceSensiInfo("Redmi Note 11", 162, 154, 142, 130, 118, 168, 580),
            DeviceSensiInfo("Redmi Note 12", 166, 158, 146, 134, 122, 172, 600),
            DeviceSensiInfo("Redmi Note 13", 170, 162, 150, 138, 125, 176, 620)
        )),

        // ═══════════════════ POCO ═══════════════════
        BrandInfo("Poco", "📱", listOf(
            DeviceSensiInfo("Poco X3", 165, 157, 145, 132, 120, 172, 590),
            DeviceSensiInfo("Poco X4", 168, 160, 148, 135, 122, 175, 610),
            DeviceSensiInfo("Poco X5", 172, 164, 152, 138, 125, 178, 630),
            DeviceSensiInfo("Poco X6", 176, 168, 155, 142, 128, 182, 660),
            DeviceSensiInfo("Poco F3", 180, 172, 160, 146, 132, 186, 700),
            DeviceSensiInfo("Poco F4", 184, 176, 163, 150, 135, 190, 730),
            DeviceSensiInfo("Poco F5", 188, 180, 166, 153, 138, 194, 760)
        )),

        // ═══════════════════ REALME ═══════════════════
        BrandInfo("Realme", "📱", listOf(
            DeviceSensiInfo("Realme 9", 155, 148, 135, 124, 112, 162, 540),
            DeviceSensiInfo("Realme 10", 158, 150, 138, 126, 115, 165, 560),
            DeviceSensiInfo("Realme 11", 162, 154, 142, 130, 118, 168, 580),
            DeviceSensiInfo("Realme 12", 166, 158, 145, 133, 120, 172, 600),
            DeviceSensiInfo("Realme Narzo 50", 150, 142, 130, 120, 110, 158, 520),
            DeviceSensiInfo("Realme Narzo 60", 160, 152, 140, 128, 116, 166, 570),
            DeviceSensiInfo("Realme GT", 185, 176, 164, 150, 136, 192, 750)
        )),

        // ═══════════════════ VIVO ═══════════════════
        BrandInfo("Vivo", "📱", listOf(
            DeviceSensiInfo("Vivo Y20", 138, 130, 120, 110, 102, 145, 470),
            DeviceSensiInfo("Vivo Y21", 140, 132, 122, 112, 104, 148, 480),
            DeviceSensiInfo("Vivo Y22", 142, 135, 124, 114, 105, 150, 490),
            DeviceSensiInfo("Vivo Y27", 148, 140, 128, 118, 108, 155, 510),
            DeviceSensiInfo("Vivo V23", 165, 157, 145, 133, 120, 172, 600),
            DeviceSensiInfo("Vivo V25", 170, 162, 150, 137, 124, 176, 630),
            DeviceSensiInfo("Vivo V27", 175, 167, 155, 142, 128, 182, 660)
        )),

        // ═══════════════════ iQOO ═══════════════════
        BrandInfo("iQOO", "📱", listOf(
            DeviceSensiInfo("iQOO Z6", 168, 160, 148, 135, 122, 175, 610),
            DeviceSensiInfo("iQOO Z7", 172, 164, 152, 138, 125, 178, 640),
            DeviceSensiInfo("iQOO Neo 6", 182, 174, 162, 148, 134, 188, 720),
            DeviceSensiInfo("iQOO Neo 7", 186, 178, 165, 152, 137, 192, 750)
        )),

        // ═══════════════════ OPPO ═══════════════════
        BrandInfo("Oppo", "📱", listOf(
            DeviceSensiInfo("Oppo A15", 135, 128, 118, 108, 100, 142, 450),
            DeviceSensiInfo("Oppo A17", 138, 130, 120, 110, 102, 145, 460),
            DeviceSensiInfo("Oppo A38", 148, 140, 128, 118, 108, 155, 510),
            DeviceSensiInfo("Oppo A58", 155, 147, 135, 124, 113, 162, 550),
            DeviceSensiInfo("Oppo Reno 7", 172, 164, 152, 140, 126, 178, 640),
            DeviceSensiInfo("Oppo Reno 8", 176, 168, 155, 143, 128, 182, 670),
            DeviceSensiInfo("Oppo Reno 10", 180, 172, 160, 147, 132, 186, 700)
        )),

        // ═══════════════════ ONEPLUS ═══════════════════
        BrandInfo("OnePlus", "📱", listOf(
            DeviceSensiInfo("OnePlus 8", 178, 170, 158, 145, 130, 185, 710),
            DeviceSensiInfo("OnePlus 9", 182, 174, 162, 148, 134, 188, 740),
            DeviceSensiInfo("OnePlus 10", 186, 178, 166, 152, 137, 192, 770),
            DeviceSensiInfo("OnePlus 11", 192, 184, 170, 156, 140, 196, 810),
            DeviceSensiInfo("OnePlus Nord CE", 165, 157, 145, 132, 120, 172, 600),
            DeviceSensiInfo("OnePlus Nord 2", 170, 162, 150, 137, 124, 176, 630),
            DeviceSensiInfo("OnePlus Nord 3", 175, 167, 155, 142, 128, 180, 660)
        )),

        // ═══════════════════ MOTOROLA ═══════════════════
        BrandInfo("Motorola", "📱", listOf(
            DeviceSensiInfo("Moto G32", 148, 140, 128, 118, 108, 155, 510),
            DeviceSensiInfo("Moto G42", 152, 144, 132, 121, 110, 158, 530),
            DeviceSensiInfo("Moto G52", 156, 148, 136, 124, 114, 162, 550),
            DeviceSensiInfo("Moto G62", 162, 154, 142, 130, 118, 168, 580),
            DeviceSensiInfo("Moto G72", 166, 158, 146, 134, 122, 172, 600),
            DeviceSensiInfo("Moto G82", 170, 162, 150, 138, 125, 176, 630)
        )),

        // ═══════════════════ GOOGLE PIXEL ═══════════════════
        BrandInfo("Google (Pixel)", "📱", listOf(
            DeviceSensiInfo("Pixel 6", 180, 172, 160, 146, 132, 186, 720),
            DeviceSensiInfo("Pixel 6a", 172, 164, 152, 140, 126, 178, 660),
            DeviceSensiInfo("Pixel 7", 185, 177, 164, 150, 136, 190, 750),
            DeviceSensiInfo("Pixel 7a", 176, 168, 156, 143, 130, 182, 690),
            DeviceSensiInfo("Pixel 8", 192, 184, 170, 156, 140, 196, 810)
        )),

        // ═══════════════════ NOKIA ═══════════════════
        BrandInfo("Nokia", "📱", listOf(
            DeviceSensiInfo("Nokia 5.4", 142, 134, 124, 114, 105, 150, 480),
            DeviceSensiInfo("Nokia 6.2", 148, 140, 128, 118, 108, 155, 510),
            DeviceSensiInfo("Nokia 7.2", 155, 148, 136, 124, 113, 162, 550),
            DeviceSensiInfo("Nokia 8.3", 168, 160, 148, 135, 122, 175, 620)
        )),

        // ═══════════════════ ASUS ═══════════════════
        BrandInfo("Asus", "📱", listOf(
            DeviceSensiInfo("ROG Phone 5", 195, 186, 172, 158, 142, 200, 870),
            DeviceSensiInfo("ROG Phone 6", 198, 190, 176, 162, 145, 200, 890),
            DeviceSensiInfo("Zenfone 9", 180, 172, 160, 146, 132, 186, 720),
            DeviceSensiInfo("Zenfone 10", 185, 177, 164, 150, 135, 190, 760)
        )),

        // ═══════════════════ SONY ═══════════════════
        BrandInfo("Sony", "📱", listOf(
            DeviceSensiInfo("Xperia 1 IV", 188, 180, 168, 154, 138, 194, 780),
            DeviceSensiInfo("Xperia 5 IV", 182, 174, 162, 148, 132, 188, 740),
            DeviceSensiInfo("Xperia 10 IV", 165, 157, 145, 133, 120, 172, 610),
            DeviceSensiInfo("Xperia 1 V", 192, 184, 170, 156, 140, 196, 820)
        )),

        // ═══════════════════ HUAWEI ═══════════════════
        BrandInfo("Huawei", "📱", listOf(
            DeviceSensiInfo("Huawei P40", 175, 167, 155, 142, 128, 182, 670),
            DeviceSensiInfo("Huawei P50", 182, 174, 162, 148, 134, 188, 720),
            DeviceSensiInfo("Huawei Nova 9", 165, 157, 145, 133, 120, 172, 610),
            DeviceSensiInfo("Huawei Nova 10", 170, 162, 150, 137, 124, 176, 640)
        )),

        // ═══════════════════ HONOR ═══════════════════
        BrandInfo("Honor", "📱", listOf(
            DeviceSensiInfo("Honor 50", 168, 160, 148, 136, 122, 175, 620),
            DeviceSensiInfo("Honor 70", 175, 167, 155, 142, 128, 182, 660),
            DeviceSensiInfo("Honor 90", 182, 174, 162, 148, 134, 188, 720),
            DeviceSensiInfo("Honor X8", 152, 144, 132, 121, 110, 158, 530)
        )),

        // ═══════════════════ INFINIX ═══════════════════
        BrandInfo("Infinix", "📱", listOf(
            DeviceSensiInfo("Infinix Hot 10", 135, 128, 118, 108, 100, 142, 440),
            DeviceSensiInfo("Infinix Hot 11", 138, 130, 120, 110, 102, 145, 460),
            DeviceSensiInfo("Infinix Hot 12", 140, 132, 122, 112, 104, 148, 470),
            DeviceSensiInfo("Infinix Hot 20", 148, 140, 128, 118, 108, 155, 510),
            DeviceSensiInfo("Infinix Note 10", 152, 144, 132, 121, 110, 158, 530),
            DeviceSensiInfo("Infinix Note 11", 155, 148, 135, 124, 113, 162, 550),
            DeviceSensiInfo("Infinix Note 12", 160, 152, 140, 128, 116, 166, 570),
            DeviceSensiInfo("Infinix Note 30", 168, 160, 148, 135, 122, 175, 610)
        )),

        // ═══════════════════ TECNO ═══════════════════
        BrandInfo("Tecno", "📱", listOf(
            DeviceSensiInfo("Tecno Spark 8", 132, 125, 115, 106, 100, 140, 430),
            DeviceSensiInfo("Tecno Spark 9", 135, 128, 118, 108, 100, 142, 440),
            DeviceSensiInfo("Tecno Spark 10", 140, 132, 122, 112, 104, 148, 470),
            DeviceSensiInfo("Tecno Spark Go", 128, 122, 112, 104, 100, 136, 420),
            DeviceSensiInfo("Tecno Camon 18", 155, 148, 135, 124, 113, 162, 550),
            DeviceSensiInfo("Tecno Camon 19", 160, 152, 140, 128, 116, 166, 570),
            DeviceSensiInfo("Tecno Camon 20", 165, 157, 145, 133, 120, 172, 600)
        )),

        // ═══════════════════ LAVA ═══════════════════
        BrandInfo("Lava", "📱", listOf(
            DeviceSensiInfo("Lava Blaze", 130, 124, 114, 105, 100, 138, 420),
            DeviceSensiInfo("Lava Blaze 2", 135, 128, 118, 108, 100, 142, 440),
            DeviceSensiInfo("Lava Storm", 142, 135, 124, 114, 105, 150, 480),
            DeviceSensiInfo("Lava Agni 2", 158, 150, 138, 126, 115, 165, 560)
        )),

        // ═══════════════════ MICROMAX ═══════════════════
        BrandInfo("Micromax", "📱", listOf(
            DeviceSensiInfo("Micromax IN 1", 140, 132, 122, 112, 104, 148, 470),
            DeviceSensiInfo("Micromax IN 2", 145, 138, 126, 116, 106, 152, 490),
            DeviceSensiInfo("Micromax IN Note 1", 150, 142, 130, 120, 110, 158, 520),
            DeviceSensiInfo("Micromax IN Note 2", 155, 148, 135, 124, 113, 162, 540)
        ))
    )
}
