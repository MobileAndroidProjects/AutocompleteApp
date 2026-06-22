package com.project.autocompleteapp.domain.model

data class AutocompleteListItem(
    val id: Int,
    val value: String, // login or repo name
    val owner: String? = null,
    val type: AutocompleteType
) {
    val label: String
        get() = when (type) {
            AutocompleteType.USER -> value
            AutocompleteType.REPOSITORY -> "$owner/$value"
        }
}
