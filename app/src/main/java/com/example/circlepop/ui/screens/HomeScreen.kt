package com.example.circlepop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.circlepop.game.CirclePopViewModel
import com.example.circlepop.ui.components.HomeHeroIllustration
import com.example.circlepop.ui.components.PlayButton
import com.example.circlepop.ui.components.StatColumn

@Composable
fun HomeScreen(
    viewModel: CirclePopViewModel,
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onAboutClick) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "About Circle Pop"
                    )
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings"
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Circle Pop",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Pop the circles before they disappear.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                HomeHeroIllustration()
                Spacer(Modifier.height(28.dp))
                PlayButton(onClick = onPlayClick)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn(label = "Best Score", value = stats.bestScore.toString())
                StatColumn(label = "Best Streak", value = stats.bestStreak.toString())
                StatColumn(label = "Games Played", value = stats.gamesPlayed.toString())
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}
