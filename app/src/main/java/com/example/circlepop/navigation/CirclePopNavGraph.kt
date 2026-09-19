package com.example.circlepop.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.circlepop.game.CirclePopViewModel
import com.example.circlepop.ui.screens.AboutScreen
import com.example.circlepop.ui.screens.GameScreen
import com.example.circlepop.ui.screens.HomeScreen
import com.example.circlepop.ui.screens.ResultScreen
import com.example.circlepop.ui.screens.SettingsScreen
import com.example.circlepop.ui.screens.SplashScreen

@Composable
fun CirclePopNavGraph(
    viewModel: CirclePopViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onPlayClick = { navController.navigate(Routes.GAME) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onAboutClick = { navController.navigate(Routes.ABOUT) }
            )
        }

        composable(Routes.GAME) {
            GameScreen(
                viewModel = viewModel,
                onRunFinished = {
                    navController.navigate(Routes.RESULT) {
                        popUpTo(Routes.GAME) { inclusive = true }
                    }
                },
                onExitToHome = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }

        composable(Routes.RESULT) {
            ResultScreen(
                viewModel = viewModel,
                onPlayAgain = {
                    navController.navigate(Routes.GAME) {
                        popUpTo(Routes.RESULT) { inclusive = true }
                    }
                },
                onHome = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
