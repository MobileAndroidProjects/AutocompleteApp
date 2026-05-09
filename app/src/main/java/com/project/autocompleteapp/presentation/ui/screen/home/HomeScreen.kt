package com.project.autocompleteapp.presentation.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.project.autocompleteapp.presentation.viewmodel.home.HomeEffect
import com.project.autocompleteapp.presentation.viewmodel.home.HomeEvent
import com.project.autocompleteapp.presentation.viewmodel.home.HomeState
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun HomeScreen(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    effect: SharedFlow<HomeEffect>,
    onError: (String?) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {

    }
}