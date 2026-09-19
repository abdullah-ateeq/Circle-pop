package com.example.circlepop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.circlepop.data.ThemeMode
import com.example.circlepop.game.CirclePopViewModel
import com.example.circlepop.navigation.CirclePopNavGraph
import com.example.circlepop.ui.theme.CirclePoPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CirclePopViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            CirclePoPTheme(darkTheme = darkTheme) {
                CirclePopNavGraph(viewModel = viewModel)
            }
        }
    }
}
