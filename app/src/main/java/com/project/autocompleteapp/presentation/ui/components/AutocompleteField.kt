package com.project.autocompleteapp.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.project.autocompleteapp.R
import com.project.autocompleteapp.ui.theme.Typography
import com.project.autocompleteapp.domain.model.AutocompleteListItem
import com.project.autocompleteapp.domain.model.AutocompleteType
import com.project.autocompleteapp.ui.theme.GreenLight
import com.project.autocompleteapp.ui.theme.GreyDark
import com.project.autocompleteapp.ui.theme.GreyLight
import com.project.autocompleteapp.ui.theme.White

@Composable
fun AutocompleteField(
    title: String,
    resultList: List<AutocompleteListItem>,
    input: String,
    isLoading: Boolean,
    inputLengthThreshold: Int,
    onInputChanged: (String) -> Unit,
    onItemSelected: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var isSelected by remember { mutableStateOf(false) }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = GreenLight,
                shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner))
            )
            .padding(all = dimensionResource(R.dimen.padding_m))
    ) {
        Text(
            style = Typography.bodyLarge,
            text = title
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

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_m)))

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isTextFieldFocused = it.isFocused
                },
            value = input,
            singleLine = true,
            placeholder = {
                if (!isTextFieldFocused) {
                    Text(
                        style = Typography.bodyLarge.copy(fontSize = 17.sp),
                        text = stringResource(R.string.autocomplete_hint),
                        color = GreyLight,
                        maxLines = 1
                    )
                }
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(R.string.autocomplete_clear_label),
                    modifier = Modifier
                        .clickable {
                            onInputChanged.invoke(String())
                            isSelected = false
                        }
                )
            },
            onValueChange = {
                onInputChanged.invoke(it)
                isSelected = false
            }
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_xs)))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = White)
        ) {
            items(
                items = resultList,
                key = { it.id }
            ) { item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = White)
                        .clickable(true) {
                            onItemSelected(item.id)
                            focusManager.clearFocus()
                            isSelected = true
                        },
                ) {
                    Text(
                        modifier = Modifier.padding(all = dimensionResource(R.dimen.padding_xxs)),
                        style = Typography.labelMedium,
                        text = item.label,
                        color = item.type.color
                    )

                    HorizontalDivider()
                }
            }
        }

        if (isTextFieldFocused && input.isNotEmpty() && !isSelected && !isLoading) {
            if (input.length < inputLengthThreshold) {
                // Show "Type at least 3 characters"
                Text(
                    modifier = Modifier.padding(all = dimensionResource(R.dimen.padding_xxs)),
                    style = Typography.labelMedium,
                    text = stringResource(R.string.autocomplete_type_char),
                    color = GreyDark
                )
            } else if (resultList.isEmpty()) {
                // Show "No results found" only if we reached the threshold and list is still empty
                Text(
                    modifier = Modifier.padding(all = dimensionResource(R.dimen.padding_xxs)),
                    style = Typography.labelMedium,
                    text = stringResource(R.string.autocomplete_empty_result),
                    color = GreyDark
                )
            }
        }
    }
}
