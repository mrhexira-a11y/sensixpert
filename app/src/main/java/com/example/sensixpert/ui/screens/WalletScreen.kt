package com.example.sensixpert.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.ui.theme.*
import com.example.sensixpert.viewmodel.ReferralViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WalletScreen(
    referralViewModel: ReferralViewModel,
    userId: String?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var withdrawAmount by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }

    // Load wallet data
    LaunchedEffect(userId) {
        userId?.let { referralViewModel.loadWalletData(it) }
    }

    // Show messages
    referralViewModel.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            referralViewModel.clearMessages()
        }
    }
    referralViewModel.successMessage?.let { msg ->
        LaunchedEffect(msg) {
            Toast.makeText(context, "✅ $msg", Toast.LENGTH_LONG).show()
            referralViewModel.clearMessages()
            withdrawAmount = ""
            upiId = ""
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
                    text = "Wallet",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(28.dp))
            }

            // ── Balance Card ──
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AVAILABLE BALANCE",
                        color = TextDimGrey,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "₹${String.format("%.2f", referralViewModel.walletInfo.balance)}",
                        color = GamingGreen,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WalletStatItem(
                            label = "Total Earned",
                            value = "₹${String.format("%.0f", referralViewModel.walletInfo.totalEarnings)}",
                            color = Color(0xFF448AFF)
                        )
                        WalletStatItem(
                            label = "Withdrawn",
                            value = "₹${String.format("%.0f", referralViewModel.walletInfo.totalWithdrawn)}",
                            color = GamingOrangeRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Withdrawal Form ──
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
                        text = "WITHDRAW FUNDS",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Minimum withdrawal: ₹50",
                        color = TextDimGrey,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Amount input
                    OutlinedTextField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Amount (₹)", color = TextDimGrey) },
                        placeholder = { Text("Enter amount", color = TextDimGrey.copy(alpha = 0.5f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingRed,
                            unfocusedBorderColor = Color(0xFF333333),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = GamingRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // UPI ID input
                    OutlinedTextField(
                        value = upiId,
                        onValueChange = { upiId = it },
                        label = { Text("UPI ID", color = TextDimGrey) },
                        placeholder = { Text("e.g. yourname@paytm", color = TextDimGrey.copy(alpha = 0.5f)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GamingRed,
                            unfocusedBorderColor = Color(0xFF333333),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = GamingRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Withdraw button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(GamingRed, Color(0xFFFF2020), GamingDarkRed)
                                )
                            )
                            .clickable(enabled = !referralViewModel.isWithdrawing) {
                                val amt = withdrawAmount.toDoubleOrNull() ?: 0.0
                                userId?.let {
                                    referralViewModel.requestWithdrawal(it, amt, upiId)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (referralViewModel.isWithdrawing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Text(
                                text = "REQUEST WITHDRAWAL",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "⏱ Processing time: 24-72 hours after admin approval",
                        color = TextDimGrey,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Withdrawal History ──
            if (referralViewModel.withdrawals.isNotEmpty()) {
                Text(
                    text = "WITHDRAWAL HISTORY",
                    color = TextDimGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                referralViewModel.withdrawals.forEach { w ->
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
                                    text = "₹${String.format("%.0f", w.amount)}",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = w.upiId,
                                    color = TextDimGrey,
                                    fontSize = 12.sp
                                )
                                if (w.requestedAt > 0) {
                                    Text(
                                        text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(w.requestedAt)),
                                        color = TextDimGrey,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            val (statusText, statusColor) = when (w.status) {
                                "approved", "completed" -> "✅ Approved" to GamingGreen
                                "rejected" -> "❌ Rejected" to GamingOrangeRed
                                else -> "⏳ Pending" to Color(0xFFFFC107)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(statusColor.copy(alpha = 0.12f))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = statusText,
                                    color = statusColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun WalletStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextDimGrey,
            fontSize = 11.sp
        )
    }
}
