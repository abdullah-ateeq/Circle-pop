package com.example.circlepop.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.circlepop.game.CirclePopViewModel
import com.example.circlepop.ui.components.StatColumn
import com.example.circlepop.ui.theme.TargetPalette

@Composable
fun ResultScreen(
    viewModel: CirclePopViewModel,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val stats by viewModel.stats.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (state.isNewBest) {
                    NewBestCelebration()
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "GAME OVER",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (state.isNewBest) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "NEW BEST!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Final Score",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = state.score.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn(label = "Best Score", value = stats.bestScore.toString())
                StatColumn(label = "Best Streak", value = stats.bestStreak.toString())
            }

            Spacer(Modifier.height(48.dp))

            ResultButton(
                text = "PLAY AGAIN",
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                onClick = onPlayAgain
            )

            Spacer(Modifier.height(14.dp))

            ResultButton(
                text = "HOME",
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
                onClick = onHome
            )
        }
    }
}

@Composable
private fun ResultButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(containerColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}

/** A lightweight, circle-based celebration: a few rings expand and fade on a loop. */
@Composable
private fun NewBestCelebration() {
    val infinite = rememberInfiniteTransition(label = "newBest")
    val progress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "celebrationProgress"
    )

    Canvas(modifier = Modifier.size(220.dp)) {
        val maxRadius = size.minDimension / 2f
        val ringCount = 3
        repeat(ringCount) { i ->
            val ringProgress = ((progress + i / ringCount.toFloat()) % 1f)
            val eased = FastOutSlowInEasing.transform(ringProgress)
            drawCircle(
                color = TargetPalette[(i * 2) % TargetPalette.size].copy(alpha = (1f - eased) * 0.5f),
                radius = maxRadius * (0.3f + eased * 0.7f),
                style = Stroke(width = 2.5.dp.toPx())
            )
        }
    }
}
