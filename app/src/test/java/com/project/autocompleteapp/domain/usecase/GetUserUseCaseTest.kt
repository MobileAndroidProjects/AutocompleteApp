package com.project.autocompleteapp.domain.usecase

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
        val userId = 123
        val userExtendedItem = mockk<UserExtendedItem>()
        coEvery { githubRepository.getUser(userId) } returns Result.success(userExtendedItem)

        // When
        val result = getUserUseCase(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(userExtendedItem, result.getOrNull())
    }

    @Test
    fun `invoke should return Failure when repository call fails`() = runTest {
        // Given
        val userId = 123
        val exception = Exception("Network error")
        coEvery { githubRepository.getUser(userId) } returns Result.failure(exception)

        // When
        val result = getUserUseCase(userId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke should catch and return Failure when repository throws exception`() = runTest {
        // Given
        val userId = 123
        val exception = Exception("Unexpected error")
        coEvery { githubRepository.getUser(userId) } throws exception

        // When
        val result = getUserUseCase(userId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
