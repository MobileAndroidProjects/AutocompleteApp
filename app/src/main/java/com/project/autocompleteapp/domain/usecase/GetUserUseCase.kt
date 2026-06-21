package com.project.autocompleteapp.domain.usecase

import com.project.autocompleteapp.domain.model.UserExtendedItem
import com.project.autocompleteapp.domain.repository.GithubRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    val githubRepository: GithubRepository
) {
    suspend operator fun invoke(userId: Int): Result<UserExtendedItem> {
        return try {
            githubRepository.getUser(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}