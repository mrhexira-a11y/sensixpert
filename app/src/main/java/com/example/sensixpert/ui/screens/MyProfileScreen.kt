package com.example.sensixpert.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    userId: String?,
    userName: String?,
    userEmail: String?,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    // Fetch full user data from Firestore
    var phone by remember { mutableStateOf("—") }
    var joinedDate by remember { mutableStateOf("—") }
    var subscriptionPlan by remember { mutableStateOf("Free") }
    var subscriptionStatus by remember { mutableStateOf("Inactive") }
    var subscriptionExpiry by remember { mutableStateOf("—") }
    var referralCode by remember { mutableStateOf("—") }
    var referredBy by remember { mutableStateOf("—") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                val doc = FirebaseFirestore.getInstance()
                    .collection("users").document(userId).get().await()
                if (doc.exists()) {
                    phone = doc.getString("phone") ?: "—"
                    referralCode = doc.getString("referralCode") ?: "—"
                    referredBy = doc.getString("referredBy") ?: "—"

                    val createdAt = doc.getTimestamp("createdAt")
                    joinedDate = if (createdAt != null) {
                        val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                        sdf.format(createdAt.toDate())
                    } else "—"

                    val sub = doc.get("subscription") as? Map<*, *>
                    if (sub != null) {
                        subscriptionPlan = (sub["plan"] as? String)?.let {
                            when (it) {
                                "7days" -> "7 Days"
                                "monthly" -> "1 Month"
                                "3months" -> "3 Months"
                                else -> it
                            }
                        } ?: "Free"
                        subscriptionStatus = (sub["status"] as? String)?.replaceFirstChar { it.uppercase() } ?: "Inactive"
                        val endDate = sub["endDate"] as? Long
                        subscriptionExpiry = if (endDate != null && endDate > System.currentTimeMillis()) {
                            val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                            sdf.format(java.util.Date(endDate))
                        } else "Expired"
                    }
                }
            } catch (e: Exception) {
                // Silently handle
            }
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GamingBackground)
    ) {
        // ── Top Bar ──
        TopAppBar(
            title = {
                Text(
                    "My Profile",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = GamingBackground,
                titleContentColor = Color.White
            )
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GamingOrangeRed)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Avatar ──
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GamingOrangeRed, Color(0xFFFF6B35))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (userName?.firstOrNull()?.uppercase() ?: "U"),
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userName ?: "User",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userEmail ?: "",
                    color = TextGrey,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── Account Info ──
                ProfileSectionHeader("ACCOUNT INFO")
                ProfileInfoRow("Name", userName ?: "—")
                ProfileInfoRow("Email", userEmail ?: "—")
                ProfileInfoRow("Phone", phone)
                ProfileInfoRow("User ID", userId?.take(16)?.plus("…") ?: "—")
                ProfileInfoRow("Joined", joinedDate)

                Spacer(modifier = Modifier.height(16.dp))

                // ── Subscription ──
                ProfileSectionHeader("SUBSCRIPTION")
                ProfileInfoRow("Plan", subscriptionPlan)
                ProfileInfoRow("Status", subscriptionStatus)
                ProfileInfoRow("Expires", subscriptionExpiry)

                Spacer(modifier = Modifier.height(16.dp))

                // ── Referral ──
                ProfileSectionHeader("REFERRAL")
                ProfileInfoRow("My Code", referralCode)
                ProfileInfoRow("Referred By", referredBy)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileSectionHeader(title: String) {
    Text(
        text = title,
        color = GamingOrangeRed,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, top = 4.dp)
    )
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(CardDark, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
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
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
