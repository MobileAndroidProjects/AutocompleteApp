package com.project.autocompleteapp.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.project.autocompleteapp.R
import com.project.autocompleteapp.ui.theme.Typography

@Composable
fun DetailsTextRow(
    modifier: Modifier = Modifier,
    style: TextStyle = Typography.bodyLarge,
    text: String
) {
    Text(
        modifier = modifier
            .padding(top = dimensionResource(R.dimen.row_padding_top)),
        style = style,
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}