package com.project.autocompleteapp.domain.model

data class RepositoryExtendedItem(
    val id: Int,
    val name: String,
    val owner: OwnerItem,
    val description: String?,
    val visibility: String?,
    val defaultBranch: String?
)