package com.project.autocompleteapp.domain.model

data class AutocompleteListItem(
    val id: Int,
    val value: String, // login or repo name
    val owner: String? = null,
    val type: AutocompleteType
)
