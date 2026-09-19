package com.example.circlepop.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.circlepop.ui.theme.TargetPalette
import kotlin.math.cos
import kotlin.math.sin

/** Transient one-shot data describing a just-popped target's location and appearance. */
data class PopEffect(
    val id: Long,
    val xFraction: Float,
    val yFraction: Float,
    val sizeDp: Float,
    val colorIndex: Int
)

private const val POP_EFFECT_DURATION_MS = 380

/** Plays a short expanding-ring + tiny-dots + floating "+1" celebration, then removes itself. */
@Composable
fun PopEffectView(
    effect: PopEffect,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(effect.id) {
        progress.animateTo(1f, tween(POP_EFFECT_DURATION_MS, easing = FastOutSlowInEasing))
        onFinished()
    }

    val color = TargetPalette[effect.colorIndex % TargetPalette.size]
    val p = progress.value
    val boxSize = effect.sizeDp.dp + 44.dp

    Box(
        modifier = modifier.size(boxSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(boxSize)) {
            val baseRadius = effect.sizeDp.dp.toPx() / 2f

            val ringRadius = baseRadius * (1f + p * 0.9f)
            drawCircle(
                color = color.copy(alpha = (1f - p) * 0.6f),
                radius = ringRadius,
                style = Stroke(width = 3.dp.toPx())
            )

            val coreScale = if (p < 0.25f) {
                1f + p * 0.8f
            } else {
                (1f - (p - 0.25f) / 0.75f).coerceAtLeast(0f)
            }
            drawCircle(
                color = color.copy(alpha = 1f - p),
                radius = baseRadius * coreScale
            )

            val dotCount = 6
            val dotDistance = baseRadius * (0.6f + p * 1.6f)
            val dotRadius = 2.5.dp.toPx() * (1f - p * 0.5f)
            repeat(dotCount) { i ->
                val angle = (2 * Math.PI / dotCount) * i
                val dotCenter = Offset(
                    x = center.x + (cos(angle) * dotDistance).toFloat(),
                    y = center.y + (sin(angle) * dotDistance).toFloat()
                )
                drawCircle(
                    color = color.copy(alpha = (1f - p) * 0.8f),
                    radius = dotRadius,
                    center = dotCenter
                )
            }
        }

        Text(
            text = "+1",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color,
            modifier = Modifier.graphicsLayer {
                translationY = -p * 60.dp.toPx()
                alpha = 1f - p
            }
        )
    }
}
