package com.project.autocompleteapp.domain.model

import androidx.compose.ui.graphics.Color
import com.project.autocompleteapp.ui.theme.BlueLight
import com.project.autocompleteapp.ui.theme.OrangeDark

data class AutocompleteListItem(
    val id: Int,
    val value: String, // login or repo name
    val owner: String? = null,
    val type: AutocompleteType
)

enum class AutocompleteType(val color: Color) {
    USER(color = BlueLight), REPOSITORY(color = OrangeDark)
}
