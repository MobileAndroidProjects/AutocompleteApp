package com.project.autocompleteapp.domain.usecase

import com.project.autocompleteapp.domain.repository.GithubRepository
import com.project.autocompleteapp.util.handleApiRequest
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(
    val githubRepository: GithubRepository
) {
    operator fun invoke(owner: String, repo: String) = handleApiRequest {
        githubRepository.getRepository(owner = owner, repo = repo)
    }
}