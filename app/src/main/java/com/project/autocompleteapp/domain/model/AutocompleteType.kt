package com.project.autocompleteapp.domain.model

import androidx.compose.ui.graphics.Color
import com.project.autocompleteapp.ui.theme.BlueLight
import com.project.autocompleteapp.ui.theme.OrangeDark

enum class AutocompleteType(val color: Color) {
    USER(color = BlueLight), REPOSITORY(color = OrangeDark)
}