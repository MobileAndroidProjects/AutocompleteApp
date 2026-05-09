package com.project.autocompleteapp.domain.usecase

import com.project.autocompleteapp.domain.repository.GithubRepository
import com.project.autocompleteapp.util.handleApiRequest
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    val githubRepository: GithubRepository
) {
    operator fun invoke(userId: Int) = handleApiRequest {
        githubRepository.getUser(userId = userId)
    }
}