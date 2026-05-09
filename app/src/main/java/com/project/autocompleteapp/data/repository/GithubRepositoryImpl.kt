package com.project.autocompleteapp.data.repository

import com.project.autocompleteapp.data.api.ApiService
import com.project.autocompleteapp.domain.model.RepositoriesDto
import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.model.UserExtendedItem
import com.project.autocompleteapp.domain.model.UsersDto
import com.project.autocompleteapp.domain.repository.GithubRepository
import retrofit2.Response
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    val api: ApiService
): GithubRepository {

    override suspend fun getUser(userId: Int): Response<UserExtendedItem> {
        return api.getUser(userId = userId)
    }

    override suspend fun getRepository(
        owner: String,
        repo: String
    ): Response<RepositoryExtendedItem> {
        return api.getRepository(owner = owner, repo = repo)
    }

    override suspend fun getUsers(input: String): Response<UsersDto> {
        return api.getUsers(searchQuery = "$input in:login")
    }

    override suspend fun getRepositories(input: String): Response<RepositoriesDto> {
        return api.getRepositories(searchQuery = "$input in:name")
    }
}
