package com.project.autocompleteapp.domain.usecase

import com.project.autocompleteapp.domain.model.AutocompleteListItem
import com.project.autocompleteapp.domain.model.AutocompleteType
import com.project.autocompleteapp.domain.repository.GithubRepository
import javax.inject.Inject

class GetUsersAndRepositoriesUseCase @Inject constructor(
    private val repository: GithubRepository
) {
    suspend operator fun invoke(query: String): Result<List<AutocompleteListItem>> {
        if (query.isBlank()) return Result.success(emptyList())

        return try {
            val users = repository.getUsers(query).getOrNull()?.items?.map {
                AutocompleteListItem(
                    id = it.id,
                    value = it.login,
                    type = AutocompleteType.USER
                )
            } ?: emptyList()
            val repos = repository.getRepositories(query).getOrNull()?.items?.map {
                AutocompleteListItem(
                    id = it.id,
                    value = it.name,
                    owner = it.owner.login,
                    type = AutocompleteType.REPOSITORY
                )
            } ?: emptyList()

            Result.success(users + repos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}