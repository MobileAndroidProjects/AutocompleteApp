package com.project.autocompleteapp.domain.usecase

import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.repository.GithubRepository
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(
    val githubRepository: GithubRepository
) {
    suspend operator fun invoke(owner: String, repo: String) : Result<RepositoryExtendedItem?> {
        return try {
            githubRepository.getRepository(owner = owner, repo = repo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}