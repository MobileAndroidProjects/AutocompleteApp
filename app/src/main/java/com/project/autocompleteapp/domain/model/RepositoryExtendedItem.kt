package com.project.autocompleteapp.domain.model

import com.google.gson.annotations.SerializedName

data class RepositoryExtendedItem(
    val id: Int,
    val name: String,
    val owner: OwnerDto,
    val description: String?,
    val visibility: String?,
    @SerializedName("default_branch") val defaultBranch: String?
)