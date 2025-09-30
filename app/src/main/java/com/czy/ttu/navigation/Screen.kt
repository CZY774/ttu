package com.czy.ttu.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Camera : Screen("camera")
    object About : Screen("about")
    object Settings : Screen("settings")
}
