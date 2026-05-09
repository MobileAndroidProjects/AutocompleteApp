package com.project.autocompleteapp.presentation.viewmodel.home

sealed class HomeEvent {

    data class OnAutocompleteInputChanged(val input: String): HomeEvent()
}