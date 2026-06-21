package com.project.autocompleteapp.presentation.viewmodel.home.structure

sealed class HomeEvent {

    data class OnAutocompleteInputChanged(val input: String): HomeEvent()

    data class OnAutocompleteItemSelected(val id: Int): HomeEvent()
}