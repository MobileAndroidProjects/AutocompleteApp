package com.project.autocompleteapp.domain.use_case

import com.project.autocompleteapp.domain.model.UserExtendedItem
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
class GetUserUseCaseTest {

    private lateinit var githubRepository: GithubRepository
    private lateinit var getUserUseCase: GetUserUseCase

    @Before
    fun setUp() {
        githubRepository = mockk()
        getUserUseCase = GetUserUseCase(githubRepository)
    }

    @Test
    fun `invoke should return Success when repository call is successful`() = runTest {
        // Given
        val username = "testuser"
        val userExtendedItem = mockk<UserExtendedItem>()
        coEvery { githubRepository.getUser(username) } returns Result.success(userExtendedItem)

        // When
        val result = getUserUseCase(username)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(userExtendedItem, result.getOrNull())
    }

    @Test
    fun `invoke should return Failure when repository call fails`() = runTest {
        // Given
        val username = "testuser"
        val exception = Exception("Network error")
        coEvery { githubRepository.getUser(username) } returns Result.failure(exception)

        // When
        val result = getUserUseCase(username)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should throws exception when repository throws any exception`() = runTest {
        val username = "testuser"
        val exception = Exception("Unexpected error")
        coEvery { githubRepository.getUser(username) } throws exception

        // When
        val throwable = assertThrows<Exception> { getUserUseCase(username) }

        // Then
        assertEquals(exception, throwable)
    }

    @Test
    fun `invoke should throws CancellationException when repository throws CancellationException`() = runTest {
        val username = "testuser"
        val exception = CancellationException("Cancel")
        coEvery { githubRepository.getUser(username) } throws exception

        // When
        val throwable = assertThrows<CancellationException> { getUserUseCase(username) }

        // Then
        assertEquals(exception, throwable)
    }
}
