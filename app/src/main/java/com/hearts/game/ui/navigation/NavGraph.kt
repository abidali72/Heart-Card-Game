package com.hearts.game.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hearts.game.data.model.*
import com.hearts.game.ui.screens.*
import com.hearts.game.ui.viewmodel.GameViewModel
import com.hearts.game.ui.viewmodel.SettingsViewModel

object Routes {
    const val MAIN_MENU = "main_menu"
    const val GAME = "game"
    const val SETTINGS = "settings"
    const val HOW_TO_PLAY = "how_to_play"
    const val STATS = "stats"
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    settingsViewModel: SettingsViewModel
) {
    val gameViewModel: GameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.MAIN_MENU
    ) {
        composable(Routes.MAIN_MENU) {
            MainMenuScreen(
                onNewGame = { difficulty ->
                    val settings = settingsViewModel.settings.value
                    gameViewModel.startNewGame(
                        GameConfig(
                            scoreLimit = settings.scoreLimit,
                            aiDifficulty = difficulty,
                            gameSpeed = settings.gameSpeed,
                            soundEnabled = settings.soundEnabled,
                            musicEnabled = settings.musicEnabled,
                            vibrationEnabled = settings.vibrationEnabled,
                            cardTheme = settings.cardTheme,
                            tableTheme = settings.tableTheme
                        )
                    )
                    navController.navigate(Routes.GAME)
                },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onHowToPlay = { navController.navigate(Routes.HOW_TO_PLAY) },
                onStats = { navController.navigate(Routes.STATS) }
            )
        }

        composable(Routes.GAME) {
            val settings by settingsViewModel.settings.collectAsState()
            val gameState by gameViewModel.gameState.collectAsState()
            
            // Sync settings with game view model
            LaunchedEffect(settings) {
                gameViewModel.updateConfig(settings)
            }
            
            // Record stats on game over
            LaunchedEffect(gameState.phase) {
                val phase = gameState.phase
                if (phase is GamePhase.GameOver) {
                    val winner = phase.winner
                    val isWin = winner == PlayerPosition.SOUTH
                    settingsViewModel.recordGameResult(isWin)
                }
            }

            GameScreen(
                viewModel = gameViewModel,
                onBackToMenu = {
                    navController.popBackStack(Routes.MAIN_MENU, inclusive = false)
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HOW_TO_PLAY) {
            HowToPlayScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.STATS) {
            StatsScreen(
                onBack = { navController.popBackStack() },
                settingsViewModel = settingsViewModel
            )
        }
    }
}
