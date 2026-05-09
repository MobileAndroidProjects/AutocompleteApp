package com.project.autocompleteapp.domain.usecase

import app.cash.turbine.test
import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
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
class GetRepositoryUseCaseTest {

    private lateinit var githubRepository: GithubRepository
    private lateinit var getRepositoryUseCase: GetRepositoryUseCase

    @Before
    fun setUp() {
        githubRepository = mockk()
        getRepositoryUseCase = GetRepositoryUseCase(githubRepository)
    }

    @Test
    fun `invoke should emit Loading and then Success when repository call is successful`() = runTest {
        // Given
        val owner = "owner"
        val repo = "repo"
        val repositoryExtendedItem = mockk<RepositoryExtendedItem>()
        coEvery { githubRepository.getRepository(owner, repo) } returns Response.success(repositoryExtendedItem)

        // When
        val result = getRepositoryUseCase(owner, repo)

        // Then
        result.test {
            assertTrue(awaitItem() is Resource.Loading)
            val successItem = awaitItem()
            assertTrue(successItem is Resource.Success)
            assertEquals(repositoryExtendedItem, (successItem as Resource.Success).data?.body())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit Loading and then Error when repository call fails`() = runTest {
        // Given
        val owner = "owner"
        val repo = "repo"
        val errorMessage = "Network error"
        coEvery { githubRepository.getRepository(owner, repo) } throws Exception(errorMessage)

        // When
        val result = getRepositoryUseCase(owner, repo)

        // Then
        result.test {
            assertTrue(awaitItem() is Resource.Loading)
            val errorItem = awaitItem()
            assertTrue(errorItem is Resource.Error)
            assertEquals(errorMessage, (errorItem as Resource.Error).message)
            awaitComplete()
        }
    }
}
