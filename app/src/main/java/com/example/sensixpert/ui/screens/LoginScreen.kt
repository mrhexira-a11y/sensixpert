package com.example.sensixpert.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.ui.theme.*
import com.example.sensixpert.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit
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
                lineTo(w, h * 0.30f)
                quadraticBezierTo(w * 0.5f, h * 0.42f, 0f, h * 0.30f)
                close()
            }
            drawPath(
                path = topFillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(topCrimson, midCrimson, Color(0xFF4A0A0A), Color.Transparent),
                    startY = 0f, endY = h * 0.42f
                )
            )

            val arcLine = Path().apply {
                moveTo(0f, h * 0.30f)
                quadraticBezierTo(w * 0.5f, h * 0.42f, w, h * 0.30f)
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
            Spacer(modifier = Modifier.height(60.dp))

            // App icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(GamingRed.copy(alpha = 0.4f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎮", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SensiXpert",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "LOGIN TO YOUR ACCOUNT",
                color = TextDimGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Email Field ──
            AuthTextField(
                value = authViewModel.loginEmail,
                onValueChange = { authViewModel.loginEmail = it; authViewModel.clearError() },
                label = "Email Address",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Password Field ──
            AuthTextField(
                value = authViewModel.loginPassword,
                onValueChange = { authViewModel.loginPassword = it; authViewModel.clearError() },
                label = "Password",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible },
                onImeAction = {
                    focusManager.clearFocus()
                    authViewModel.login()
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

            Spacer(modifier = Modifier.height(28.dp))

            // ── LOGIN BUTTON ──
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
                        authViewModel.login()
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
                        text = "LOGIN",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Register link ──
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = TextGrey,
                    fontSize = 14.sp
                )
                Text(
                    text = "Register",
                    color = GamingRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            authViewModel.clearError()
                            onNavigateToRegister()
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════
// REUSABLE AUTH TEXT FIELD
// ═══════════════════════════════════════════════════════════
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label, color = TextDimGrey, fontSize = 13.sp)
        },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = GamingRed,
            focusedBorderColor = GamingRed,
            unfocusedBorderColor = Color(0xFF333333),
            focusedLabelColor = GamingRed,
            unfocusedLabelColor = TextDimGrey
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() }
        ),
        trailingIcon = if (isPassword) {
            {
                Text(
                    text = if (passwordVisible) "🙈" else "👁",
                    fontSize = 18.sp,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onTogglePassword?.invoke() }
                    )
                )
            }
        } else null
    )
}
