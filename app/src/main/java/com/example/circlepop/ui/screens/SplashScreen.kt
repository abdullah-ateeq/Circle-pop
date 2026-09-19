package com.example.circlepop.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.circlepop.ui.theme.TargetPalette
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

private const val ANIMATION_DURATION_MS = 1500
private const val HOLD_AFTER_MS = 350L

private fun remap(value: Float, start: Float, end: Float): Float =
    ((value - start) / (end - start)).coerceIn(0f, 1f)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(ANIMATION_DURATION_MS, easing = LinearEasing))
        delay(HOLD_AFTER_MS)
        onFinished()
    }

    val p = progress.value

    val orbitAlpha = FastOutSlowInEasing.transform(remap(p, 0f, 0.15f))
    val orbitScale = FastOutSlowInEasing.transform(remap(p, 0f, 0.2f))

    val expandScale = FastOutSlowInEasing.transform(remap(p, 0.05f, 0.45f))
    val burstShrink = remap(p, 0.45f, 0.62f)
    val coreScale = if (p < 0.45f) expandScale else (1f - burstShrink)
    val coreAlpha = if (p < 0.45f) 1f else (1f - burstShrink)

    val burstProgress = remap(p, 0.45f, 0.78f)
    val burstAlpha = 1f - burstProgress

    val titleAlpha = FastOutSlowInEasing.transform(remap(p, 0.55f, 0.9f))
    val titleScale = 0.7f + 0.3f * FastOutSlowInEasing.transform(remap(p, 0.55f, 0.9f))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(180.dp)) {
                val baseRadius = 34.dp.toPx()

                if (orbitAlpha > 0f) {
                    val orbitDots = 5
                    repeat(orbitDots) { i ->
                        val angle = (2 * Math.PI / orbitDots) * i - Math.PI / 2
                        val dist = baseRadius * 1.9f * orbitScale
                        val dotCenter = Offset(
                            x = center.x + (cos(angle) * dist).toFloat(),
                            y = center.y + (sin(angle) * dist).toFloat()
                        )
                        drawCircle(
                            color = TargetPalette[i % TargetPalette.size].copy(alpha = orbitAlpha * 0.9f),
                            radius = 7.dp.toPx() * orbitScale,
                            center = dotCenter
                        )
                    }
                }

                if (coreAlpha > 0f) {
                    drawCircle(
                        color = TargetPalette[1].copy(alpha = coreAlpha),
                        radius = baseRadius * coreScale.coerceAtLeast(0f)
                    )
                }

                if (burstAlpha > 0f && p >= 0.45f) {
                    val burstDots = 8
                    repeat(burstDots) { i ->
                        val angle = (2 * Math.PI / burstDots) * i
                        val dist = baseRadius * (0.4f + burstProgress * 1.6f)
                        val dotCenter = Offset(
                            x = center.x + (cos(angle) * dist).toFloat(),
                            y = center.y + (sin(angle) * dist).toFloat()
                        )
                        drawCircle(
                            color = TargetPalette[(i + 2) % TargetPalette.size].copy(alpha = burstAlpha),
                            radius = 5.dp.toPx() * (1f - burstProgress * 0.4f),
                            center = dotCenter
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier.graphicsLayer {
                translationY = 160.dp.toPx()
                alpha = titleAlpha
                scaleX = titleScale
                scaleY = titleScale
            }
        ) {
            Text(
                text = "Circle Pop",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
