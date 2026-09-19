package com.example.circlepop.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.circlepop.game.CirclePopViewModel
import com.example.circlepop.ui.components.ConfirmDialog
import com.example.circlepop.ui.components.LiveTargetCircle
import com.example.circlepop.ui.components.MissedTargetGhost
import com.example.circlepop.ui.components.PopEffect
import com.example.circlepop.ui.components.PopEffectView

@Composable
fun GameScreen(
    viewModel: CirclePopViewModel,
    onRunFinished: () -> Unit,
    onExitToHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showLeaveDialog by remember { mutableStateOf(false) }
    val popEffects = remember { mutableStateListOf<PopEffect>() }

    LaunchedEffect(Unit) {
        if (!viewModel.state.value.isGameActive) {
            viewModel.startGame()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.popEvents.collect { target ->
            popEffects.add(
                PopEffect(
                    id = target.id,
                    xFraction = target.xFraction,
                    yFraction = target.yFraction,
                    sizeDp = target.sizeDp,
                    colorIndex = target.colorIndex
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToResult.collect { onRunFinished() }
    }

    BackHandler(enabled = state.isGameActive) {
        showLeaveDialog = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GameStat(label = "Score", value = state.score.toString())
            GameStat(label = "Streak", value = "🔥 ${state.streak}", alignEnd = true)
        }

        Spacer(Modifier.height(20.dp))

        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            viewModel.updatePlayAreaSize(maxWidth.value, maxHeight.value)

            state.target?.let { target ->
                key(target.id) {
                    LiveTargetCircle(
                        target = target,
                        progress = state.lifetimeProgress,
                        onTap = { viewModel.onTargetTapped(target.id) },
                        modifier = centerOffset(maxWidth, maxHeight, target.xFraction, target.yFraction, target.sizeDp + 14f)
                    )
                }
            }

            state.missedTarget?.let { ghost ->
                MissedTargetGhost(
                    target = ghost,
                    modifier = centerOffset(maxWidth, maxHeight, ghost.xFraction, ghost.yFraction, ghost.sizeDp)
                )
            }

            popEffects.forEach { effect ->
                key(effect.id) {
                    PopEffectView(
                        effect = effect,
                        onFinished = { popEffects.remove(effect) },
                        modifier = centerOffset(maxWidth, maxHeight, effect.xFraction, effect.yFraction, effect.sizeDp + 44f)
                    )
                }
            }

            MissedLabel(
                visible = state.missedTarget != null,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    if (showLeaveDialog) {
        ConfirmDialog(
            title = "Leave this game?",
            text = "Your current run will end and won't count toward your best score.",
            confirmLabel = "Exit Game",
            dismissLabel = "Continue Playing",
            onConfirm = {
                showLeaveDialog = false
                viewModel.leaveGameEarly()
                onExitToHome()
            },
            onDismiss = { showLeaveDialog = false }
        )
    }
}

@Composable
private fun MissedLabel(visible: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Text(
            text = "MISSED!",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun GameStat(label: String, value: String, alignEnd: Boolean = false) {
    Column(horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

private fun centerOffset(
    maxWidth: Dp,
    maxHeight: Dp,
    xFraction: Float,
    yFraction: Float,
    boxSizeDp: Float
): Modifier {
    val x = maxWidth * xFraction - (boxSizeDp / 2f).dp
    val y = maxHeight * yFraction - (boxSizeDp / 2f).dp
    return Modifier.offset(x = x, y = y)
}
