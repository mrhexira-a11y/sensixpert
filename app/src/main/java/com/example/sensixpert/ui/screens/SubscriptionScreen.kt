package com.example.sensixpert.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.data.SubscriptionPlan
import com.example.sensixpert.ui.theme.*
import com.example.sensixpert.viewmodel.SubscriptionViewModel

@Composable
fun SubscriptionScreen(
    subscriptionViewModel: SubscriptionViewModel,
    userId: String?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedPlan by remember { mutableStateOf(SubscriptionPlan.STANDARD) }

    // ── Activity Result Launcher for PaymentWebViewActivity ──
    val paymentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val message = subscriptionViewModel.handlePaymentResult(result)
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    // ── Launch payment WebView when intent is ready ──
    val pendingIntent = subscriptionViewModel.pendingPaymentIntent
    LaunchedEffect(pendingIntent) {
        pendingIntent?.let { intent ->
            paymentLauncher.launch(intent)
            subscriptionViewModel.clearPendingPaymentIntent()
        }
    }

    // ── Warmup backend server when screen opens ──
    LaunchedEffect(Unit) {
        subscriptionViewModel.warmupBackend()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingBackground)
    ) {
        // ── Background arcs ──
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val topCrimson = Color(0xFF8B1A1A)
            val midCrimson = Color(0xFF6B1010)
            val arcLineColor = Color(0xFFAA3030)

            val topFillPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, h * 0.22f)
                quadraticBezierTo(w * 0.5f, h * 0.32f, 0f, h * 0.22f)
                close()
            }
            drawPath(
                path = topFillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(topCrimson, midCrimson, Color(0xFF4A0A0A), Color.Transparent),
                    startY = 0f, endY = h * 0.32f
                )
            )
            val arcLine = Path().apply {
                moveTo(0f, h * 0.22f)
                quadraticBezierTo(w * 0.5f, h * 0.32f, w, h * 0.22f)
            }
            drawPath(path = arcLine, color = arcLineColor.copy(alpha = 0.35f), style = Stroke(width = 2f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
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
                        onClick = onNavigateBack
                    )
                )
                Text(
                    text = "Premium Plans",
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
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(GamingRed.copy(alpha = 0.4f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👑", fontSize = 30.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "UNLOCK PREMIUM",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Get full access to all features",
                    color = TextDimGrey,
                    fontSize = 13.sp
                )

                // ── Active Plan Badge ──
                if (subscriptionViewModel.isSubscribed) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(GamingGreen.copy(alpha = 0.15f))
                            .border(1.dp, GamingGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "✅ Active — ${subscriptionViewModel.getRemainingDaysText()}",
                            color = GamingGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Plan Cards ──
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SubscriptionPlan.entries.forEach { plan ->
                    PlanCard(
                        plan = plan,
                        isSelected = selectedPlan == plan,
                        isCurrentPlan = subscriptionViewModel.isSubscribed &&
                                subscriptionViewModel.subscriptionInfo.plan == plan.planId,
                        onClick = { selectedPlan = plan }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Features List ──
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "PREMIUM FEATURES",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    FeatureItem("🔗", "CONNECT Booster — Full access")
                    FeatureItem("🔫", "Gun Sensitivity — All guns unlocked")
                    FeatureItem("📱", "Device Sensitivity — All devices")
                    FeatureItem("⚙", "Generate Sensi — Unlimited")
                    FeatureItem("⚡", "Ultra Boost — Max performance")
                    FeatureItem("🚫", "Ad-free experience")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Subscribe Button ──
            if (!subscriptionViewModel.isSubscribed) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(58.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(GamingRed, Color(0xFFFF2020), GamingDarkRed)
                            )
                        )
                        .clickable(enabled = !subscriptionViewModel.isPaymentLoading && userId != null) {
                            userId?.let {
                                subscriptionViewModel.purchasePlan(context, it, selectedPlan)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (subscriptionViewModel.isPaymentLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            text = "SUBSCRIBE — ₹${selectedPlan.price}",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                // ── Payment Error ──
                subscriptionViewModel.paymentError?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = GamingOrangeRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 20.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Secure payment note ──
            Text(
                text = "🔒 Secure payment via ZapUPI",
                color = TextDimGrey,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PlanCard(
    plan: SubscriptionPlan,
    isSelected: Boolean,
    isCurrentPlan: Boolean,
    onClick: () -> Unit
) {
    val borderBrush = if (isSelected) {
        Brush.horizontalGradient(listOf(GamingRed, GamingOrangeRed))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFF333333), Color(0xFF333333)))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1E1010) else Color(0xFF1A1A1A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio indicator
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(
                        2.dp,
                        if (isSelected) GamingRed else Color(0xFF555555),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(GamingRed)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = plan.displayName,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (plan == SubscriptionPlan.STANDARD) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GamingRed.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "POPULAR",
                                color = GamingRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    if (isCurrentPlan) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GamingGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = GamingGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
                Text(
                    text = "${plan.days} days access",
                    color = TextDimGrey,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "₹${plan.price}",
                color = if (isSelected) GamingRed else Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun FeatureItem(emoji: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = TextGrey,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
