package com.example.sensixpert.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.data.SupportMessage
import com.example.sensixpert.data.SupportRepository
import com.example.sensixpert.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    userId: String?,
    userName: String?,
    userEmail: String?,
    onNavigateBack: () -> Unit
) {
    val supportRepository = remember { SupportRepository() }
    var messages by remember { mutableStateOf<List<SupportMessage>>(emptyList()) }
    var messageText by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Observe messages in real-time
    LaunchedEffect(userId) {
        if (userId != null) {
            supportRepository.markAdminMessagesRead(userId)
            supportRepository.observeMessages(userId).collectLatest { msgs ->
                messages = msgs
            }
        }
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = Color(0xFF0A0A0A),
        // ── Fixed Top Bar — won't move with keyboard ──
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
                        // Back button
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
                            // Glow ring
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
                                // Animated green dot
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
                                    text = "Online • Typically replies within hours",
                                    color = GamingGreen.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        },
        // ── Bottom Input Bar ──
        bottomBar = {
            Surface(
                color = Color(0xFF0F0F0F),
                shadowElevation = 12.dp,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Text field
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = {
                            Text(
                                text = "Type a message...",
                                color = Color(0xFF555555),
                                fontSize = 14.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            cursorColor = GamingRed,
                            focusedBorderColor = GamingRed.copy(alpha = 0.4f),
                            unfocusedBorderColor = Color(0xFF222222),
                            focusedContainerColor = Color(0xFF161616),
                            unfocusedContainerColor = Color(0xFF161616)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp, max = 120.dp),
                        maxLines = 4,
                        singleLine = false
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Send button with gradient
                    val canSend = messageText.isNotBlank() && !isSending && userId != null
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(
                                elevation = if (canSend) 8.dp else 0.dp,
                                shape = CircleShape,
                                ambientColor = GamingRed.copy(alpha = 0.3f),
                                spotColor = GamingRed.copy(alpha = 0.3f)
                            )
                            .clip(CircleShape)
                            .background(
                                if (canSend)
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFFE81616),
                                            Color(0xFFAA0000)
                                        )
                                    )
                                else
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF252525),
                                            Color(0xFF1A1A1A)
                                        )
                                    )
                            )
                            .clickable(enabled = canSend) {
                                val text = messageText.trim()
                                if (text.isNotEmpty() && userId != null) {
                                    isSending = true
                                    messageText = ""
                                    scope.launch {
                                        try {
                                            supportRepository.sendMessage(
                                                userId = userId,
                                                userName = userName ?: "User",
                                                userEmail = userEmail ?: "",
                                                text = text
                                            )
                                        } catch (_: Exception) {
                                        } finally {
                                            isSending = false
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (canSend) Color.White else Color(0xFF444444),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        // ── Chat Messages ──
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Welcome card
            item {
                WelcomeCard()
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Group messages by date
            val grouped = messages.groupBy { msg ->
                if (msg.timestamp > 0) {
                    val cal = Calendar.getInstance().apply { timeInMillis = msg.timestamp }
                    val today = Calendar.getInstance()
                    when {
                        cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                                cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> "Today"
                        else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            .format(Date(msg.timestamp))
                    }
                } else ""
            }

            grouped.forEach { (date, msgs) ->
                // Date separator
                if (date.isNotEmpty()) {
                    item {
                        DateSeparator(date = date)
                    }
                }
                items(msgs, key = { it.id }) { message ->
                    ChatBubble(message = message)
                }
            }
        }
    }
}

@Composable
private fun WelcomeCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1A0808),
                        Color(0xFF140505),
                        Color(0xFF0F0F0F)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon with glow
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                GamingRed.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFCC1818), Color(0xFF8B0000))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💬", fontSize = 22.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "How can we help?",
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Our team is here to assist you.\nWe typically respond within a few hours.",
                color = Color(0xFF777777),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick info pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoPill(text = "🕐 Fast Reply")
                InfoPill(text = "🔒 Secure")
                InfoPill(text = "🎮 24/7")
            }
        }
    }
}

@Composable
private fun InfoPill(text: String) {
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

@Composable
private fun DateSeparator(date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(0.5.dp)
                .background(Color(0xFF222222))
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF151515))
                .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
            Text(
                text = date,
                color = Color(0xFF666666),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(0.5.dp)
                .background(Color(0xFF222222))
        )
    }
}

@Composable
private fun ChatBubble(message: SupportMessage) {
    val isUser = message.sender == "user"
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Admin label
        if (!isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFCC1818), Color(0xFF8B0000))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SensiXpert Team",
                    color = GamingRed.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Box(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    )
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    )
                )
                .background(
                    if (isUser)
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFD41414),
                                Color(0xFFAA0000)
                            )
                        )
                    else
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1C1C1C),
                                Color(0xFF171717)
                            )
                        )
                )
                .then(
                    if (!isUser) Modifier
                        .background(Color.Transparent)
                    else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (message.timestamp > 0)
                            timeFormat.format(Date(message.timestamp))
                        else "",
                        color = if (isUser)
                            Color.White.copy(alpha = 0.55f)
                        else
                            Color(0xFF555555),
                        fontSize = 10.sp
                    )

                    if (isUser) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (message.read) "✓✓" else "✓",
                            color = if (message.read)
                                Color(0xFF64B5F6)
                            else
                                Color.White.copy(alpha = 0.45f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
