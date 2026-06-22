package com.project.autocompleteapp.data.repository

import com.project.autocompleteapp.data.api.ApiService
import com.project.autocompleteapp.data.mapper.toDomain
import com.project.autocompleteapp.domain.model.AutocompleteListItem
import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.model.UserExtendedItem
import com.project.autocompleteapp.domain.repository.GithubRepository
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GithubRepositoryImpl @Inject constructor(
    private val api: ApiService
): GithubRepository {

    override suspend fun getUser(username: String): Result<UserExtendedItem> {
        return try {
            val response = api.getUser(username)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    override suspend fun getRepository(
        owner: String,
        repo: String
    ): Result<RepositoryExtendedItem> {
        return try {
            val response = api.getRepository(owner = owner, repo = repo)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    override suspend fun getUsers(input: String): Result<List<AutocompleteListItem>> {
        return try {
            val response = api.getUsers(searchQuery = "$input in:login")
            Result.success(response.toDomain())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    override suspend fun getRepositories(input: String): Result<List<AutocompleteListItem>> {
        return try {
            val response = api.getRepositories(searchQuery = "$input in:full_name")
            Result.success(response.toDomain())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }
}
