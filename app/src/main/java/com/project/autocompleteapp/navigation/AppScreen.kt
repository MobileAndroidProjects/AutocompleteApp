package com.project.autocompleteapp.navigation

sealed class AppScreen(val route: String) {
    object StartScreen : AppScreen("start_screen")
    object HomeScreen : AppScreen("home_screen")
}