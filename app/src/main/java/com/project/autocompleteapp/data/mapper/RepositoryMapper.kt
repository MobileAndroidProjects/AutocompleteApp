package com.project.autocompleteapp.data.mapper

import com.project.autocompleteapp.data.model.OwnerDto
import com.project.autocompleteapp.data.model.RepositoriesDto
import com.project.autocompleteapp.data.model.RepositoryExtendedDto
import com.project.autocompleteapp.data.model.UserExtendedDto
import com.project.autocompleteapp.data.model.UsersDto
import com.project.autocompleteapp.domain.model.AutocompleteListItem
import com.project.autocompleteapp.domain.model.AutocompleteType
import com.project.autocompleteapp.domain.model.OwnerItem
import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.model.UserExtendedItem

fun UsersDto.toDomain(): List<AutocompleteListItem> {
    return this.items.map {
        AutocompleteListItem(
            id = it.id,
            login = it.login,
            type = AutocompleteType.USER
        )
    }
}

fun RepositoriesDto.toDomain(): List<AutocompleteListItem> {
    return this.items.map {
        AutocompleteListItem(
            id = it.id,
            login = it.owner.login,
            repo = it.name,
            type = AutocompleteType.REPOSITORY
        )
    }
}

fun UserExtendedDto.toDomain(): UserExtendedItem {
    return UserExtendedItem(
        id = this.id,
        login = this.login,
        avatarUrl = this.avatarUrl,
        name = this.name,
        company = this.company,
        blog = this.blog
    )
}

fun OwnerDto.toDomain(): OwnerItem {
    return OwnerItem(
        login = this.login
    )
}

fun RepositoryExtendedDto.toDomain(): RepositoryExtendedItem {
    return RepositoryExtendedItem(
        id = this.id,
        name = this.name,
        owner = this.owner.toDomain(),
        description = this.description,
        visibility = this.visibility,
        defaultBranch = this.defaultBranch
    )
}