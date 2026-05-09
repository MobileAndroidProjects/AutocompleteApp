package com.project.autocompleteapp.domain.usecase

import com.project.autocompleteapp.domain.model.AutocompleteListItem
import com.project.autocompleteapp.domain.model.AutocompleteType
import com.project.autocompleteapp.domain.repository.GithubRepository
import com.project.autocompleteapp.util.handleApiRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import javax.inject.Inject

private const val TIMEOUT = 800L

class GetUsersAndRepositoriesUseCase @Inject constructor(
    val githubRepository: GithubRepository
) {
    operator fun invoke(input: String) = handleApiRequest {
        coroutineScope {
            delay(TIMEOUT)

            val repositories = async { githubRepository.getRepositories(input = input) }
            val users = async { githubRepository.getUsers(input = input) }

            val repositoriesResult = repositories.await()
            val usersResult = users.await()

            if (!usersResult.isSuccessful) {
                throw Exception("Error code: ${usersResult.code()}")
            }

            if (!repositoriesResult.isSuccessful) {
                throw Exception("Error code: ${repositoriesResult.code()}")
            }

            usersResult.body()?.items?.map {
                AutocompleteListItem(
                    id = it.id,
                    value = it.login,
                    type = AutocompleteType.USER
                )
            }?.plus(
                repositoriesResult.body()?.items?.map {
                    AutocompleteListItem(
                        id = it.id,
                        value = it.name,
                        owner = it.owner.login,
                        type = AutocompleteType.REPOSITORY
                    )
                }.orEmpty()
            )
        }
    }
}
