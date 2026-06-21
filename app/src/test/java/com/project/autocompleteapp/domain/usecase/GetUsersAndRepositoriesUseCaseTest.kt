package com.project.autocompleteapp.domain.usecase

import com.project.autocompleteapp.domain.model.AutocompleteType
import com.project.autocompleteapp.domain.model.OwnerDto
import com.project.autocompleteapp.domain.model.RepositoriesDto
import com.project.autocompleteapp.domain.model.RepositoryItem
import com.project.autocompleteapp.domain.model.UserItem
import com.project.autocompleteapp.domain.model.UsersDto
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
    fun `invoke should return empty list when query is blank`() = runTest {
        val result = getUsersAndRepositoriesUseCase("   ")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
    }

    @Test
    fun `invoke should return combined list of users and repositories`() = runTest {
        // Given
        val query = "test"
        val userItem = UserItem(id = 1, login = "user1")
        val usersDto = UsersDto(items = listOf(userItem))
        val ownerDto = OwnerDto(login = "owner1")
        val repositoryItem = RepositoryItem(id = 101, name = "repo1", owner = ownerDto)
        val repositoriesDto = RepositoriesDto(items = listOf(repositoryItem))

        coEvery { githubRepository.getUsers(query) } returns Result.success(usersDto)
        coEvery { githubRepository.getRepositories(query) } returns Result.success(repositoriesDto)

        // When
        val result = getUsersAndRepositoriesUseCase(query)

        // Then
        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertEquals(2, data?.size)
        
        val userResult = data?.get(0)
        assertEquals(1, userResult?.id)
        assertEquals("user1", userResult?.value)
        assertEquals(AutocompleteType.USER, userResult?.type)

        val repoResult = data?.get(1)
        assertEquals(101, repoResult?.id)
        assertEquals("repo1", repoResult?.value)
        assertEquals("owner1", repoResult?.owner)
        assertEquals(AutocompleteType.REPOSITORY, repoResult?.type)
    }

    @Test
    fun `invoke should return Success with partial data if one call fails`() = runTest {
        // Given
        val query = "test"
        val userItem = UserItem(id = 1, login = "user1")
        val usersDto = UsersDto(items = listOf(userItem))

        coEvery { githubRepository.getUsers(query) } returns Result.success(usersDto)
        coEvery { githubRepository.getRepositories(query) } returns Result.failure(Exception("API Error"))

        // When
        val result = getUsersAndRepositoriesUseCase(query)

        // Then
        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertEquals(1, data?.size)
        assertEquals("user1", data?.get(0)?.value)
    }

    @Test
    fun `invoke should return Failure if an unexpected exception occurs`() = runTest {
        // Given
        val query = "test"
        coEvery { githubRepository.getUsers(query) } throws RuntimeException("Unexpected")

        // When
        val result = getUsersAndRepositoriesUseCase(query)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}
