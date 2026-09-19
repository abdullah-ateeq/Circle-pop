package com.example.circlepop.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.circlepop.ui.theme.TargetPalette

/** Central animated illustration for Home: soft background orbs, a breathing ripple, and a core circle. */
@Composable
fun HomeHeroIllustration(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "homeHero")
    val rippleScale by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rippleScale"
    )
    val rippleAlpha by infinite.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rippleAlpha"
    )

    Box(modifier = modifier.size(220.dp), contentAlignment = Alignment.Center) {
        BackgroundOrb(offsetX = (-78).dp, offsetY = (-52).dp, size = 34.dp, color = TargetPalette[1], alpha = 0.25f)
        BackgroundOrb(offsetX = 82.dp, offsetY = (-30).dp, size = 24.dp, color = TargetPalette[4], alpha = 0.3f)
        BackgroundOrb(offsetX = (-58).dp, offsetY = 76.dp, size = 20.dp, color = TargetPalette[5], alpha = 0.3f)
        BackgroundOrb(offsetX = 70.dp, offsetY = 68.dp, size = 30.dp, color = TargetPalette[2], alpha = 0.25f)

        Box(
            modifier = Modifier
                .size(150.dp)
                .graphicsLayer {
                    scaleX = rippleScale
                    scaleY = rippleScale
                    alpha = rippleAlpha
                }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .shadow(elevation = 18.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)
                    )
                )
        )
    }
}

@Composable
private fun BackgroundOrb(
    offsetX: Dp,
    offsetY: Dp,
    size: Dp,
    color: Color,
    alpha: Float
) {
    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}
