package com.czy.ttu.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.czy.ttu.ui.screens.AboutScreen
import com.czy.ttu.ui.screens.CameraScreen
import com.czy.ttu.ui.screens.HomeScreen
import com.czy.ttu.ui.screens.SettingsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCamera = {
                    navController.navigate(Screen.Camera.route)
                },
                onNavigateToAbout = {
                    navController.navigate(Screen.About.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
