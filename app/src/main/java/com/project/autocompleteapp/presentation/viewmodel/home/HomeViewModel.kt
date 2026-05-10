package com.project.autocompleteapp.presentation.viewmodel.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.autocompleteapp.domain.model.AutocompleteType
import com.project.autocompleteapp.domain.usecase.GetRepositoryUseCase
import com.project.autocompleteapp.domain.usecase.GetUserUseCase
import com.project.autocompleteapp.domain.usecase.GetUsersAndRepositoriesUseCase
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeEffect
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeEvent
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeState
import com.project.autocompleteapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private var currentSearchJob: Job? = null

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnAutocompleteInputChanged -> {
                _state.value = _state.value.copy(
                    input = event.input,
                    list = emptyList(),
                    selectedUser = null,
                    selectedRepository = null
                )

                if (event.actionTriggered) {
                    fetchData(event.input)
                } else {
                    dismissCurrentSearch()
                    _state.value = _state.value.copy(isLoading = false)
                }
            }
            is HomeEvent.OnAutocompleteItemSelected -> {
                val selectedItem = _state.value.list?.get(event.index)
                selectedItem?.let {
                    _state.value = _state.value.copy(
                        input = it.value,
                        list = emptyList()
                    )

                    when (it.type) {
                        AutocompleteType.USER -> getUser(
                            userId = it.id
                        )
                        AutocompleteType.REPOSITORY -> getRepository(
                            owner = it.owner.orEmpty(),
                            repo = it.value
                        )
                    }
                }
            }
        }
    }

    private fun getUser(userId: Int) {
        getUserUseCase(userId = userId).onEach { result ->
            handleRequestResult(result) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    selectedUser = result.data?.body()
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun getRepository(owner: String, repo: String) {
        getRepositoryUseCase(owner = owner, repo = repo).onEach { result ->
            handleRequestResult(result) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    selectedRepository = result.data?.body()
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchData(input: String) {
        dismissCurrentSearch()
        currentSearchJob = getUsersAndRepositoriesUseCase(input = input).onEach { result ->
            handleRequestResult(result) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    list = result.data?.distinct()?.sortedBy { it.value }
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun dismissCurrentSearch() {
        currentSearchJob?.let {
            if (!it.isCompleted) {
                it.cancel()
            }
        }
        currentSearchJob = null
    }

    private suspend fun <T> handleRequestResult(
        result: Resource<T>,
        onSuccess: (Resource<T>) -> Unit
    ) {
        when (result) {
            is Resource.Loading -> {
                _state.value = _state.value.copy(
                    isLoading = true
                )
            }
            is Resource.Success -> onSuccess(result)
            is Resource.Error -> {
                _state.value = _state.value.copy(
                    isLoading = false
                )
                _effect.emit(HomeEffect.ErrorOccurred(result.message))
            }
        }
    }
}