package com.project.autocompleteapp.domain.repository

import com.project.autocompleteapp.domain.model.RepositoriesDto
import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.model.UserExtendedItem
import com.project.autocompleteapp.domain.model.UsersDto
import retrofit2.Response

interface GithubRepository {

    suspend fun getUser(userId: Int): Response<UserExtendedItem>

    suspend fun getRepository(owner: String, repo: String): Response<RepositoryExtendedItem>

    suspend fun getUsers(input: String): Response<UsersDto>

    suspend fun getRepositories(input: String): Response<RepositoriesDto>
}
