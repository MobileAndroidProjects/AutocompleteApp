package com.project.autocompleteapp.util

import app.cash.turbine.test
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResourceTest {

    @Test
    fun `handleApiRequest should emit Loading and then Success`() = runBlocking {
        val expectedData = "Success Data"
        val flow = handleApiRequest { expectedData }

        flow.test {
            assertTrue(awaitItem() is Resource.Loading)
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals(expectedData, (success as Resource.Success).data)
            awaitComplete()
        }
    }

    @Test
    fun `handleApiRequest should emit Loading and then Error on exception`() = runBlocking {
        val errorMessage = "API Error"
        val flow = handleApiRequest<String> { throw Exception(errorMessage) }

        flow.test {
            assertTrue(awaitItem() is Resource.Loading)
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertEquals(errorMessage, (error as Resource.Error).message)
            awaitComplete()
        }
    }
}
