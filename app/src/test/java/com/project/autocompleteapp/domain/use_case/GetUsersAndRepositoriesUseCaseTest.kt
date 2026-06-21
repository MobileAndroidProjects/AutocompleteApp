package com.project.autocompleteapp.domain.use_case

import com.project.autocompleteapp.domain.model.AutocompleteListItem
import com.project.autocompleteapp.domain.model.AutocompleteType
import com.project.autocompleteapp.domain.repository.GithubRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetUsersAndRepositoriesUseCaseTest {

    private lateinit var githubRepository: GithubRepository
    private lateinit var getUsersAndRepositoriesUseCase: GetUsersAndRepositoriesUseCase

    @Before
    fun setUp() {
        githubRepository = mockk()
        getUsersAndRepositoriesUseCase = GetUsersAndRepositoriesUseCase(githubRepository)
    }

    @Test
    fun `invoke returns success with empty list when query is blank`() = runTest {
        // When
        val result = getUsersAndRepositoriesUseCase("   ")

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun `invoke returns success with combined list when both repository calls succeed`() = runTest {
        // Given
        val query = "test"
        val userItems = listOf(
            AutocompleteListItem(id = 1, value = "user1", type = AutocompleteType.USER)
        )
        val repoItems = listOf(
            AutocompleteListItem(id = 101, value = "repo1", owner = "owner1", type = AutocompleteType.REPOSITORY)
        )
        coEvery { githubRepository.getUsers(query) } returns Result.success(userItems)
        coEvery { githubRepository.getRepositories(query) } returns Result.success(repoItems)

        // When
        val result = getUsersAndRepositoriesUseCase(query)

        // Then
        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertEquals(2, data?.size)
        assertEquals(userItems[0], data?.get(0))
        assertEquals(repoItems[0], data?.get(1))
    }

    @Test
    fun `invoke returns success with partial list when only users call fails`() = runTest {
        // Given
        val query = "test"
        val repoItems = listOf(
            AutocompleteListItem(id = 101, value = "repo1", owner = "owner1", type = AutocompleteType.REPOSITORY)
        )
        coEvery { githubRepository.getUsers(query) } returns Result.failure(Exception("API Error"))
        coEvery { githubRepository.getRepositories(query) } returns Result.success(repoItems)

        // When
        val result = getUsersAndRepositoriesUseCase(query)

        // Then
        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertEquals(1, data?.size)
        assertEquals(repoItems[0], data?.get(0))
    }

    @Test
    fun `invoke returns success with empty list when both repository calls fail`() = runTest {
        // Given
        val query = "test"
        coEvery { githubRepository.getUsers(query) } returns Result.failure(Exception("Error 1"))
        coEvery { githubRepository.getRepositories(query) } returns Result.failure(Exception("Error 2"))

        // When
        val result = getUsersAndRepositoriesUseCase(query)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun `invoke returns failure when repository throws unexpected exception`() = runTest {
        // Given
        val query = "test"
        val exception = RuntimeException("Unexpected crash")
        coEvery { githubRepository.getUsers(query) } throws exception

        // When
        val result = getUsersAndRepositoriesUseCase(query)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
