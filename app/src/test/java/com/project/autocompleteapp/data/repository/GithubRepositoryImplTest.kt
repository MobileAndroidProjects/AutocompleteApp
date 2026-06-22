package com.project.autocompleteapp.data.repository

import com.project.autocompleteapp.data.api.ApiService
import com.project.autocompleteapp.data.model.OwnerDto
import com.project.autocompleteapp.data.model.RepositoriesDto
import com.project.autocompleteapp.data.model.RepositoryDto
import com.project.autocompleteapp.data.model.RepositoryExtendedDto
import com.project.autocompleteapp.data.model.UserDto
import com.project.autocompleteapp.data.model.UserExtendedDto
import com.project.autocompleteapp.data.model.UsersDto
import com.project.autocompleteapp.domain.model.AutocompleteType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

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
    fun `getUser should return Success with domain model when apiService returns DTO`() = runTest {
        val username = "testuser"
        val userDto = UserExtendedDto(
            id = 1,
            login = username,
            avatarUrl = "url",
            name = "Name",
            company = "Company",
            blog = "blog"
        )
        coEvery { apiService.getUser(username) } returns userDto

        val result = repository.getUser(username)

        assertTrue(result.isSuccess)
        val domainModel = result.getOrNull()
        assertEquals(userDto.id, domainModel?.id)
        assertEquals(userDto.login, domainModel?.login)
    }

    @Test
    fun `getUser should return Failure when apiService throws exception`() = runTest {
        val username = "testuser"
        val exception = RuntimeException("Network error")
        coEvery { apiService.getUser(username) } throws exception

        val result = repository.getUser(username)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getRepository should return Success when apiService returns DTO`() = runTest {
        val owner = "owner"
        val repoName = "repo"
        val repoDto = RepositoryExtendedDto(
            id = 1,
            name = repoName,
            owner = OwnerDto(login = owner),
            description = "desc",
            visibility = "public",
            defaultBranch = "main"
        )
        coEvery { apiService.getRepository(owner, repoName) } returns repoDto

        val result = repository.getRepository(owner, repoName)

        assertTrue(result.isSuccess)
        assertEquals(repoName, result.getOrNull()?.name)
        assertEquals(owner, result.getOrNull()?.owner?.login)
    }

    @Test
    fun `getRepository should return Failure when apiService throws exception`() = runTest {
        val owner = "owner"
        val repoName = "repo"
        val exception = RuntimeException("Not found")
        coEvery { apiService.getRepository(owner, repoName) } throws exception

        val result = repository.getRepository(owner, repoName)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getUsers should return mapped AutocompleteListItems`() = runTest {
        val input = "test"
        val usersDto = UsersDto(
            items = listOf(
                UserDto(id = 1, login = "user1")
            )
        )
        coEvery { apiService.getUsers("$input in:login") } returns usersDto

        val result = repository.getUsers(input)

        assertTrue(result.isSuccess)
        val items = result.getOrNull()
        assertEquals(1, items?.size)
        assertEquals("user1", items?.first()?.value)
        assertEquals(AutocompleteType.USER, items?.first()?.type)
    }

    @Test
    fun `getUsers should return Failure when apiService throws exception`() = runTest {
        val input = "test"
        val exception = RuntimeException("Search failed")
        coEvery { apiService.getUsers("$input in:login") } throws exception

        val result = repository.getUsers(input)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getRepositories should return mapped AutocompleteListItems`() = runTest {
        val input = "test"
        val reposDto = RepositoriesDto(
            items = listOf(
                RepositoryDto(
                    id = 1,
                    name = "repo1",
                    owner = OwnerDto(login = "owner1")
                )
            )
        )
        coEvery { apiService.getRepositories("$input in:full_name") } returns reposDto

        val result = repository.getRepositories(input)

        assertTrue(result.isSuccess)
        val items = result.getOrNull()
        assertEquals(1, items?.size)
        assertEquals("repo1", items?.first()?.value)
        assertEquals(AutocompleteType.REPOSITORY, items?.first()?.type)
    }

    @Test
    fun `getRepositories should return Failure when apiService throws exception`() = runTest {
        val input = "test"
        val exception = RuntimeException("Search failed")
        coEvery { apiService.getRepositories("$input in:full_name") } throws exception

        val result = repository.getRepositories(input)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
