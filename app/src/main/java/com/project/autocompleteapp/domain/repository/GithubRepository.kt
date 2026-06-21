package com.project.autocompleteapp.domain.repository

import com.project.autocompleteapp.domain.model.RepositoriesDto
import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.model.UserExtendedItem
import com.project.autocompleteapp.domain.model.UsersDto

interface GithubRepository {

    suspend fun getUser(userId: Int): Result<UserExtendedItem>

    suspend fun getRepository(owner: String, repo: String): Result<RepositoryExtendedItem>

    suspend fun getUsers(input: String): Result<UsersDto>

    suspend fun getRepositories(input: String): Result<RepositoriesDto>
}
