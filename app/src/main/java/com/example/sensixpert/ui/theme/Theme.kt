package com.example.sensixpert.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val GamingDarkColorScheme = darkColorScheme(
    primary = GamingRed,
    onPrimary = TextWhite,
    primaryContainer = GamingDarkRed,
    onPrimaryContainer = TextWhite,
    secondary = GamingCrimson,
    onSecondary = TextWhite,
    background = GamingBackground,
    onBackground = TextWhite,
    surface = CardDark,
    onSurface = TextWhite,
    surfaceVariant = CardDarkElevated,
    onSurfaceVariant = TextGrey,
    outline = Color(0xFF333333),
    outlineVariant = Color(0xFF222222),
)

@Composable
fun SensixpertTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = GamingDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = GamingBackground.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}