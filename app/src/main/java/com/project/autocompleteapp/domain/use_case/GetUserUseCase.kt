package com.project.autocompleteapp.domain.use_case

import com.project.autocompleteapp.domain.model.UserExtendedItem
import com.project.autocompleteapp.domain.repository.GithubRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val githubRepository: GithubRepository
) {
    suspend operator fun invoke(username: String): Result<UserExtendedItem> {
        return githubRepository.getUser(username)
    }
}
