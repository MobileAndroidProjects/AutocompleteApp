package com.project.autocompleteapp.presentation.viewmodel.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.project.autocompleteapp.domain.usecase.GetRepositoryUseCase
import com.project.autocompleteapp.domain.usecase.GetUserUseCase
import com.project.autocompleteapp.domain.usecase.GetUsersAndRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getRepositoryUseCase: GetRepositoryUseCase,
    private val getUsersAndRepositoriesUseCase: GetUsersAndRepositoriesUseCase
): ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnAutocompleteInputChanged -> Unit
        }
    }
}