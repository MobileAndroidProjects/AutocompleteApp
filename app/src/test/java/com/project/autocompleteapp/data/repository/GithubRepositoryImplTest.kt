package com.project.autocompleteapp.data.repository

import com.project.autocompleteapp.data.api.ApiService
import com.project.autocompleteapp.domain.model.RepositoriesDto
import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.model.UserExtendedItem
import com.project.autocompleteapp.domain.model.UsersDto
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
class GithubRepositoryImplTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: GithubRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        repository = GithubRepositoryImpl(apiService)
    }

    @Test
    fun `getUser should return Success when apiService returns successful response`() = runTest {
        val userId = 1
        val userItem = mockk<UserExtendedItem>()
        coEvery { apiService.getUser(userId) } returns Response.success(userItem)

        val result = repository.getUser(userId)

        assertTrue(result.isSuccess)
        assertEquals(userItem, result.getOrNull())
    }

    @Test
    fun `getUser should return Failure when apiService returns null body`() = runTest {
        val userId = 1
        coEvery { apiService.getUser(userId) } returns Response.success(null)

        val result = repository.getUser(userId)

        assertTrue(result.isFailure)
        assertEquals("User not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getRepository should return Success when apiService returns successful response`() = runTest {
        val owner = "owner"
        val repo = "repo"
        val repoItem = mockk<RepositoryExtendedItem>()
        coEvery { apiService.getRepository(owner, repo) } returns Response.success(repoItem)

        val result = repository.getRepository(owner, repo)

        assertTrue(result.isSuccess)
        assertEquals(repoItem, result.getOrNull())
    }

    @Test
    fun `getUsers should return Success when apiService returns successful response`() = runTest {
        val input = "test"
        val usersDto = mockk<UsersDto>()
        coEvery { apiService.getUsers("test in:login") } returns Response.success(usersDto)

        val result = repository.getUsers(input)

        assertTrue(result.isSuccess)
        assertEquals(usersDto, result.getOrNull())
    }

    @Test
    fun `getRepositories should return Success when apiService returns successful response`() = runTest {
        val input = "test"
        val repositoriesDto = mockk<RepositoriesDto>()
        coEvery { apiService.getRepositories("test in:name") } returns Response.success(repositoriesDto)

        val result = repository.getRepositories(input)

        assertTrue(result.isSuccess)
        assertEquals(repositoriesDto, result.getOrNull())
    }
}
