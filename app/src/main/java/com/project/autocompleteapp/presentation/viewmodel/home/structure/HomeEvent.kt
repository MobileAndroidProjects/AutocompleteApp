package com.project.autocompleteapp.presentation.viewmodel.home.structure

sealed class HomeEvent {

    data class OnAutocompleteInputChanged(
        val input: String,
        val actionTriggered: Boolean
    ): HomeEvent()

    data class OnAutocompleteItemSelected(val index: Int): HomeEvent()
}