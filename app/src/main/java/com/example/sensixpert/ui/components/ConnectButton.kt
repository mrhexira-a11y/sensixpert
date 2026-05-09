package com.example.sensixpert.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.data.ConnectionState
import com.example.sensixpert.ui.theme.*

@Composable
fun ConnectButton(
    connectionState: ConnectionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = connectionState == ConnectionState.CONNECTED
    val isTransitioning = connectionState == ConnectionState.CONNECTING ||
            connectionState == ConnectionState.DISCONNECTING

    // Colors based on state
    val corePrimary = if (isConnected) GamingGreen else ButtonCoral
    val corePrimaryLight = if (isConnected) GamingGreen.copy(alpha = 0.8f) else ButtonCoralLight
    val corePrimaryDark = if (isConnected) GamingGreenDark else ButtonDeepRed
    val ringDark = if (isConnected) Color(0xFF0A2A0A) else ButtonRingDark
    val ringOuter = if (isConnected) GamingGreenDark.copy(alpha = 0.5f) else ButtonRingOuter
    val warmGlow = if (isConnected) GamingGreen else WarmGlowAmber

    // Infinite animations
    val infiniteTransition = rememberInfiniteTransition(label = "btn_anim")

    // Pulsating outer rings (slower = less GPU work)
    val ringAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0.08f,
        animationSpec = infiniteRepeatable(tween(2800, easing = LinearEasing), RepeatMode.Restart),
        label = "r1a"
    )
    val ringScale1 by infiniteTransition.animateFloat(
        initialValue = 1.0f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(2800, easing = LinearEasing), RepeatMode.Restart),
        label = "r1s"
    )
    val ringAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.35f, targetValue = 0.04f,
        animationSpec = infiniteRepeatable(tween(2800, easing = LinearEasing, delayMillis = 900), RepeatMode.Restart),
        label = "r2a"
    )
    val ringScale2 by infiniteTransition.animateFloat(
        initialValue = 1.0f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(2800, easing = LinearEasing, delayMillis = 900), RepeatMode.Restart),
        label = "r2s"
    )

    // Connecting spinner
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "spin"
    )

    // Inner glow breath (slower = smoother)
    val glowBreath by infiniteTransition.animateFloat(
        initialValue = 0.35f, targetValue = 0.65f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    val buttonSize = 340.dp

    Box(
        modifier = modifier
            .size(buttonSize)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !isTransitioning,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(buttonSize)) {
            val center = Offset(size.width / 2, size.height / 2)
            val mainRadius = size.minDimension / 2 * 0.52f
            val darkRingRadius = mainRadius + 20f
            val outerLineRadius = darkRingRadius + 6f

            // ─── Pulsating rings (outermost) ───
            drawCircle(
                color = corePrimary.copy(alpha = ringAlpha2),
                radius = outerLineRadius * ringScale2,
                center = center,
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = corePrimary.copy(alpha = ringAlpha1),
                radius = outerLineRadius * ringScale1,
                center = center,
                style = Stroke(width = 2f)
            )

            // ─── Warm bottom glow (amber/orange reflection below button) ───
            val glowCenter = Offset(center.x, center.y + mainRadius * 0.85f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        warmGlow.copy(alpha = glowBreath * 0.35f),
                        warmGlow.copy(alpha = glowBreath * 0.15f),
                        warmGlow.copy(alpha = glowBreath * 0.04f),
                        Color.Transparent
                    ),
                    center = glowCenter,
                    radius = mainRadius * 1.2f
                ),
                radius = mainRadius * 1.2f,
                center = glowCenter
            )

            // ─── Overall radial glow behind everything ───
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        corePrimary.copy(alpha = glowBreath * 0.12f),
                        corePrimary.copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = outerLineRadius * 1.6f
                ),
                radius = outerLineRadius * 1.6f,
                center = center
            )

            // ─── Thin outer ring line ───
            drawCircle(
                color = ringOuter,
                radius = outerLineRadius,
                center = center,
                style = Stroke(width = 1.5f)
            )

            // ─── Thick dark ring (the prominent dark border) ───
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ringDark.copy(alpha = 0.95f),
                        ringDark,
                        ringDark.copy(alpha = 0.85f)
                    ),
                    center = center,
                    radius = darkRingRadius
                ),
                radius = darkRingRadius,
                center = center,
                style = Stroke(width = 24f)
            )

            // ─── Inner thin ring (between dark ring and button fill) ───
            drawCircle(
                color = corePrimaryDark.copy(alpha = 0.6f),
                radius = mainRadius + 2f,
                center = center,
                style = Stroke(width = 1.2f)
            )

            // ─── Main button fill — bright coral-red gradient ───
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        corePrimaryLight,
                        corePrimary,
                        corePrimaryDark,
                        corePrimaryDark.copy(alpha = 0.8f)
                    ),
                    center = Offset(center.x, center.y - mainRadius * 0.20f),
                    radius = mainRadius * 1.2f
                ),
                radius = mainRadius,
                center = center
            )

            // ─── Top light reflection (glass highlight) ───
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.22f),
                        Color.White.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(center.x, center.y - mainRadius * 0.40f),
                    radius = mainRadius * 0.55f
                ),
                radius = mainRadius * 0.55f,
                center = Offset(center.x, center.y - mainRadius * 0.40f)
            )

            // ─── Connecting: spinning arc ───
            if (isTransitioning) {
                drawArc(
                    color = Color.White.copy(alpha = 0.75f),
                    startAngle = spinAngle,
                    sweepAngle = 100f,
                    useCenter = false,
                    style = Stroke(width = 3f),
                    topLeft = Offset(center.x - outerLineRadius, center.y - outerLineRadius),
                    size = androidx.compose.ui.geometry.Size(outerLineRadius * 2, outerLineRadius * 2)
                )
            }
        }

        // ─── Label ───
        Text(
            text = when (connectionState) {
                ConnectionState.DISCONNECTED -> "CONNECT"
                ConnectionState.CONNECTING -> "CONNECTING..."
                ConnectionState.CONNECTED -> "DISCONNECT"
                ConnectionState.DISCONNECTING -> "STOPPING..."
            },
            color = TextWhite,
            fontSize = when (connectionState) {
                ConnectionState.DISCONNECTED -> 26.sp
                ConnectionState.CONNECTED -> 20.sp
                else -> 16.sp
            },
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
    }
}
