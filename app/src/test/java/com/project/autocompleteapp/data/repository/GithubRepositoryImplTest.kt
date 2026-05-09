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
    fun `getUser should call apiService getUser`() = runTest {
        val userId = 1
        val expectedResponse = Response.success(mockk<UserExtendedItem>())
        coEvery { apiService.getUser(userId) } returns expectedResponse

        val actualResponse = repository.getUser(userId)

        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `getRepository should call apiService getRepository`() = runTest {
        val owner = "owner"
        val repo = "repo"
        val expectedResponse = Response.success(mockk<RepositoryExtendedItem>())
        coEvery { apiService.getRepository(owner, repo) } returns expectedResponse

        val actualResponse = repository.getRepository(owner, repo)

        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `getUsers should call apiService getUsers with login qualifier`() = runTest {
        val input = "test"
        val expectedResponse = Response.success(mockk<UsersDto>())
        coEvery { apiService.getUsers("test in:login") } returns expectedResponse

        val actualResponse = repository.getUsers(input)

        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `getRepositories should call apiService getRepositories with name qualifier`() = runTest {
        val input = "test"
        val expectedResponse = Response.success(mockk<RepositoriesDto>())
        coEvery { apiService.getRepositories("test in:name") } returns expectedResponse

        val actualResponse = repository.getRepositories(input)

        assertEquals(expectedResponse, actualResponse)
    }
}
