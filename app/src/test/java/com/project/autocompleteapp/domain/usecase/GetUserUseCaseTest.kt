package com.project.autocompleteapp.domain.usecase

import app.cash.turbine.test
import com.project.autocompleteapp.domain.model.UserExtendedItem
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
class GetUserUseCaseTest {

    private lateinit var githubRepository: GithubRepository
    private lateinit var getUserUseCase: GetUserUseCase

    @Before
    fun setUp() {
        githubRepository = mockk()
        getUserUseCase = GetUserUseCase(githubRepository)
    }

    @Test
    fun `invoke should emit Loading and then Success when repository call is successful`() = runTest {
        // Given
        val userId = 123
        val userExtendedItem = mockk<UserExtendedItem>()
        coEvery { githubRepository.getUser(userId) } returns Response.success(userExtendedItem)

        // When
        val result = getUserUseCase(userId)

        // Then
        result.test {
            assertTrue(awaitItem() is Resource.Loading)
            val successItem = awaitItem()
            assertTrue(successItem is Resource.Success)
            assertEquals(userExtendedItem, (successItem as Resource.Success).data?.body())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit Loading and then Error when repository call fails`() = runTest {
        // Given
        val userId = 123
        val errorMessage = "Network error"
        coEvery { githubRepository.getUser(userId) } throws Exception(errorMessage)

        // When
        val result = getUserUseCase(userId)

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
