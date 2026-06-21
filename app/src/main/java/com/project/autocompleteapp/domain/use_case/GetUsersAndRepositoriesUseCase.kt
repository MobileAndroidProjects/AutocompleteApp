package com.project.autocompleteapp.domain.use_case

import com.project.autocompleteapp.domain.model.AutocompleteListItem
import com.project.autocompleteapp.domain.repository.GithubRepository
import javax.inject.Inject

class GetUsersAndRepositoriesUseCase @Inject constructor(
    private val repository: GithubRepository
) {
    suspend operator fun invoke(query: String): Result<List<AutocompleteListItem>> = runCatching {
        if (query.isBlank()) return@runCatching emptyList()

        val users = repository.getUsers(query).getOrNull() ?: emptyList()
        val repos = repository.getRepositories(query).getOrNull() ?: emptyList()

        users + repos
    }
}
