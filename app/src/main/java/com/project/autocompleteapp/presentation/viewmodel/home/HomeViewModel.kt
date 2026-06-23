package com.project.autocompleteapp.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.autocompleteapp.domain.model.AutocompleteType
import com.project.autocompleteapp.domain.use_case.GetRepositoryUseCase
import com.project.autocompleteapp.domain.use_case.GetUserUseCase
import com.project.autocompleteapp.domain.use_case.GetUsersAndRepositoriesUseCase
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeEffect
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeEvent
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getRepositoryUseCase: GetRepositoryUseCase,
    private val getUsersAndRepositoriesUseCase: GetUsersAndRepositoriesUseCase
) : ViewModel() {

    companion object {
        const val INPUT_LENGTH_THRESHOLD = 3
        private const val DEBOUNCE_TIMEOUT_MILLIS = 500L
    }

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchTrigger = MutableSharedFlow<String>()
    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    init {
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        _searchTrigger
            .debounce(DEBOUNCE_TIMEOUT_MILLIS)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                flow {
                    if (query.length >= INPUT_LENGTH_THRESHOLD) {
                        _state.update { it.copy(isLoading = true) }
                        val result = getUsersAndRepositoriesUseCase(query)
                        emit(result)
                    } else {
                        emit(Result.success(emptyList()))
                    }
                }
            }
            .onEach { result ->
                result.onSuccess { items ->
                    _state.update { it.copy(list = items, isLoading = false) }
                }
                result.onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _effect.emit(HomeEffect.ErrorOccurred(error.message))
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnAutocompleteInputChanged -> {
                _searchQuery.value = event.input
                // Trigger the search flow only for manual input
                viewModelScope.launch {
                    _searchTrigger.emit(event.input)
                }
            }
            is HomeEvent.OnAutocompleteItemSelected -> {
                // Safely get item from the current list
                _state.value.list?.firstOrNull { it.id == event.id }?.let { selectedItem ->
                    // 1. Clear the suggestions and previous selections
                    _state.update { it.copy(
                        list = emptyList(),
                        selectedUser = null,
                        selectedRepository = null
                    ) }

                    // 2. Update the UI text field, but DON'T emit to _searchTrigger
                    _searchQuery.value = selectedItem.label

                    // 3. Execute detail fetch
                    when (selectedItem.type) {
                        AutocompleteType.USER -> getUser(selectedItem.login)
                        AutocompleteType.REPOSITORY -> getRepository(
                            owner = selectedItem.login,
                            repo = selectedItem.repo.orEmpty()
                        )
                    }
                }
            }
        }
    }

    private fun getUser(username: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getUserUseCase(username).fold(
                onSuccess = { user ->
                    _state.update { it.copy(isLoading = false, selectedUser = user) }
                },
                onFailure = { throwable ->
                    _state.update { it.copy(isLoading = false) }
                    _effect.emit(HomeEffect.ErrorOccurred(throwable.message))
                }
            )
        }
    }

    private fun getRepository(owner: String, repo: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getRepositoryUseCase(owner, repo).fold(
                onSuccess = { repository ->
                    _state.update { it.copy(isLoading = false, selectedRepository = repository) }
                },
                onFailure = { throwable ->
                    _state.update { it.copy(isLoading = false) }
                    _effect.emit(HomeEffect.ErrorOccurred(throwable.message))
                }
            )
        }
    }
}