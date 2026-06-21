package com.project.autocompleteapp.domain.use_case

import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.repository.GithubRepository
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(
    private val githubRepository: GithubRepository
) {
    suspend operator fun invoke(owner: String, repo: String): Result<RepositoryExtendedItem> = runCatching {
        githubRepository.getRepository(owner = owner, repo = repo).getOrThrow()
    }
}
