package com.project.autocompleteapp.presentation.viewmodel.home

sealed class HomeEffect {

    data class ErrorOccurred(val msg: String?): HomeEffect()
}