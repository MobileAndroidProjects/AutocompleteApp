package com.project.autocompleteapp.presentation.viewmodel.home.structure

sealed class HomeEffect {

    data class ErrorOccurred(val msg: String?): HomeEffect()
}