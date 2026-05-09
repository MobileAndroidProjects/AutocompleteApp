package com.project.autocompleteapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.autocompleteapp.presentation.ui.screen.home.HomeScreen
import com.project.autocompleteapp.presentation.ui.screen.start.StartScreen
import com.project.autocompleteapp.presentation.viewmodel.home.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    snackbarHostState: SnackbarHostState,
    innerPadding: PaddingValues
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    NavHost(
        modifier = Modifier.padding(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding()
        ),
        navController = navController,
        startDestination = AppScreen.StartScreen.route
    ) {
        composable(AppScreen.StartScreen.route) {
            StartScreen(navController)
        }
        composable(AppScreen.HomeScreen.route) {
            val vm = hiltViewModel<HomeViewModel>()
            HomeScreen(
                state = vm.state.value,
                onEvent = vm::onEvent,
                effect = vm.effect,
                onError = {
                    scope.launch {
                        snackbarHostState.showSnackbar(it.orEmpty())
                    }
                }
            )
        }
    }
}