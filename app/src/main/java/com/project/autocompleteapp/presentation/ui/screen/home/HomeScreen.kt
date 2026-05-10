package com.project.autocompleteapp.presentation.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.project.autocompleteapp.R
import com.project.autocompleteapp.presentation.ui.components.AutocompleteField
import com.project.autocompleteapp.presentation.ui.components.DetailsField
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeEffect
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeEvent
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    effect: SharedFlow<HomeEffect>,
    onError: (String?) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        effect.collectLatest { event ->
            when (event) {
                is HomeEffect.ErrorOccurred -> {
                    keyboardController?.hide()
                    onError(event.msg)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = dimensionResource(R.dimen.padding_l),
                    start = dimensionResource(R.dimen.padding_s),
                    end = dimensionResource(R.dimen.padding_s)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AutocompleteField(
                title = stringResource(R.string.autocomplete_label),
                resultList = state.list.orEmpty(),
                input = state.input,
                isLoading = state.isLoading,
                onInputChanged = { input, triggered ->
                    onEvent(
                        HomeEvent.OnAutocompleteInputChanged(
                            input = input, actionTriggered = triggered
                        )
                    )
                },
                onItemSelected = {
                    onEvent(HomeEvent.OnAutocompleteItemSelected(index = it))
                }
            )

            if (state.isSelected) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_m)))

                DetailsField(
                    selectedUser = state.selectedUser,
                    selectedRepository = state.selectedRepository
                )
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.width(dimensionResource(R.dimen.circular_progress_size))
            )
        }
    }
}
