package com.example.sensixpert.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.ui.theme.*

enum class BottomIconType {
    GUN, PLUS, PHONE
}

@Composable
fun BottomCircleButton(
    iconType: BottomIconType,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bc_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bc_pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier.size(size + 16.dp), // extra space for glow
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size + 16.dp)) {
                val center = Offset(this.size.width / 2, this.size.height / 2)
                val outerRadius = this.size.minDimension / 2 - 8f
                val ringWidth = 3f

                // ─── OUTER WARM GLOW (big soft halo) ───
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF4030).copy(alpha = glowAlpha * 0.25f),
                            Color(0xFFFF6B20).copy(alpha = glowAlpha * 0.12f),
                            Color(0xFFFF4030).copy(alpha = glowAlpha * 0.05f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = outerRadius * 1.45f
                    ),
                    radius = outerRadius * 1.45f,
                    center = center
                )

                // ─── MEDIUM GLOW (tighter around ring) ───
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF4030).copy(alpha = glowAlpha * 0.18f),
                            Color(0xFFFF6040).copy(alpha = glowAlpha * 0.10f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = outerRadius * 1.20f
                    ),
                    radius = outerRadius * 1.20f,
                    center = center
                )

                // ─── MAIN CIRCLE RING (gradient: red -> coral -> orange) ───
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFFFF3525),
                            Color(0xFFFF5535),
                            Color(0xFFFF7040),
                            Color(0xFFFF5535),
                            Color(0xFFFF3525),
                            Color(0xFFCC2218),
                            Color(0xFFFF3525)
                        ),
                        center = center
                    ),
                    radius = outerRadius,
                    center = center,
                    style = Stroke(width = ringWidth)
                )

                // ─── Inner subtle ring (depth effect) ───
                drawCircle(
                    color = Color(0xFFFF3525).copy(alpha = 0.25f),
                    radius = outerRadius - 5f,
                    center = center,
                    style = Stroke(width = 1f)
                )

                // ─── DRAW ICON ───
                val iconColor = Color.White.copy(alpha = 0.92f)
                val iconScale = this.size.minDimension

                when (iconType) {
                    BottomIconType.GUN -> {
                        // ── FILLED pistol silhouette (like reference) ──
                        val s = iconScale * 0.38f
                        val ox = center.x - s * 0.50f
                        val oy = center.y - s * 0.42f

                        val gunPath = Path().apply {
                            // Barrel top-left
                            moveTo(ox + s * 0.0f, oy + s * 0.15f)
                            // Barrel top
                            lineTo(ox + s * 0.75f, oy + s * 0.0f)
                            // Barrel front tip (muzzle)
                            lineTo(ox + s * 0.95f, oy + s * 0.05f)
                            lineTo(ox + s * 0.95f, oy + s * 0.22f)
                            // Barrel bottom front
                            lineTo(ox + s * 0.75f, oy + s * 0.28f)
                            // Slide bottom to trigger area
                            lineTo(ox + s * 0.55f, oy + s * 0.33f)
                            // Trigger guard front
                            lineTo(ox + s * 0.55f, oy + s * 0.48f)
                            lineTo(ox + s * 0.62f, oy + s * 0.48f)
                            // Trigger guard curve down
                            lineTo(ox + s * 0.65f, oy + s * 0.62f)
                            // Trigger guard bottom
                            lineTo(ox + s * 0.42f, oy + s * 0.62f)
                            // Trigger (the actual trigger piece)
                            lineTo(ox + s * 0.42f, oy + s * 0.50f)
                            lineTo(ox + s * 0.48f, oy + s * 0.50f)
                            lineTo(ox + s * 0.48f, oy + s * 0.38f)
                            // Grip front
                            lineTo(ox + s * 0.38f, oy + s * 0.38f)
                            // Grip bottom (angled)
                            lineTo(ox + s * 0.28f, oy + s * 0.88f)
                            // Grip bottom back
                            lineTo(ox + s * 0.08f, oy + s * 0.85f)
                            // Grip back
                            lineTo(ox + s * 0.12f, oy + s * 0.38f)
                            // Frame back to barrel
                            lineTo(ox + s * 0.0f, oy + s * 0.33f)
                            close()
                        }
                        // Draw filled white gun
                        drawPath(
                            path = gunPath,
                            color = iconColor,
                            style = Fill
                        )
                    }

                    BottomIconType.PLUS -> {
                        // ── Thick clean + icon ──
                        val armLength = iconScale * 0.16f
                        val strokeW = iconScale * 0.022f

                        // Horizontal line
                        drawLine(
                            color = iconColor,
                            start = Offset(center.x - armLength, center.y),
                            end = Offset(center.x + armLength, center.y),
                            strokeWidth = strokeW,
                            cap = StrokeCap.Round
                        )
                        // Vertical line
                        drawLine(
                            color = iconColor,
                            start = Offset(center.x, center.y - armLength),
                            end = Offset(center.x, center.y + armLength),
                            strokeWidth = strokeW,
                            cap = StrokeCap.Round
                        )
                    }

                    BottomIconType.PHONE -> {
                        // ── Clean smartphone outline (like reference) ──
                        val phoneW = iconScale * 0.20f
                        val phoneH = iconScale * 0.32f
                        val px = center.x - phoneW / 2
                        val py = center.y - phoneH / 2
                        val cornerR = phoneW * 0.22f

                        // Phone body (rounded rect outline)
                        val phonePath = Path().apply {
                            addRoundRect(
                                RoundRect(
                                    left = px,
                                    top = py,
                                    right = px + phoneW,
                                    bottom = py + phoneH,
                                    cornerRadius = CornerRadius(cornerR, cornerR)
                                )
                            )
                        }
                        drawPath(
                            path = phonePath,
                            color = iconColor,
                            style = Stroke(width = iconScale * 0.014f)
                        )

                        // Screen area (inner rect)
                        val screenMX = phoneW * 0.14f
                        val screenMTop = phoneH * 0.16f
                        val screenMBot = phoneH * 0.20f
                        val screenCorner = cornerR * 0.3f
                        drawRoundRect(
                            color = iconColor.copy(alpha = 0.55f),
                            topLeft = Offset(px + screenMX, py + screenMTop),
                            size = Size(
                                phoneW - screenMX * 2,
                                phoneH - screenMTop - screenMBot
                            ),
                            cornerRadius = CornerRadius(screenCorner, screenCorner),
                            style = Stroke(width = iconScale * 0.010f)
                        )

                        // Bottom indicator (home bar)
                        val indicatorW = phoneW * 0.28f
                        val indicatorY = py + phoneH - phoneH * 0.09f
                        drawLine(
                            color = iconColor.copy(alpha = 0.6f),
                            start = Offset(center.x - indicatorW / 2, indicatorY),
                            end = Offset(center.x + indicatorW / 2, indicatorY),
                            strokeWidth = iconScale * 0.010f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }

        if (label.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = TextGrey.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.3.sp
            )
        }
    }
}

// Backward-compatible overload
@Composable
fun BottomCircleButton(
    icon: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    iconSize: Int = 20
) {
    val iconType = when {
        icon.contains("🔫") || icon.lowercase().contains("gun") -> BottomIconType.GUN
        icon.contains("📱") || icon.lowercase().contains("phone") -> BottomIconType.PHONE
        icon == "+" -> BottomIconType.PLUS
        else -> BottomIconType.PLUS
    }
    BottomCircleButton(
        iconType = iconType,
        label = label,
        onClick = onClick,
        modifier = modifier,
        size = size
    )
}
