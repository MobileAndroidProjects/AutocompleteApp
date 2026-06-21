package com.project.autocompleteapp.domain.repository

import com.project.autocompleteapp.domain.model.AutocompleteListItem
import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.model.UserExtendedItem

interface GithubRepository {

    suspend fun getUser(username: String): Result<UserExtendedItem>

    suspend fun getRepository(owner: String, repo: String): Result<RepositoryExtendedItem>

    suspend fun getUsers(input: String): Result<List<AutocompleteListItem>>

    suspend fun getRepositories(input: String): Result<List<AutocompleteListItem>>
}
