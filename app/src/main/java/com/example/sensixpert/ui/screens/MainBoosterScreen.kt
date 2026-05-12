package com.example.sensixpert.ui.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.data.ConnectionState
import com.example.sensixpert.ui.components.*
import com.example.sensixpert.ui.theme.*
import com.example.sensixpert.viewmodel.AuthViewModel
import com.example.sensixpert.viewmodel.BoosterViewModel
import com.example.sensixpert.viewmodel.SubscriptionViewModel
import kotlinx.coroutines.launch

@Composable
fun MainBoosterScreen(
    viewModel: BoosterViewModel,
    authViewModel: AuthViewModel,
    subscriptionViewModel: SubscriptionViewModel,
    modifier: Modifier = Modifier,
    onNavigateToSensitivity: () -> Unit = {},
    onNavigateToDeviceSensi: () -> Unit = {},
    onNavigateToPrivacyPolicy: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {},
    onNavigateToSupport: () -> Unit = {},
    onNavigateToReferEarn: () -> Unit = {},
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val connectionState = viewModel.connectionState
    val isConnected = connectionState == ConnectionState.CONNECTED
    val isSubscribed = subscriptionViewModel.isSubscribed

    // Generate Sensi sheet state
    var showGenerateSensi by remember { mutableStateOf(false) }

    // Subscribe prompt dialog
    var showSubscribePrompt by remember { mutableStateOf(false) }

    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF121212),
                drawerTonalElevation = 0.dp,
                modifier = Modifier.width(280.dp)
            ) {
                // ── Drawer Header ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF8B1A1A),
                                    Color(0xFF5A0C0C),
                                    Color(0xFF121212)
                                )
                            )
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Text(
                            text = "SensiXpert",
                            color = TextWhite,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        // Show user name
                        authViewModel.getUserName()?.let { name ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "👋 $name",
                                color = TextGrey,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        // Show subscription status
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isSubscribed) "👑 Premium User" else "Free User",
                            color = if (isSubscribed) GamingGreen else TextDimGrey,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── Subscription ──
                DrawerMenuItem(
                    label = if (isSubscribed) "Active Plan" else "Subscribe",
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToSubscription()
                    }
                )

                // ── Privacy Policy ──
                DrawerMenuItem(
                    label = "Privacy Policy",
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToPrivacyPolicy()
                    }
                )

                // ── Terms & Conditions ──
                DrawerMenuItem(
                    label = "Terms & Conditions",
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToTerms()
                    }
                )

                // ── Refer & Earn (Premium Only) ──
                DrawerMenuItem(
                    label = if (isSubscribed) "🎁 Refer & Earn" else "🔒 Refer & Earn",
                    onClick = {
                        scope.launch { drawerState.close() }
                        if (isSubscribed) {
                            onNavigateToReferEarn()
                        } else {
                            showSubscribePrompt = true
                        }
                    }
                )

                // ── Customer Support ──
                DrawerMenuItem(
                    label = "💬 Customer Support",
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToSupport()
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                // ── Logout ──
                DrawerMenuItem(
                    label = "🚪 Logout",
                    onClick = {
                        scope.launch { drawerState.close() }
                        authViewModel.logout()
                    }
                )

                // ── Footer ──
                Text(
                    text = "v1.0",
                    color = TextDimGrey,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .navigationBarsPadding()
                )
            }
        }
    ) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GamingBackground)
    ) {
        // ═══════════════════════════════════════════════
        // BACKGROUND: Solid deep crimson top → black bottom
        // with thin curved arc separator lines (reference match)
        // ═══════════════════════════════════════════════
        // Pre-computed colors (stable — not re-allocated per frame)
        val topCrimson = Color(0xFF8B1A1A)
        val midCrimson = Color(0xFF6B1010)
        val deepCrimson = Color(0xFF4A0A0A)
        val arcLineColor = Color(0xFFAA3030)
        val bgBlack = Color(0xFF0A0A0A)

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // ── LAYER 1: Solid rich crimson fill covering top 40% ──
            val topFillPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, h * 0.35f)
                quadraticBezierTo(
                    w * 0.5f, h * 0.48f,
                    0f, h * 0.35f
                )
                close()
            }
            drawPath(
                path = topFillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        topCrimson,
                        topCrimson,
                        midCrimson,
                        deepCrimson,
                        bgBlack.copy(alpha = 0.0f)
                    ),
                    startY = 0f,
                    endY = h * 0.48f
                )
            )

            // ── LAYER 2: Slightly darker crimson arc underneath ──
            val layer2Path = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, h * 0.28f)
                quadraticBezierTo(
                    w * 0.5f, h * 0.40f,
                    0f, h * 0.28f
                )
                close()
            }
            drawPath(
                path = layer2Path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF921818),
                        Color(0xFF7A1212),
                        Color(0xFF5A0C0C),
                        deepCrimson.copy(alpha = 0.5f)
                    ),
                    startY = 0f,
                    endY = h * 0.40f
                )
            )

            // ── LAYER 3: Topmost richest crimson ──
            val layer3Path = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, h * 0.20f)
                quadraticBezierTo(
                    w * 0.5f, h * 0.30f,
                    0f, h * 0.20f
                )
                close()
            }
            drawPath(
                path = layer3Path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF9E1E1E),
                        Color(0xFF8A1515),
                        Color(0xFF6E1010),
                        Color(0xFF4A0A0A)
                    ),
                    startY = 0f,
                    endY = h * 0.30f
                )
            )

            // ── THIN ARC LINE 1 (outermost visible curve) ──
            val arcLine1 = Path().apply {
                moveTo(0f, h * 0.35f)
                quadraticBezierTo(
                    w * 0.5f, h * 0.48f,
                    w, h * 0.35f
                )
            }
            drawPath(
                path = arcLine1,
                color = arcLineColor.copy(alpha = 0.45f),
                style = Stroke(width = 2f)
            )

            // ── THIN ARC LINE 2 (inner curve, slightly dimmer) ──
            val arcLine2 = Path().apply {
                moveTo(0f, h * 0.28f)
                quadraticBezierTo(
                    w * 0.5f, h * 0.38f,
                    w, h * 0.28f
                )
            }
            drawPath(
                path = arcLine2,
                color = arcLineColor.copy(alpha = 0.30f),
                style = Stroke(width = 1.5f)
            )

            // ── THIN ARC LINE 3 (topmost thin curve) ──
            val arcLine3 = Path().apply {
                moveTo(0f, h * 0.20f)
                quadraticBezierTo(
                    w * 0.5f, h * 0.28f,
                    w, h * 0.20f
                )
            }
            drawPath(
                path = arcLine3,
                color = arcLineColor.copy(alpha = 0.20f),
                style = Stroke(width = 1f)
            )

            // ═══════════════════════════════════════════════
            // BOTTOM ARC LINES (mirror of top — 3 curved lines)
            // ═══════════════════════════════════════════════

            // ── Bottom fill (subtle crimson glow at bottom) ──
            val bottomFillPath = Path().apply {
                moveTo(0f, h)
                lineTo(w, h)
                lineTo(w, h * 0.78f)
                quadraticBezierTo(
                    w * 0.5f, h * 0.72f,
                    0f, h * 0.78f
                )
                close()
            }
            drawPath(
                path = bottomFillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        deepCrimson.copy(alpha = 0.0f),
                        deepCrimson.copy(alpha = 0.15f),
                        midCrimson.copy(alpha = 0.12f),
                        topCrimson.copy(alpha = 0.08f)
                    ),
                    startY = h * 0.72f,
                    endY = h
                )
            )

            // ── BOTTOM ARC LINE 1 (outermost — closest to bottom) ──
            val bottomArc1 = Path().apply {
                moveTo(0f, h * 0.78f)
                quadraticBezierTo(
                    w * 0.5f, h * 0.72f,
                    w, h * 0.78f
                )
            }
            drawPath(
                path = bottomArc1,
                color = arcLineColor.copy(alpha = 0.40f),
                style = Stroke(width = 2f)
            )

            // ── BOTTOM ARC LINE 2 (middle) ──
            val bottomArc2 = Path().apply {
                moveTo(0f, h * 0.82f)
                quadraticBezierTo(
                    w * 0.5f, h * 0.77f,
                    w, h * 0.82f
                )
            }
            drawPath(
                path = bottomArc2,
                color = arcLineColor.copy(alpha = 0.28f),
                style = Stroke(width = 1.5f)
            )

            // ── BOTTOM ARC LINE 3 (innermost — furthest from bottom) ──
            val bottomArc3 = Path().apply {
                moveTo(0f, h * 0.86f)
                quadraticBezierTo(
                    w * 0.5f, h * 0.82f,
                    w, h * 0.86f
                )
            }
            drawPath(
                path = bottomArc3,
                color = arcLineColor.copy(alpha = 0.18f),
                style = Stroke(width = 1f)
            )

            // ── Ambient glow around button area ──
            val btnGlow = if (isConnected) GamingGreen else ButtonCoral
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        btnGlow.copy(alpha = 0.06f),
                        btnGlow.copy(alpha = 0.02f),
                        Color.Transparent
                    ),
                    center = Offset(w / 2, h * 0.42f),
                    radius = w * 0.50f
                ),
                radius = w * 0.50f,
                center = Offset(w / 2, h * 0.42f)
            )
        }

        // ═══════════════════════════════════════════════
        // CONTENT
        // ═══════════════════════════════════════════════
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Header with Hamburger Menu + Subscribe Button ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                // Hamburger menu icon (left)
                IconButton(
                    onClick = {
                        scope.launch { drawerState.open() }
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = TextWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Centered title
                Text(
                    text = "SensiXpert",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )

                // ── Top-right Premium icon ──
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSubscribed) GamingRed.copy(alpha = 0.25f)
                            else Color.White.copy(alpha = 0.12f)
                        )
                        .clickable { onNavigateToSubscription() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👑",
                        fontSize = 18.sp
                    )
                }
            }

            // ── Push button to center ──
            Spacer(modifier = Modifier.weight(0.28f))

            // ── CONNECT BUTTON ──
            // Only premium users can use CONNECT
            Box(contentAlignment = Alignment.Center) {
                ConnectButton(
                    connectionState = connectionState,
                    onClick = {
                        if (isSubscribed) {
                            activity?.let { viewModel.onConnectPressed(it) }
                        } else {
                            showSubscribePrompt = true
                        }
                    }
                )

                // Lock overlay for non-subscribed
                if (!isSubscribed && connectionState == ConnectionState.DISCONNECTED) {
                    Box(
                        modifier = Modifier
                            .offset(y = 60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🔒 Premium Only",
                            color = GamingOrangeRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Status info + Timer (only when connected or transitioning) ──
            AnimatedContent(
                targetState = connectionState,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                label = "status"
            ) { state ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (state) {
                        ConnectionState.CONNECTED -> {
                            // Running timer — pure white, extra bold, prominent
                            Text(
                                text = viewModel.timerText,
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 3.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Connected",
                                color = GamingGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        ConnectionState.CONNECTING -> {
                            Text(
                                text = "Connecting…",
                                color = GamingOrangeRed,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        ConnectionState.DISCONNECTING -> {
                            Text(
                                text = "Disconnecting…",
                                color = GamingOrangeRed,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        ConnectionState.DISCONNECTED -> {
                            // Empty — clean look when disconnected
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }

            // ── Ultra Boost floating text ──
            AnimatedVisibility(
                visible = viewModel.ultraBoostStatusText.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = viewModel.ultraBoostStatusText,
                    color = AccentOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            // ── Push bottom nav down ──
            Spacer(modifier = Modifier.weight(0.18f))



            // ═══════════════════════════════════════════════
            // BOTTOM NAVIGATION: 3 circle buttons
            // ═══════════════════════════════════════════════
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                // LEFT — Guns sensi
                BottomCircleButton(
                    iconType = BottomIconType.GUN,
                    label = "Guns sensi",
                    onClick = onNavigateToSensitivity,
                    size = 72.dp
                )

                // CENTER — Plus (Generate Sensi)
                BottomCircleButton(
                    iconType = BottomIconType.PLUS,
                    label = "",
                    onClick = {
                        showGenerateSensi = true
                    },
                    size = 66.dp
                )

                // RIGHT — Device sensi
                BottomCircleButton(
                    iconType = BottomIconType.PHONE,
                    label = "Device sensi",
                    onClick = onNavigateToDeviceSensi,
                    size = 72.dp
                )
            }
        }

        // ═══════════════════════════════════════════════
        // GENERATE SENSI SHEET
        // ═══════════════════════════════════════════════
        if (showGenerateSensi) {
            GenerateSensiSheet(
                isSubscribed = isSubscribed,
                onNavigateToSubscription = {
                    showGenerateSensi = false
                    onNavigateToSubscription()
                },
                onDismiss = {
                    showGenerateSensi = false
                }
            )
        }

        // ═══════════════════════════════════════════════
        // SUBSCRIBE PROMPT DIALOG
        // ═══════════════════════════════════════════════
        if (showSubscribePrompt) {
            AlertDialog(
                onDismissRequest = { showSubscribePrompt = false },
                containerColor = Color(0xFF1A1A1A),
                titleContentColor = Color.White,
                textContentColor = TextGrey,
                title = {
                    Text(
                        text = "🔒 Premium Feature",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text("CONNECT Booster is available for premium users only. Subscribe to unlock full access!")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSubscribePrompt = false
                            onNavigateToSubscription()
                        }
                    ) {
                        Text("Subscribe", color = GamingRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSubscribePrompt = false }) {
                        Text("Later", color = TextDimGrey)
                    }
                }
            )
        }
    }

    } // end ModalNavigationDrawer
}

// ── Reusable Drawer Menu Item ──
@Composable
private fun DrawerMenuItem(
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextDimGrey,
            modifier = Modifier.size(22.dp)
        )
    }

    // Subtle divider
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(0.5.dp)
            .background(Color(0xFF2A2A2A))
    )
}
