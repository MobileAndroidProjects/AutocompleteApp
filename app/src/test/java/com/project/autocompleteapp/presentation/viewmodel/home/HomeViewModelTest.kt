package com.project.autocompleteapp.presentation.viewmodel.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.project.autocompleteapp.domain.model.AutocompleteListItem
import com.project.autocompleteapp.domain.model.AutocompleteType
import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.model.UserExtendedItem
import com.project.autocompleteapp.domain.use_case.GetRepositoryUseCase
import com.project.autocompleteapp.domain.use_case.GetUserUseCase
import com.project.autocompleteapp.domain.use_case.GetUsersAndRepositoriesUseCase
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeEffect
import com.project.autocompleteapp.presentation.viewmodel.home.structure.HomeEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Using UnconfinedTestDispatcher makes state updates immediate.
    private val testDispatcher = UnconfinedTestDispatcher()

    private val getUserUseCase: GetUserUseCase = mockk(relaxed = true)
    private val getRepositoryUseCase: GetRepositoryUseCase = mockk(relaxed = true)
    private val getUsersAndRepositoriesUseCase: GetUsersAndRepositoriesUseCase = mockk(relaxed = true)

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Provide default answers to avoid "no answer found" exceptions
        // especially during background search flow triggers or when Result type is involved.
        // We use any() to catch all background calls initiated in the ViewModel's init block.
        coEvery { getUsersAndRepositoriesUseCase(any()) } returns Result.success(emptyList())
        coEvery { getUserUseCase(any()) } returns Result.success(mockk(relaxed = true))
        coEvery { getRepositoryUseCase(any(), any()) } returns Result.success(mockk(relaxed = true))

        // Initialize ViewModel after setting Main dispatcher and configuring default mocks
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
    fun `initial state should be correct`() = runTest {
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.list?.isEmpty() ?: false)
        assertEquals(null, state.selectedUser)
        assertEquals(null, state.selectedRepository)
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `OnAutocompleteInputChanged updates searchQuery`() = runTest {
        val query = "test"

        // When
        viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(query))

        // Then
        // With UnconfinedTestDispatcher, the value update is immediate.
        assertEquals(query, viewModel.searchQuery.value)
    }

    @Test
    fun `search query shorter than threshold clears list`() = runTest {
        // Given
        val query = "ab"

        // When
        viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(query))
        
        // Advance time to pass the 500ms debounce
        advanceTimeBy(600)
        runCurrent()

        // Then
        assertTrue(viewModel.state.value.list?.isEmpty() ?: false)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `search query triggers usecase and updates state on success`() = runTest {
        // Given
        val query = "android"
        val mockItems = listOf(
            AutocompleteListItem(id = 1, value = "android", type = AutocompleteType.USER)
        )
        // Specific override for this test
        coEvery { getUsersAndRepositoriesUseCase(query) } returns Result.success(mockItems)

        // When
        viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(query))
        
        // Advance time to pass the 500ms debounce
        advanceTimeBy(600)
        runCurrent()

        // Then
        assertEquals(mockItems, viewModel.state.value.list)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `debounce should delay usecase call`() = runTest {
        // Given
        val query = "android"
        coEvery { getUsersAndRepositoriesUseCase(query) } returns Result.success(emptyList())

        // When
        viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(query))

        // Advance time just before debounce (500ms)
        advanceTimeBy(400)
        runCurrent()

        // Then: Usecase should not have been called yet
        coVerify(exactly = 0) { getUsersAndRepositoriesUseCase(query) }

        // Advance past debounce
        advanceTimeBy(101)
        runCurrent()

        // Then: Usecase should have been called
        coVerify(exactly = 1) { getUsersAndRepositoriesUseCase(query) }
    }

    @Test
    fun `debounce should only trigger search for the latest query`() = runTest {
        // Given
        val query1 = "andr"
        val query2 = "andro"
        val query3 = "android"
        coEvery { getUsersAndRepositoriesUseCase(any()) } returns Result.success(emptyList())

        // When
        viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(query1))
        advanceTimeBy(200)
        viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(query2))
        advanceTimeBy(200)
        viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(query3))
        
        // Advance remaining time for the last debounce
        advanceTimeBy(501)
        runCurrent()

        // Then: Only the last query should have triggered the usecase
        coVerify(exactly = 1) { getUsersAndRepositoriesUseCase(query3) }
        coVerify(exactly = 0) { getUsersAndRepositoriesUseCase(query1) }
        coVerify(exactly = 0) { getUsersAndRepositoriesUseCase(query2) }
    }

    @Test
    fun `search query updates effect on failure`() = runTest {
        // Given
        val query = "error"
        val errorMessage = "Something went wrong"
        coEvery { getUsersAndRepositoriesUseCase(query) } returns Result.failure(Exception(errorMessage))

        viewModel.effect.test {
            // When
            viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(query))
            advanceTimeBy(600)
            runCurrent()

            // Then
            val effect = awaitItem()
            assertTrue(effect is HomeEffect.ErrorOccurred)
            assertEquals(errorMessage, (effect as HomeEffect.ErrorOccurred).msg)
        }
    }

    @Test
    fun `OnAutocompleteItemSelected USER fetches user details`() = runTest {
        // Given
        val userId = 1
        val username = "user1"
        val query = "user"
        val mockItems = listOf(
            AutocompleteListItem(id = userId, value = username, type = AutocompleteType.USER)
        )
        val userDetails = mockk<UserExtendedItem>(relaxed = true)
        
        coEvery { getUsersAndRepositoriesUseCase(query) } returns Result.success(mockItems)
        coEvery { getUserUseCase(username) } returns Result.success(userDetails)

        // 1. Populate list first
        viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(query))
        advanceTimeBy(600)
        runCurrent()

        // 2. Select item
        viewModel.onEvent(HomeEvent.OnAutocompleteItemSelected(userId))
        runCurrent()

        // Then
        assertEquals(userDetails, viewModel.state.value.selectedUser)
        assertTrue(viewModel.state.value.list?.isEmpty() ?: false)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `OnAutocompleteItemSelected REPOSITORY fetches repository details`() = runTest {
        // Given
        val repoId = 101
        val repoName = "repo1"
        val owner = "owner1"
        val query = "repo"
        val mockItems = listOf(
            AutocompleteListItem(id = repoId, value = repoName, type = AutocompleteType.REPOSITORY, owner = owner)
        )
        val repoDetails = mockk<RepositoryExtendedItem>(relaxed = true)
        
        coEvery { getUsersAndRepositoriesUseCase(query) } returns Result.success(mockItems)
        coEvery { getRepositoryUseCase(owner, repoName) } returns Result.success(repoDetails)

        // 1. Populate list first
        viewModel.onEvent(HomeEvent.OnAutocompleteInputChanged(query))
        advanceTimeBy(600)
        runCurrent()

        // 2. Select item
        viewModel.onEvent(HomeEvent.OnAutocompleteItemSelected(repoId))
        runCurrent()

        // Then
        assertEquals(repoDetails, viewModel.state.value.selectedRepository)
        assertTrue(viewModel.state.value.list?.isEmpty() ?: false)
        assertFalse(viewModel.state.value.isLoading)
    }
}
