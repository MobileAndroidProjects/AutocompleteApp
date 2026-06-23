package com.project.autocompleteapp.domain.model

data class AutocompleteListItem(
    val id: Int,
    val login: String,
    val repo: String? = null,
    val type: AutocompleteType
) {
    val label: String
        get() = when (type) {
            AutocompleteType.USER -> login
            AutocompleteType.REPOSITORY -> "$login/$repo"
        }
}
