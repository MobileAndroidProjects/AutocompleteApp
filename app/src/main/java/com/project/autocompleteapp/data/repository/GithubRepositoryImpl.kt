package com.project.autocompleteapp.data.repository

import com.project.autocompleteapp.data.api.ApiService
import com.project.autocompleteapp.domain.model.RepositoriesDto
import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.model.UserExtendedItem
import com.project.autocompleteapp.domain.model.UsersDto
import com.project.autocompleteapp.domain.repository.GithubRepository
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val api: ApiService
): GithubRepository {

    override suspend fun getUser(userId: Int): Result<UserExtendedItem> = runCatching {
        val response = api.getUser(userId = userId)
        response.body() ?: throw Exception("User not found")
    }

    override suspend fun getRepository(
        owner: String,
        repo: String
    ): Result<RepositoryExtendedItem> = runCatching {
        val response = api.getRepository(owner = owner, repo = repo)
        response.body() ?: throw Exception("Repository not found")
    }

    override suspend fun getUsers(input: String): Result<UsersDto> = runCatching {
        val response = api.getUsers(searchQuery = "$input in:login")
        response.body() ?: throw Exception("User search failed")
    }

    override suspend fun getRepositories(input: String): Result<RepositoriesDto> = runCatching {
        val response = api.getRepositories(searchQuery = "$input in:name")
        response.body() ?: throw Exception("Repository search failed")
    }
}