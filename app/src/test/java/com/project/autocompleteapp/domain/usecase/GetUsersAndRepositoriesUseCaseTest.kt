package com.project.autocompleteapp.domain.usecase

import app.cash.turbine.test
import com.project.autocompleteapp.domain.model.AutocompleteType
import com.project.autocompleteapp.domain.model.OwnerDto
import com.project.autocompleteapp.domain.model.RepositoriesDto
import com.project.autocompleteapp.domain.model.RepositoryItem
import com.project.autocompleteapp.domain.model.UserItem
import com.project.autocompleteapp.domain.model.UsersDto
import com.project.autocompleteapp.domain.repository.GithubRepository
import com.project.autocompleteapp.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

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
    fun `invoke should emit Loading and then Success when repository calls are successful`() = runTest {
        // Given
        val input = "test"
        val userItem = UserItem(id = 1, login = "user1")
        val usersDto = UsersDto(items = listOf(userItem))
        val ownerDto = OwnerDto(login = "owner1")
        val repositoryItem = RepositoryItem(id = 101, name = "repo1", owner = ownerDto)
        val repositoriesDto = RepositoriesDto(items = listOf(repositoryItem))

        coEvery { githubRepository.getUsers(input) } returns Response.success(usersDto)
        coEvery { githubRepository.getRepositories(input) } returns Response.success(repositoriesDto)

        // When
        val result = getUsersAndRepositoriesUseCase(input)

        // Then
        result.test {
            assertTrue(awaitItem() is Resource.Loading)
            val successItem = awaitItem()
            assertTrue(successItem is Resource.Success)
            val data = (successItem as Resource.Success).data
            assertEquals(2, data?.size)
            assertEquals("user1", data?.get(0)?.value)
            assertEquals(AutocompleteType.USER, data?.get(0)?.type)
            assertEquals("repo1", data?.get(1)?.value)
            assertEquals(AutocompleteType.REPOSITORY, data?.get(1)?.type)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit Loading and then Error when users call fail`() = runTest {
        // Given
        val input = "test"
        coEvery { githubRepository.getUsers(input) } returns Response.error(404, mockk(relaxed = true))
        coEvery { githubRepository.getRepositories(input) } returns Response.success(RepositoriesDto(emptyList()))

        // When
        val result = getUsersAndRepositoriesUseCase(input)

        // Then
        result.test {
            assertTrue(awaitItem() is Resource.Loading)
            val errorItem = awaitItem()
            assertTrue(errorItem is Resource.Error)
            assertEquals("Error code: 404", (errorItem as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit Loading and then Error when repositories call fail`() = runTest {
        // Given
        val input = "test"
        coEvery { githubRepository.getUsers(input) } returns Response.success(UsersDto(emptyList()))
        coEvery { githubRepository.getRepositories(input) } returns Response.error(404, mockk(relaxed = true))

        // When
        val result = getUsersAndRepositoriesUseCase(input)

        // Then
        result.test {
            assertTrue(awaitItem() is Resource.Loading)
            val errorItem = awaitItem()
            assertTrue(errorItem is Resource.Error)
            assertEquals("Error code: 404", (errorItem as Resource.Error).message)
            awaitComplete()
        }
    }
}
