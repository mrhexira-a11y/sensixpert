package com.example.sensixpert.ui.screens

import android.app.Activity
import android.os.Build
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.data.DeviceAnalyzer
import com.example.sensixpert.data.SensitivityCalculator
import com.example.sensixpert.data.SensitivitySettings
import com.example.sensixpert.ui.theme.*

/**
 * Full-screen overlay for "Generate Sensi For This Device"
 *
 * Two states:
 *   1) Form — Choose sensitivity level + tap GENERATE (shows ad first)
 *   2) Results — Device name, sliders, DPI, button size, feedback
 */
@Composable
fun GenerateSensiSheet(
    isSubscribed: Boolean = false,
    onNavigateToSubscription: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // ── State ──
    var selectedLevel by remember { mutableStateOf("VERY HIGH") }
    val levels = listOf("VERY HIGH", "HIGH", "MEDIUM", "LOW")

    var generatedSettings by remember { mutableStateOf<SensitivitySettings?>(null) }
    var deviceName by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    // Scrim
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        // Bottom sheet content
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.82f)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF181818), Color(0xFF0A0A0A))
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {} // consume
                )
        ) {
            // Handle bar
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(TextDimGrey)
                    .align(Alignment.CenterHorizontally)
            )

            AnimatedContent(
                targetState = generatedSettings,
                transitionSpec = {
                    fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 4 } togetherWith
                            fadeOut(tween(200))
                },
                label = "sensi_state"
            ) { settings ->
                if (settings == null) {
                    // ═══════════════════════════════════
                    // STATE 1: FORM — Choose level + Generate
                    // ═══════════════════════════════════
                    GenerateFormContent(
                        selectedLevel = selectedLevel,
                        levels = levels,
                        isGenerating = isGenerating,
                        onLevelSelected = { selectedLevel = it },
                        onGenerate = {
                            if (!isSubscribed) {
                                // Free users → subscription page
                                onNavigateToSubscription()
                                return@GenerateFormContent
                            }
                            isGenerating = true
                            // Detect device
                            val specs = DeviceAnalyzer.analyze(context)
                            val brand = specs.deviceBrand
                            val model = specs.deviceModel
                            deviceName = "$brand $model"

                            // Calculate sensitivity with level multiplier
                            val baseSensi = SensitivityCalculator.calculate(specs)
                            val multiplier = when (selectedLevel) {
                                "VERY HIGH" -> 1.15f
                                "HIGH" -> 1.0f
                                "MEDIUM" -> 0.85f
                                "LOW" -> 0.70f
                                else -> 1.0f
                            }
                            val adjusted = SensitivitySettings(
                                general = (baseSensi.general * multiplier).toInt().coerceIn(100, 200),
                                redDot = (baseSensi.redDot * multiplier).toInt().coerceIn(100, 200),
                                scope2x = (baseSensi.scope2x * multiplier).toInt().coerceIn(100, 200),
                                scope4x = (baseSensi.scope4x * multiplier).toInt().coerceIn(100, 200),
                                awmScope = (baseSensi.awmScope * multiplier).toInt().coerceIn(100, 200),
                                freeLook = (baseSensi.freeLook * multiplier).toInt().coerceIn(100, 200),
                                buttonSize = baseSensi.buttonSize,
                                recommendedDpi = baseSensi.recommendedDpi
                            )

                            // Premium users → direct result (no ads)
                            generatedSettings = adjusted
                            isGenerating = false
                        }
                    )
                } else {
                    // ═══════════════════════════════════
                    // STATE 2: RESULTS
                    // ═══════════════════════════════════
                    ResultsContent(
                        deviceName = deviceName,
                        settings = settings,
                        onDismiss = onDismiss,
                        onRegenerate = {
                            generatedSettings = null
                        }
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// FORM STATE
// ═══════════════════════════════════════════════════════════
@Composable
private fun GenerateFormContent(
    selectedLevel: String,
    levels: List<String>,
    isGenerating: Boolean,
    onLevelSelected: (String) -> Unit,
    onGenerate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // ── Icon ──
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(GamingDarkRed.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚙",
                fontSize = 28.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Title ──
        Text(
            text = "GENERATE SENSI",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
        )
        Text(
            text = "FOR THIS DEVICE",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Sensitivity Level Card ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💀", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Sensitivity Level",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Choose your preferred level",
                            color = TextDimGrey,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Level Chips ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    levels.forEach { level ->
                        val isSelected = selectedLevel == level
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .then(
                                    if (isSelected) {
                                        Modifier.background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(GamingRed, GamingDarkRed)
                                            )
                                        )
                                    } else {
                                        Modifier
                                            .background(Color(0xFF2A2A2A))
                                            .border(
                                                1.dp,
                                                Color(0xFF444444),
                                                RoundedCornerShape(10.dp)
                                            )
                                    }
                                )
                                .clickable { onLevelSelected(level) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = level,
                                color = if (isSelected) Color.White else TextGrey,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── GENERATE BUTTON ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(GamingRed, Color(0xFFFF2020), GamingDarkRed)
                    )
                )
                .clickable(enabled = !isGenerating) { onGenerate() },
            contentAlignment = Alignment.Center
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "✦", fontSize = 18.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "GENERATE SENSI",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ═══════════════════════════════════════════════════════════
// RESULTS STATE
// ═══════════════════════════════════════════════════════════
@Composable
private fun ResultsContent(
    deviceName: String,
    settings: SensitivitySettings,
    onDismiss: () -> Unit,
    onRegenerate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // ── Device icon ──
        Text(text = "📱", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(12.dp))

        // ── Device name ──
        Text(
            text = deviceName,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // ── Subtitle ──
        Text(
            text = "RECOMMENDED SENSI",
            color = TextGrey,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Sensitivity Sliders Card ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                SensiSliderRow("General", settings.general)
                SensiSliderRow("Red Dot", settings.redDot)
                SensiSliderRow("2x Scope", settings.scope2x)
                SensiSliderRow("4x Scope", settings.scope4x)
                SensiSliderRow("AWM Scope", settings.awmScope)
                SensiSliderRow("Free Look", settings.freeLook)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── BUTTON + DPI row ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button size
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🎯", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "BUTTON:",
                        color = TextGrey,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${settings.buttonSize}%",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(Color(0xFF333333))
                )

                // DPI
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📐", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "DPI:",
                        color = TextGrey,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = settings.recommendedDpi.toString(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Feedback Row — NOT WORKING / IT WORKS ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // NOT WORKING
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2A2A2A))
                    .border(1.dp, Color(0xFF444444), RoundedCornerShape(12.dp))
                    .clickable { onRegenerate() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "👎", fontSize = 16.sp)
                    Text(
                        text = "NOT WORKING",
                        color = TextGrey,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // IT WORKS
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(GamingRed, GamingDarkRed)
                        )
                    )
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "👍", fontSize = 16.sp)
                    Text(
                        text = "IT WORKS",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Regenerate button ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    1.dp,
                    Brush.horizontalGradient(listOf(GamingRed, GamingDarkRed)),
                    RoundedCornerShape(12.dp)
                )
                .clickable { onRegenerate() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🔄  REGENERATE",
                color = GamingRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ═══════════════════════════════════════════════════════════
// SENSITIVITY SLIDER ROW (red themed, matching reference)
// ═══════════════════════════════════════════════════════════
@Composable
private fun SensiSliderRow(
    label: String,
    value: Int
) {
    val animatedFraction by animateFloatAsState(
        targetValue = value.toFloat() / 200f,  // max 200 for scale
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "slider_anim"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = TextGrey,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value.toString(),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Slider track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF333333))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFraction.coerceIn(0.02f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(GamingRed, GamingOrangeRed)
                        )
                    )
            )
        }

        // Red line below slider (reference match)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GamingRed.copy(alpha = 0.25f))
        )
    }
}
