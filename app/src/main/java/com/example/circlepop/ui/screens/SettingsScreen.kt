package com.example.circlepop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.circlepop.data.ThemeMode
import com.example.circlepop.game.CirclePopViewModel
import com.example.circlepop.ui.components.ConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CirclePopViewModel,
    onBack: () -> Unit
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            SettingsSectionTitle("Theme")
            ThemeOptionRow(
                label = "Light",
                selected = themeMode == ThemeMode.LIGHT,
                onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) }
            )
            ThemeOptionRow(
                label = "Dark",
                selected = themeMode == ThemeMode.DARK,
                onClick = { viewModel.setThemeMode(ThemeMode.DARK) }
            )
            ThemeOptionRow(
                label = "System Default",
                selected = themeMode == ThemeMode.SYSTEM,
                onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) }
            )

            Spacer(Modifier.height(20.dp))

            SettingsSectionTitle("Gameplay")
            SwitchRow(
                label = "Sound Effects",
                checked = soundEnabled,
                onCheckedChange = { viewModel.setSoundEnabled(it) }
            )
            SwitchRow(
                label = "Haptic Feedback",
                checked = hapticsEnabled,
                onCheckedChange = { viewModel.setHapticsEnabled(it) }
            )

            Spacer(Modifier.height(28.dp))

            TextButton(onClick = { showResetDialog = true }) {
                Text(
                    text = "Reset Game Statistics",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    if (showResetDialog) {
        ConfirmDialog(
            title = "Reset all game statistics?",
            text = "This clears your Best Score, Best Streak, and Games Played. This can't be undone.",
            confirmLabel = "Reset",
            dismissLabel = "Cancel",
            onConfirm = {
                showResetDialog = false
                viewModel.resetStats()
            },
            onDismiss = { showResetDialog = false }
        )
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun ThemeOptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selected, onClick = onClick)
            Spacer(Modifier.height(0.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
