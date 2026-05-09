package com.example.sensixpert.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.sensixpert.data.GunSensitivity
import com.example.sensixpert.data.GunSensitivityData
import com.example.sensixpert.ui.theme.*

/**
 * Guns Sensitivity Screen
 *
 * Shows a list of all Free Fire guns with a "SHOW" button next to each.
 * Clicking SHOW → interstitial ad → then gun sensitivity details appear.
 */
@Composable
fun SensitivityScreen(
    modifier: Modifier = Modifier,
    autoDetect: Boolean = false,
    isSubscribed: Boolean = false,
    onNavigateToSubscription: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Which gun's details are we showing? null = gun list
    var selectedGun by remember { mutableStateOf<GunSensitivity?>(null) }
    var isLoadingAd by remember { mutableStateOf(false) }

    // Handle system back button
    BackHandler {
        if (selectedGun != null) {
            selectedGun = null  // Go back to gun list
        } else {
            onNavigateBack()   // Go back to main screen
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GamingBackground)
    ) {
        AnimatedContent(
            targetState = selectedGun,
            transitionSpec = {
                if (targetState != null) {
                    slideInHorizontally(tween(250)) { it } + fadeIn(tween(250)) togetherWith
                            slideOutHorizontally(tween(250)) { -it } + fadeOut(tween(250))
                } else {
                    slideInHorizontally(tween(250)) { -it } + fadeIn(tween(250)) togetherWith
                            slideOutHorizontally(tween(250)) { it } + fadeOut(tween(250))
                }
            },
            label = "gun_nav"
        ) { gun ->
            if (gun == null) {
                // ═══════════════════════════════
                // GUN LIST
                // ═══════════════════════════════
                GunListContent(
                    onNavigateBack = onNavigateBack,
                    isLoadingAd = isLoadingAd,
                    isSubscribed = isSubscribed,
                    onShowGun = { clickedGun ->
                        if (isSubscribed) {
                            // Premium users → skip ads, direct access
                            selectedGun = clickedGun
                        } else {
                            // Free users → show subscription popup
                            onNavigateToSubscription()
                        }
                    }
                )
            } else {
                // ═══════════════════════════════
                // GUN DETAILS
                // ═══════════════════════════════
                GunDetailsContent(
                    gun = gun,
                    onBack = { selectedGun = null }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// GUN LIST
// ═══════════════════════════════════════════════════════════
@Composable
private fun GunListContent(
    onNavigateBack: () -> Unit,
    isLoadingAd: Boolean,
    isSubscribed: Boolean,
    onShowGun: (GunSensitivity) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // ── Top Bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onNavigateBack
                )
            )
            Text(
                text = "Guns Sensitivity",
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.width(24.dp))
        }

        // ── Header ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(GamingRed.copy(alpha = 0.4f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🔫", fontSize = 26.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SELECT A GUN",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Text(
                text = "Tap SHOW to view best sensitivity",
                color = TextDimGrey,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Gun List ──
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(GunSensitivityData.guns, key = { it.gunName }) { gun ->
                GunListItem(
                    gun = gun,
                    isLoadingAd = isLoadingAd,
                    isSubscribed = isSubscribed,
                    onShowClick = { onShowGun(gun) }
                )
            }
        }
    }
}

@Composable
private fun GunListItem(
    gun: GunSensitivity,
    isLoadingAd: Boolean,
    isSubscribed: Boolean,
    onShowClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gun icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = gun.emoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Gun name + category
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = gun.gunName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = gun.category,
                    color = TextDimGrey,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
            }

            // SHOW button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (isSubscribed) listOf(GamingRed, GamingDarkRed)
                                     else listOf(Color(0xFF555555), Color(0xFF444444))
                        )
                    )
                    .clickable(enabled = !isLoadingAd) { onShowClick() }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSubscribed) "SHOW" else "🔒 SHOW",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// GUN DETAILS
// ═══════════════════════════════════════════════════════════
@Composable
private fun GunDetailsContent(
    gun: GunSensitivity,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // ── Top Bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBack
                )
            )
            Text(
                text = gun.gunName,
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.width(24.dp))
        }

        // ── Scrollable content ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Gun icon ──
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(GamingRed.copy(alpha = 0.3f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = gun.emoji, fontSize = 34.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Gun name + category ──
            Text(
                text = gun.gunName,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "RECOMMENDED SENSI  •  ${gun.category.uppercase()}",
                color = TextGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                letterSpacing = 1.5.sp
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
                    GunSensiSlider("General", gun.general)
                    GunSensiSlider("Red Dot", gun.redDot)
                    GunSensiSlider("2x Scope", gun.scope2x)
                    GunSensiSlider("4x Scope", gun.scope4x)
                    GunSensiSlider("AWM Scope", gun.awmScope)
                    GunSensiSlider("Free Look", gun.freeLook)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── DPI Card ──
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
                    // DPI
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📐", fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "DPI",
                            color = TextDimGrey,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = gun.dpi.toString(),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(44.dp)
                            .background(Color(0xFF333333))
                    )

                    // Category
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = gun.emoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "TYPE",
                            color = TextDimGrey,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = gun.category,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Back to guns button ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(GamingRed, GamingDarkRed)),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "← BACK TO GUNS",
                    color = GamingRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════
// GUN SENSITIVITY SLIDER (red themed, animated)
// ═══════════════════════════════════════════════════════════
@Composable
private fun GunSensiSlider(
    label: String,
    value: Int
) {
    // Animate from 0 to value for smooth entry
    val animatedFraction by animateFloatAsState(
        targetValue = value.toFloat() / 200f,   // max is 200
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "gun_slider_anim"
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

        // Red divider line
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GamingRed.copy(alpha = 0.2f))
        )
    }
}
