package com.example.sensixpert.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    userId: String?,
    userName: String?,
    userEmail: String?,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var supportLink by remember { mutableStateOf("https://t.me/sensixpert") }
    var linkLabel by remember { mutableStateOf("Contact us on Telegram") }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch support link from Firestore (1 read only)
    LaunchedEffect(Unit) {
        try {
            FirebaseFirestore.getInstance()
                .collection("app_config")
                .document("support")
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        doc.getString("link")?.let { supportLink = it }
                        doc.getString("label")?.let { linkLabel = it }
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } catch (_: Exception) {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = Color(0xFF0A0A0A),
        topBar = {
            Surface(
                color = Color.Transparent,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1A0505),
                                    Color(0xFF140404),
                                    Color(0xFF0E0E0E)
                                )
                            )
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Support avatar with glow
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                GamingRed.copy(alpha = 0.3f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFCC1818),
                                                Color(0xFF8B0000)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "S",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "SensiXpert Support",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val pulseAlpha by rememberInfiniteTransition(label = "pulse")
                                    .animateFloat(
                                        initialValue = 0.4f,
                                        targetValue = 1f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1200),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "dotPulse"
                                    )
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(GamingGreen.copy(alpha = pulseAlpha))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Available • Tap below to reach us",
                                    color = GamingGreen.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon with glow
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GamingRed.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFCC1818), Color(0xFF8B0000))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💬", fontSize = 36.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Need Help?",
                    color = TextWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Our support team is ready to help you\nwith any questions or issues.",
                    color = Color(0xFF888888),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Info pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoChip(text = "🕐 Fast Reply")
                    InfoChip(text = "🔒 Secure")
                    InfoChip(text = "🎮 24/7")
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Contact card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1A0808),
                                    Color(0xFF140505)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = linkLabel,
                            color = Color(0xFFAAAAAA),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Contact Now button
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = GamingRed,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(
                                        elevation = 12.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        ambientColor = GamingRed.copy(alpha = 0.4f),
                                        spotColor = GamingRed.copy(alpha = 0.4f)
                                    )
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(0xFFE81616),
                                                Color(0xFFAA0000)
                                            )
                                        )
                                    )
                                    .clickable {
                                        try {
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(supportLink)
                                            )
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CONTACT NOW",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "You'll be redirected to our support channel",
                    color = Color(0xFF555555),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1A1A1A))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF999999),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
