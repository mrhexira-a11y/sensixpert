package com.example.sensixpert.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.ui.theme.*
import com.example.sensixpert.viewmodel.ReferralViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReferEarnScreen(
    referralViewModel: ReferralViewModel,
    userId: String?,
    isSubscribed: Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToWallet: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Load data on first open
    LaunchedEffect(userId) {
        userId?.let { referralViewModel.loadReferralData(it) }
    }

    // Show error/success toasts
    referralViewModel.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            referralViewModel.clearMessages()
        }
    }
    referralViewModel.successMessage?.let { msg ->
        LaunchedEffect(msg) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            referralViewModel.clearMessages()
        }
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
                moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, h * 0.22f)
                quadraticBezierTo(w * 0.5f, h * 0.32f, 0f, h * 0.22f); close()
            }
            drawPath(
                path = topFillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(topCrimson, midCrimson, Color(0xFF4A0A0A), Color.Transparent),
                    startY = 0f, endY = h * 0.32f
                )
            )
            val arcLine = Path().apply {
                moveTo(0f, h * 0.22f); quadraticBezierTo(w * 0.5f, h * 0.32f, w, h * 0.22f)
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onNavigateBack() }
                )
                Text(
                    text = "Refer & Earn",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(28.dp))
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
                    Text(text = "🎁", fontSize = 30.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "EARN ₹15 — ₹120 PER REFERRAL",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Share your code, earn when friends subscribe",
                    color = TextDimGrey,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Referral Code Card ──
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "YOUR REFERRAL CODE",
                        color = TextDimGrey,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (referralViewModel.referralCode.isNotEmpty()) {
                        // Show code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0D0D0D))
                                .border(
                                    1.dp,
                                    Brush.horizontalGradient(listOf(GamingRed, GamingOrangeRed)),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(referralViewModel.referralCode))
                                    Toast
                                        .makeText(context, "Code copied!", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                .padding(horizontal = 32.dp, vertical = 16.dp)
                        ) {
                            Text(
                                text = referralViewModel.referralCode,
                                color = GamingRed,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 4.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap to copy",
                            color = TextDimGrey,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Share button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(GamingRed, Color(0xFFFF2020), GamingDarkRed)
                                    )
                                )
                                .clickable {
                                    referralViewModel.shareReferralCode(context)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📤  SHARE CODE",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    } else {
                        // Generate code button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(GamingRed, Color(0xFFFF2020), GamingDarkRed)
                                    )
                                )
                                .clickable(enabled = !referralViewModel.isGeneratingCode) {
                                    userId?.let { referralViewModel.generateCode(it) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (referralViewModel.isGeneratingCode) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Text(
                                    text = "🔑  GENERATE MY CODE",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Stats Cards ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReferralStatCard(
                    modifier = Modifier.weight(1f),
                    label = "Total\nReferred",
                    value = "${referralViewModel.referralInfo.totalReferrals}",
                    color = Color(0xFF448AFF)
                )
                ReferralStatCard(
                    modifier = Modifier.weight(1f),
                    label = "Successful\nReferrals",
                    value = "${referralViewModel.referralInfo.successfulReferrals}",
                    color = GamingGreen
                )
                ReferralStatCard(
                    modifier = Modifier.weight(1f),
                    label = "Total\nEarned",
                    value = "₹${String.format("%.0f", referralViewModel.referralInfo.totalEarnings)}",
                    color = GamingOrangeRed
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Wallet Card ──
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { onNavigateToWallet() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "💰 WALLET BALANCE",
                            color = TextDimGrey,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹${String.format("%.2f", referralViewModel.walletInfo.balance)}",
                            color = GamingGreen,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(GamingRed.copy(alpha = 0.15f))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "WITHDRAW →",
                            color = GamingRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── How It Works ──
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
                        text = "HOW IT WORKS",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HowItWorksStep("1️⃣", "Share your unique referral code with friends")
                    HowItWorksStep("2️⃣", "Friend registers using your code")
                    HowItWorksStep("3️⃣", "When they buy any plan, you earn ₹15 to ₹120 per referral")
                    HowItWorksStep("4️⃣", "Withdraw to UPI (minimum ₹50)")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Recent Referrals ──
            if (referralViewModel.referralLogs.isNotEmpty()) {
                Text(
                    text = "RECENT REFERRALS",
                    color = TextDimGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                referralViewModel.referralLogs.forEach { log ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF151515))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "User: ${log.referredUserId.take(10)}…",
                                    color = TextGrey,
                                    fontSize = 13.sp
                                )
                                if (log.createdAt > 0) {
                                    Text(
                                        text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(log.createdAt)),
                                        color = TextDimGrey,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                if (log.commission > 0) {
                                    Text(
                                        text = "+₹${String.format("%.0f", log.commission)}",
                                        color = GamingGreen,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (log.status == "completed") GamingGreen.copy(alpha = 0.15f)
                                            else Color(0xFFFFC107).copy(alpha = 0.15f)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (log.status == "completed") "✅ Earned" else "⏳ Pending",
                                        color = if (log.status == "completed") GamingGreen else Color(0xFFFFC107),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // ── Loading overlay ──
        if (referralViewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GamingRed)
            }
        }
    }
}

@Composable
private fun ReferralStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = color,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = TextDimGrey,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun HowItWorksStep(emoji: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = TextGrey,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 18.sp
        )
    }
}
