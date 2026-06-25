package com.project.autocompleteapp.domain.use_case

import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.repository.GithubRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import kotlin.coroutines.cancellation.CancellationException

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
    fun `invoke should return Success when repository call is successful`() = runTest {
        // Given
        val owner = "owner"
        val repo = "repo"
        val repositoryExtendedItem = mockk<RepositoryExtendedItem>()
        coEvery { githubRepository.getRepository(owner, repo) } returns Result.success(repositoryExtendedItem)

        // When
        val result = getRepositoryUseCase(owner, repo)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(repositoryExtendedItem, result.getOrNull())
    }

    @Test
    fun `invoke should return Failure when repository call fails`() = runTest {
        // Given
        val owner = "owner"
        val repo = "repo"
        val exception = Exception("Network error")
        coEvery { githubRepository.getRepository(owner, repo) } returns Result.failure(exception)

        // When
        val result = getRepositoryUseCase(owner, repo)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should throws exception when repository throws any exception`() = runTest {
        // Given
        val owner = "owner"
        val repo = "repo"
        val exception = Exception("Unexpected error")
        coEvery { githubRepository.getRepository(owner, repo) } throws exception

        // When
        val throwable = assertThrows<Exception> { getRepositoryUseCase(owner, repo) }

        // Then
        assertEquals(exception, throwable)
    }

    @Test
    fun `invoke should throws CancellationException when repository throws CancellationException`() = runTest {
        val owner = "owner"
        val repo = "repo"
        val exception = CancellationException("Cancel")
        coEvery { githubRepository.getRepository(owner, repo) } throws exception

        // When
        val throwable = assertThrows<CancellationException> { getRepositoryUseCase(owner, repo) }

        // Then
        assertEquals(exception, throwable)
    }
}
