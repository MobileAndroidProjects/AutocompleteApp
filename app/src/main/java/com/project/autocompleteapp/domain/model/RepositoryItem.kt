package com.project.autocompleteapp.domain.model

data class RepositoryItem(
    val id: Int,
    val name: String,
    val owner: OwnerDto
)
