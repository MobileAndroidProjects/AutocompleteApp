package com.project.autocompleteapp.presentation.viewmodel.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.project.autocompleteapp.domain.model.AutocompleteListItem
import com.project.autocompleteapp.domain.model.AutocompleteType
import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.model.UserExtendedItem
import com.project.autocompleteapp.domain.usecase.GetRepositoryUseCase
import com.project.autocompleteapp.domain.usecase.GetUserUseCase
import com.project.autocompleteapp.domain.usecase.GetUsersAndRepositoriesUseCase
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeEffect
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeEvent
import com.project.autocompleteapp.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var getUserUseCase: GetUserUseCase
    private lateinit var getRepositoryUseCase: GetRepositoryUseCase
    private lateinit var getUsersAndRepositoriesUseCase: GetUsersAndRepositoriesUseCase
    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getUserUseCase = mockk()
        getRepositoryUseCase = mockk()
        getUsersAndRepositoriesUseCase = mockk()
        viewModel = HomeViewModel(
            getUserUseCase,
            getRepositoryUseCase,
            getUsersAndRepositoriesUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onEvent OnAutocompleteInputChanged should update state and fetch data when actionTriggered is true`() = runTest {
        // Given
        val input = "test"
        val list = listOf(AutocompleteListItem(1, "test", type = AutocompleteType.USER))
        coEvery { getUsersAndRepositoriesUseCase(input) } returns flowOf(
            Resource.Loading(),
            Resource.Success(list)
        )

        // When
        viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(input = input, actionTriggered = true))
        advanceUntilIdle()

        // Then
        assertEquals(input, viewModel.state.value.input)
        assertEquals(list, viewModel.state.value.list)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `onEvent OnAutocompleteInputChanged should update state and not fetch data when actionTriggered is false`() = runTest {
        // Given
        val input = "te"

        // When
        viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(input = input, actionTriggered = false))
        advanceUntilIdle()

        // Then
        assertEquals(input, viewModel.state.value.input)
        assertTrue(viewModel.state.value.list!!.isEmpty())
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `onEvent OnAutocompleteItemSelected for User should fetch user details`() = runTest {
        // Given
        val input = "test"
        val userItem = AutocompleteListItem(1, "user1", type = AutocompleteType.USER)
        val list = listOf(userItem)
        coEvery { getUsersAndRepositoriesUseCase(input) } returns flowOf(Resource.Success(list))
        
        val userExtendedItem = mockk<UserExtendedItem>()
        coEvery { getUserUseCase(1) } returns flowOf(
            Resource.Loading(),
            Resource.Success(Response.success(userExtendedItem))
        )

        // Set initial state with list
        viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(input = input, actionTriggered = true))
        advanceUntilIdle()

        // When
        viewModel.onEvent(HomeEvent.OnAutocompleteItemSelected(0))
        advanceUntilIdle()

        // Then
        assertEquals("user1", viewModel.state.value.input)
        assertEquals(userExtendedItem, viewModel.state.value.selectedUser)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `onEvent OnAutocompleteItemSelected for Repository should fetch repository details`() = runTest {
        // Given
        val input = "test"
        val repoItem = AutocompleteListItem(1, "repo1", owner = "owner1", type = AutocompleteType.REPOSITORY)
        val list = listOf(repoItem)
        coEvery { getUsersAndRepositoriesUseCase(input) } returns flowOf(Resource.Success(list))
        
        val repoExtendedItem = mockk<RepositoryExtendedItem>()
        coEvery { getRepositoryUseCase("owner1", "repo1") } returns flowOf(
            Resource.Loading(),
            Resource.Success(Response.success(repoExtendedItem))
        )

        // Set initial state with list
        viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(input = input, actionTriggered = true))
        advanceUntilIdle()

        // When
        viewModel.onEvent(HomeEvent.OnAutocompleteItemSelected(0))
        advanceUntilIdle()

        // Then
        assertEquals("repo1", viewModel.state.value.input)
        assertEquals(repoExtendedItem, viewModel.state.value.selectedRepository)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `fetchData error should emit ErrorOccurred effect`() = runTest {
        // Given
        val input = "test"
        val errorMessage = "Something went wrong"
        coEvery { getUsersAndRepositoriesUseCase(input) } returns flowOf(
            Resource.Loading(),
            Resource.Error(errorMessage)
        )

        // When & Then
        viewModel.effect.test {
            viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(input = input, actionTriggered = true))
            val effect = awaitItem()
            assertTrue(effect is HomeEffect.ErrorOccurred)
            assertEquals(errorMessage, (effect as HomeEffect.ErrorOccurred).msg)
        }
    }
}
