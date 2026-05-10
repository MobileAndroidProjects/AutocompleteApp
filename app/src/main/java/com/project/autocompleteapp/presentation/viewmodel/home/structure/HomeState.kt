package com.project.autocompleteapp.presentation.viewmodel.home.structure

import com.project.autocompleteapp.domain.model.AutocompleteListItem
import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.model.UserExtendedItem

data class HomeState(
    val isLoading: Boolean = false,
    val input: String = String(),
    val list: List<AutocompleteListItem>? = emptyList(),
    val selectedUser: UserExtendedItem? = null,
    val selectedRepository: RepositoryExtendedItem? = null
) {
    val isSelected: Boolean
        get() = selectedUser != null || selectedRepository != null
}
