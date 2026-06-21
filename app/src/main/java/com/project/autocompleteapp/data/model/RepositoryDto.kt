package com.project.autocompleteapp.data.model

data class RepositoryDto(
    val id: Int,
    val name: String,
    val owner: OwnerDto
)
