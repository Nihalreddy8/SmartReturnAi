package com.smartreturn.ai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartreturn.ai.ui.additem.AddItemScreen
import com.smartreturn.ai.ui.alert.AlertScreen
import com.smartreturn.ai.ui.assistant.AskSmartReturnScreen
import com.smartreturn.ai.ui.home.HomeScreen
import com.smartreturn.ai.ui.memory.MemoryScreen
import com.smartreturn.ai.ui.replay.MemoryReplayScreen
import com.smartreturn.ai.ui.scan.ScanScreen
import com.smartreturn.ai.ui.settings.SettingsScreen
import com.smartreturn.ai.ui.splash.SplashScreen

@Composable
fun SmartReturnNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH
    ) {
        // Splash
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Home dashboard
        composable(NavRoutes.HOME) {
            HomeScreen(
                onNavigateToAddItem = { navController.navigate(NavRoutes.ADD_ITEM) },
                onNavigateToScan = { navController.navigate(NavRoutes.SCAN) },
                onNavigateToMemory = { navController.navigate(NavRoutes.MEMORY) },
                onNavigateToAsk = { navController.navigate(NavRoutes.ASK_SMARTRETURN) },
                onNavigateToSettings = { navController.navigate(NavRoutes.SETTINGS) },
                onNavigateToAlert = { navController.navigate(NavRoutes.ALERT) },
                onNavigateToReplay = { itemId ->
                    navController.navigate(NavRoutes.memoryReplay(itemId))
                }
            )
        }

        // Add item
        composable(NavRoutes.ADD_ITEM) {
            AddItemScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Camera scan
        composable(NavRoutes.SCAN) {
            ScanScreen(
                onNavigateBack = { navController.popBackStack() },
                onScanComplete = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.HOME) { inclusive = false }
                    }
                }
            )
        }

        // Memory overview
        composable(NavRoutes.MEMORY) {
            MemoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToReplay = { itemId ->
                    navController.navigate(NavRoutes.memoryReplay(itemId))
                }
            )
        }

        // Memory replay (with itemId arg)
        composable(
            route = NavRoutes.MEMORY_REPLAY,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStack ->
            val itemId = backStack.arguments?.getLong("itemId") ?: -1L
            MemoryReplayScreen(
                itemId = itemId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // AI assistant
        composable(NavRoutes.ASK_SMARTRETURN) {
            AskSmartReturnScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToReplay = { itemId ->
                    navController.navigate(NavRoutes.memoryReplay(itemId))
                }
            )
        }

        // Forgotten item alert
        composable(NavRoutes.ALERT) {
            AlertScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToReplay = { itemId ->
                    navController.navigate(NavRoutes.memoryReplay(itemId))
                }
            )
        }

        // Settings
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
