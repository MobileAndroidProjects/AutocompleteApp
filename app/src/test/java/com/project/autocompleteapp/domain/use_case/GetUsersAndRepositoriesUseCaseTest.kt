package com.project.autocompleteapp.domain.use_case

import com.project.autocompleteapp.domain.model.AutocompleteListItem
import com.project.autocompleteapp.domain.model.AutocompleteType
import com.project.autocompleteapp.domain.repository.GithubRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
    fun `invoke should return empty list immediately when query is blank`() = runTest {
        val result = getUsersAndRepositoriesUseCase("   ")

        assertTrue(result.isSuccess)
        assertEquals(emptyList<AutocompleteListItem>(), result.getOrNull())
        coVerify(exactly = 0) { githubRepository.getUsers(any()) }
        coVerify(exactly = 0) { githubRepository.getRepositories(any()) }
    }

    @Test
    fun `invoke should return combined results when both repository calls succeed`() = runTest {
        val query = "search"
        val users = listOf(AutocompleteListItem(1, "user1", type = AutocompleteType.USER))
        val repos = listOf(AutocompleteListItem(101, "repo1", owner = "owner1", type = AutocompleteType.REPOSITORY))

        coEvery { githubRepository.getUsers(query) } returns Result.success(users)
        coEvery { githubRepository.getRepositories(query) } returns Result.success(repos)

        val result = getUsersAndRepositoriesUseCase(query)

        assertTrue(result.isSuccess)
        assertEquals(users + repos, result.getOrNull())
    }

    @Test
    fun `invoke should return partial results when users fetch fails but repos succeeds`() = runTest {
        val query = "search"
        val repos = listOf(AutocompleteListItem(101, "repo1", owner = "owner1", type = AutocompleteType.REPOSITORY))

        coEvery { githubRepository.getUsers(query) } returns Result.failure(Exception("Network error"))
        coEvery { githubRepository.getRepositories(query) } returns Result.success(repos)

        val result = getUsersAndRepositoriesUseCase(query)

        assertTrue(result.isSuccess)
        assertEquals(repos, result.getOrNull())
    }

    @Test
    fun `invoke should return partial results when repos fetch fails but users succeeds`() = runTest {
        val query = "search"
        val users = listOf(AutocompleteListItem(1, "user1", type = AutocompleteType.USER))

        coEvery { githubRepository.getUsers(query) } returns Result.success(users)
        coEvery { githubRepository.getRepositories(query) } returns Result.failure(Exception("Network error"))

        val result = getUsersAndRepositoriesUseCase(query)

        assertTrue(result.isSuccess)
        assertEquals(users, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when both repository calls fail`() = runTest {
        val query = "search"
        val usersException = Exception("Users fail")
        val reposException = Exception("Repos fail")

        coEvery { githubRepository.getUsers(query) } returns Result.failure(usersException)
        coEvery { githubRepository.getRepositories(query) } returns Result.failure(reposException)

        val result = getUsersAndRepositoriesUseCase(query)

        assertTrue(result.isFailure)
        assertEquals(usersException, result.exceptionOrNull())
    }

    @Test
    fun `invoke should return failure when a repository call throws an unexpected exception`() = runTest {
        val query = "search"
        val unexpectedException = RuntimeException("Crash")

        // We stub BOTH to ensure deterministic behavior in the parallel scope
        coEvery { githubRepository.getUsers(query) } throws unexpectedException
        coEvery { githubRepository.getRepositories(query) } returns Result.success(emptyList())

        val result = getUsersAndRepositoriesUseCase(query)

        assertTrue(result.isFailure)
    }
}
