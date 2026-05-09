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
import com.example.sensixpert.data.BrandInfo
import com.example.sensixpert.data.DeviceSensiInfo
import com.example.sensixpert.data.DeviceSensitivityData
import com.example.sensixpert.ui.theme.*

/**
 * Device Sensitivity Screen
 *
 * Flow: Brand List → (Ad) → Device List → (Ad) → Sensitivity Details
 */

// Navigation state for this screen
private sealed class DeviceNavState {
    object BrandList : DeviceNavState()
    data class DeviceList(val brand: BrandInfo) : DeviceNavState()
    data class DeviceDetails(val brand: BrandInfo, val device: DeviceSensiInfo) : DeviceNavState()
}

@Composable
fun DeviceSensiScreen(
    modifier: Modifier = Modifier,
    isSubscribed: Boolean = false,
    onNavigateToSubscription: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var navState by remember { mutableStateOf<DeviceNavState>(DeviceNavState.BrandList) }
    var isLoadingAd by remember { mutableStateOf(false) }

    // Handle system back button
    BackHandler {
        when (navState) {
            is DeviceNavState.DeviceDetails -> {
                navState = DeviceNavState.DeviceList((navState as DeviceNavState.DeviceDetails).brand)
            }
            is DeviceNavState.DeviceList -> {
                navState = DeviceNavState.BrandList
            }
            is DeviceNavState.BrandList -> {
                onNavigateBack()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GamingBackground)
    ) {
        AnimatedContent(
            targetState = navState,
            transitionSpec = {
                val forward = when {
                    targetState is DeviceNavState.DeviceList && initialState is DeviceNavState.BrandList -> true
                    targetState is DeviceNavState.DeviceDetails && initialState is DeviceNavState.DeviceList -> true
                    else -> false
                }
                if (forward) {
                    slideInHorizontally(tween(250)) { it } + fadeIn(tween(250)) togetherWith
                            slideOutHorizontally(tween(250)) { -it } + fadeOut(tween(250))
                } else {
                    slideInHorizontally(tween(250)) { -it } + fadeIn(tween(250)) togetherWith
                            slideOutHorizontally(tween(250)) { it } + fadeOut(tween(250))
                }
            },
            label = "device_nav"
        ) { state ->
            when (state) {
                is DeviceNavState.BrandList -> {
                    BrandListContent(
                        onNavigateBack = onNavigateBack,
                        isLoadingAd = isLoadingAd,
                        onBrandClick = { brand ->
                            // All users can see brand list → navigate to device list
                            navState = DeviceNavState.DeviceList(brand)
                        }
                    )
                }
                is DeviceNavState.DeviceList -> {
                    DeviceListContent(
                        brand = state.brand,
                        isLoadingAd = isLoadingAd,
                        isSubscribed = isSubscribed,
                        onBack = { navState = DeviceNavState.BrandList },
                        onDeviceClick = { device ->
                            if (isSubscribed) {
                                // Premium → direct access
                                navState = DeviceNavState.DeviceDetails(state.brand, device)
                            } else {
                                // Free users → subscription popup
                                onNavigateToSubscription()
                            }
                        }
                    )
                }
                is DeviceNavState.DeviceDetails -> {
                    DeviceDetailsContent(
                        brand = state.brand,
                        device = state.device,
                        onBack = { navState = DeviceNavState.DeviceList(state.brand) }
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// BRAND LIST
// ═══════════════════════════════════════════════════════════
@Composable
private fun BrandListContent(
    onNavigateBack: () -> Unit,
    isLoadingAd: Boolean,
    onBrandClick: (BrandInfo) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
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
                text = "Device Sensitivity",
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
                Text(text = "📱", fontSize = 26.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "SELECT YOUR BRAND",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Text(
                text = "Choose your mobile brand",
                color = TextDimGrey,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Brand List ──
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(DeviceSensitivityData.brands, key = { it.brandName }) { brand ->
                BrandListItem(
                    brand = brand,
                    isLoadingAd = isLoadingAd,
                    onClick = { onBrandClick(brand) }
                )
            }
        }
    }
}

@Composable
private fun BrandListItem(
    brand: BrandInfo,
    isLoadingAd: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoadingAd) { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = brand.emoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Brand name + device count
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = brand.brandName,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${brand.devices.size} devices",
                    color = TextDimGrey,
                    fontSize = 12.sp
                )
            }

            // Arrow
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(GamingRed.copy(alpha = 0.2f), GamingDarkRed.copy(alpha = 0.2f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "→",
                    color = GamingRed,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// DEVICE LIST (for a selected brand)
// ═══════════════════════════════════════════════════════════
@Composable
private fun DeviceListContent(
    brand: BrandInfo,
    isLoadingAd: Boolean,
    isSubscribed: Boolean,
    onBack: () -> Unit,
    onDeviceClick: (DeviceSensiInfo) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
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
                    onClick = onBack
                )
            )
            Text(
                text = brand.brandName,
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
            Text(
                text = "SELECT YOUR DEVICE",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${brand.devices.size} models available",
                color = TextDimGrey,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Device List ──
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(brand.devices, key = { it.deviceName }) { device ->
                DeviceListItem(
                    device = device,
                    isLoadingAd = isLoadingAd,
                    isSubscribed = isSubscribed,
                    onClick = { onDeviceClick(device) }
                )
            }
        }
    }
}

@Composable
private fun DeviceListItem(
    device: DeviceSensiInfo,
    isLoadingAd: Boolean,
    isSubscribed: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoadingAd) { onClick() },
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
            // Device icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📱", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Device name
            Text(
                text = device.deviceName,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            // View button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (isSubscribed) listOf(GamingRed, GamingDarkRed)
                                     else listOf(Color(0xFF555555), Color(0xFF444444))
                        )
                    )
                    .padding(horizontal = 18.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSubscribed) "VIEW" else "🔒 VIEW",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// DEVICE DETAILS (sensitivity values)
// ═══════════════════════════════════════════════════════════
@Composable
private fun DeviceDetailsContent(
    brand: BrandInfo,
    device: DeviceSensiInfo,
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
                text = device.deviceName,
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
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
            Spacer(modifier = Modifier.height(12.dp))

            // ── Device icon ──
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
                Text(text = "📱", fontSize = 34.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Device name ──
            Text(
                text = device.deviceName,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "RECOMMENDED SENSI  •  ${brand.brandName.uppercase()}",
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
                    DeviceSensiSlider("General", device.general)
                    DeviceSensiSlider("Red Dot", device.redDot)
                    DeviceSensiSlider("2x Scope", device.scope2x)
                    DeviceSensiSlider("4x Scope", device.scope4x)
                    DeviceSensiSlider("AWM Scope", device.awmScope)
                    DeviceSensiSlider("Free Look", device.freeLook)
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
                            text = device.dpi.toString(),
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

                    // Brand
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📱", fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "BRAND",
                            color = TextDimGrey,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = brand.brandName,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Back button ──
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
                    text = "← BACK TO DEVICES",
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
// DEVICE SENSITIVITY SLIDER
// ═══════════════════════════════════════════════════════════
@Composable
private fun DeviceSensiSlider(
    label: String,
    value: Int
) {
    val animatedFraction by animateFloatAsState(
        targetValue = value.toFloat() / 200f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "device_slider_anim"
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

        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GamingRed.copy(alpha = 0.2f))
        )
    }
}
