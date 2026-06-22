package com.project.autocompleteapp.domain.use_case

import com.project.autocompleteapp.domain.model.AutocompleteListItem
import com.project.autocompleteapp.domain.repository.GithubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GetUsersAndRepositoriesUseCase @Inject constructor(
    private val repository: GithubRepository
) {
    suspend operator fun invoke(query: String): Result<List<AutocompleteListItem>> =
        withContext(Dispatchers.IO) {
            if (query.isBlank()) return@withContext Result.success(emptyList())

            try {
                coroutineScope {
                    val usersDeferred = async { repository.getUsers(query) }
                    val reposDeferred = async { repository.getRepositories(query) }

                    val usersResult = usersDeferred.await()
                    val reposResult = reposDeferred.await()

                    if (usersResult.isFailure && reposResult.isFailure) {
                        Result.failure(usersResult.exceptionOrNull() ?: Exception("Unknown error"))
                    } else {
                        val combinedList = usersResult.getOrDefault(emptyList()) +
                                reposResult.getOrDefault(emptyList())
                        Result.success(combinedList)
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Result.failure(e)
            }
    }
}
