package com.example.circlepop.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.circlepop.game.CircleTarget
import com.example.circlepop.ui.theme.TargetPalette
import kotlinx.coroutines.launch

/**
 * The single active, tappable target. Plays a short entrance bounce and shows a thin
 * lifetime ring. Callers should wrap this in `key(target.id) { ... }` so a new target
 * always gets a fresh entrance animation.
 */
@Composable
fun LiveTargetCircle(
    target: CircleTarget,
    progress: Float,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { alpha.animateTo(1f, tween(150)) }
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    val color = TargetPalette[target.colorIndex % TargetPalette.size]
    val ringBoxSize = target.sizeDp.dp + 14.dp

    Box(
        modifier = modifier.size(ringBoxSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 3.dp.toPx()
            val ringRadius = (size.minDimension - strokeWidth) / 2f
            drawCircle(
                color = color.copy(alpha = 0.3f),
                radius = ringRadius,
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
                size = Size(ringRadius * 2f, ringRadius * 2f),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Box(
            modifier = Modifier
                .size(target.sizeDp.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    this.alpha = alpha.value
                }
                .shadow(elevation = 10.dp, shape = CircleShape, clip = false)
                .clip(CircleShape)
                .background(color)
                .drawWithContent {
                    drawContent()
                    // Soft highlight near the top-left, like light catching a glossy sphere.
                    drawCircle(
                        color = Color.White.copy(alpha = 0.35f),
                        radius = size.minDimension * 0.22f,
                        center = Offset(size.width * 0.32f, size.height * 0.3f)
                    )
                    // Thin outer ring for definition against similarly-toned backgrounds.
                    drawCircle(
                        color = Color.White.copy(alpha = 0.25f),
                        radius = size.minDimension / 2f - 1.dp.toPx(),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
                .semantics {
                    role = Role.Button
                    contentDescription = "Target circle"
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClickLabel = "Pop circle",
                    role = Role.Button,
                    onClick = onTap
                )
        )
    }
}

/** A non-interactive copy of a missed target that shrinks and fades away. */
@Composable
fun MissedTargetGhost(
    target: CircleTarget,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        launch { scale.animateTo(0.35f, tween(500)) }
        alpha.animateTo(0f, tween(500))
    }

    val color = TargetPalette[target.colorIndex % TargetPalette.size]

    Box(
        modifier = modifier
            .size(target.sizeDp.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value
            }
            .clip(CircleShape)
            .background(color)
    )
}

@Preview
@Composable
private fun LiveTargetCirclePreview() {
    LiveTargetCircle(
        target = CircleTarget(id = 1, xFraction = 0.5f, yFraction = 0.5f, sizeDp = 76f, colorIndex = 0),
        progress = 0.7f,
        onTap = {}
    )
}
