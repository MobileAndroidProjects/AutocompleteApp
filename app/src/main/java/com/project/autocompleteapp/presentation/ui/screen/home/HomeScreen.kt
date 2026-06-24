package com.project.autocompleteapp.presentation.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.project.autocompleteapp.R
import com.project.autocompleteapp.domain.model.AutocompleteListItem
import com.project.autocompleteapp.domain.model.AutocompleteType
import com.project.autocompleteapp.presentation.ui.components.AutocompleteField
import com.project.autocompleteapp.presentation.ui.components.DetailsField
import com.project.autocompleteapp.presentation.viewmodel.home.HomeViewModel
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeEffect
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeEvent
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeState
import com.project.autocompleteapp.ui.theme.GreenLight
import com.project.autocompleteapp.ui.theme.Typography
import com.project.autocompleteapp.ui.theme.White
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    state: HomeState,
    searchQuery: String,
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = GreenLight,
                        shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner))
                    )
                    .padding(all = dimensionResource(R.dimen.padding_m))
            ) {
                TitleAndDescriptionField()

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_m)))

                AutocompleteField(
                    resultList = state.list?.sortedWith(
                        compareBy(
                            String.CASE_INSENSITIVE_ORDER, AutocompleteListItem::label
                        )
                    ).orEmpty(),
                    input = searchQuery,
                    isLoading = state.isLoading,
                    inputLengthThreshold = HomeViewModel.INPUT_LENGTH_THRESHOLD,
                    onInputChanged = { input ->
                        onEvent(HomeEvent.OnAutocompleteInputChanged(input = input))
                    },
                    onItemSelected = {
                        keyboardController?.hide()
                        onEvent(HomeEvent.OnAutocompleteItemSelected(id = it))
                    }
                )
            }

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

@Composable
fun TitleAndDescriptionField() {

    Text(
        style = Typography.bodyLarge,
        text = stringResource(R.string.autocomplete_label)
    )

    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_m)))

    Row(
        modifier = Modifier.padding(
            vertical = dimensionResource(R.dimen.padding_s)
        )
    ) {
        Text(
            modifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_xs),
                end = dimensionResource(R.dimen.padding_s)
            ),
            style = Typography.labelMedium,
            text = stringResource(R.string.autocomplete_result_label)
        )

        LazyRow(
            modifier = Modifier
                .background(
                    color = White,
                    shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner))
                )
        ) {
            items(AutocompleteType.entries.size) {
                Text(
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_xs)),
                    style = Typography.labelMedium,
                    text = AutocompleteType.entries[it].name.lowercase(),
                    color = AutocompleteType.entries[it].color
                )
            }
        }
    }
}
