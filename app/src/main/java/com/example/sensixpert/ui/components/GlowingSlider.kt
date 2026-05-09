package com.example.sensixpert.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensixpert.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlowingSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: IntRange = 0..100
) {
    val animatedValue by animateFloatAsState(
        targetValue = value.toFloat(),
        animationSpec = tween(durationMillis = 500),
        label = "slider_anim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = TextWhite,
                letterSpacing = 1.5.sp
            )
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(GamingDarkRed, GamingRed)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Slider(
            value = animatedValue,
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = GamingRed,
                activeTrackColor = GamingRed,
                inactiveTrackColor = SliderTrackInactive,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = GamingRed,
                            spotColor = GamingRed
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(GamingRed, GamingDarkRed)
                            )
                        )
                )
            },
            track = { sliderState ->
                val fraction = (animatedValue - valueRange.first) / (valueRange.last - valueRange.first)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(SliderTrackInactive)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(3.dp),
                                ambientColor = SliderGlow,
                                spotColor = SliderGlow
                            )
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(GamingDarkRed, GamingRed)
                                )
                            )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
