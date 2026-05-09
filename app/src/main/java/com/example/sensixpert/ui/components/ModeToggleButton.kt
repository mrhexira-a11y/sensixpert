package com.example.sensixpert.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.ui.theme.*

@Composable
fun ModeToggleButton(
    label: String,
    icon: String,
    isActive: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isActive) accentColor else accentColor.copy(alpha = 0.5f),
        animationSpec = tween(300),
        label = "mode_border"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isActive) accentColor.copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = tween(300),
        label = "mode_bg"
    )

    // Subtle glow pulse when active
    val infiniteTransition = rememberInfiniteTransition(label = "mode_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mode_glow_alpha"
    )

    Box(
        modifier = modifier
            .then(
                if (isActive) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(25.dp),
                        ambientColor = accentColor.copy(alpha = glowAlpha * 0.5f),
                        spotColor = accentColor.copy(alpha = glowAlpha * 0.5f)
                    )
                } else Modifier
            )
            .clip(RoundedCornerShape(25.dp))
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(25.dp)
            )
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                color = if (isActive) accentColor else TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = icon,
                fontSize = 14.sp
            )
        }
    }
}
