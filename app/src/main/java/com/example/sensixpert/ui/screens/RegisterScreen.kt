package com.example.sensixpert.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.ui.theme.*
import com.example.sensixpert.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

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
                lineTo(w, h * 0.25f)
                quadraticBezierTo(w * 0.5f, h * 0.35f, 0f, h * 0.25f)
                close()
            }
            drawPath(
                path = topFillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(topCrimson, midCrimson, Color(0xFF4A0A0A), Color.Transparent),
                    startY = 0f, endY = h * 0.35f
                )
            )

            val arcLine = Path().apply {
                moveTo(0f, h * 0.25f)
                quadraticBezierTo(w * 0.5f, h * 0.35f, w, h * 0.25f)
            }
            drawPath(path = arcLine, color = arcLineColor.copy(alpha = 0.4f), style = Stroke(width = 2f))
        }

        // ── Content ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // App icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(GamingRed.copy(alpha = 0.4f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎮", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "SensiXpert",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "CREATE YOUR ACCOUNT",
                color = TextDimGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // ── Name Field ──
            AuthTextField(
                value = authViewModel.registerName,
                onValueChange = { authViewModel.registerName = it; authViewModel.clearError() },
                label = "Full Name",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Phone Field (10 digits only) ──
            AuthTextField(
                value = authViewModel.registerPhone,
                onValueChange = {
                    val digits = it.filter { c -> c.isDigit() }.take(10)
                    authViewModel.registerPhone = digits
                    authViewModel.clearError()
                },
                label = "Phone Number (10 digits)",
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Email Field ──
            AuthTextField(
                value = authViewModel.registerEmail,
                onValueChange = { authViewModel.registerEmail = it; authViewModel.clearError() },
                label = "Email Address",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Password Field ──
            AuthTextField(
                value = authViewModel.registerPassword,
                onValueChange = { authViewModel.registerPassword = it; authViewModel.clearError() },
                label = "Password",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible },
                onImeAction = {
                    focusManager.clearFocus()
                    authViewModel.register()
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Promo/Referral Code Field (Optional) ──
            AuthTextField(
                value = authViewModel.registerPromoCode,
                onValueChange = { authViewModel.registerPromoCode = it.uppercase(); authViewModel.clearError() },
                label = "Referral Code (Optional)",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                onImeAction = {
                    focusManager.clearFocus()
                    authViewModel.register()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Error Message ──
            authViewModel.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = GamingOrangeRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── REGISTER BUTTON ──
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
                    .clickable(enabled = !authViewModel.isProcessing) {
                        focusManager.clearFocus()
                        authViewModel.register()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (authViewModel.isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(26.dp),
                        color = Color.White,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        text = "REGISTER",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Login link ──
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = TextGrey,
                    fontSize = 14.sp
                )
                Text(
                    text = "Login",
                    color = GamingRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            authViewModel.clearError()
                            onNavigateToLogin()
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
