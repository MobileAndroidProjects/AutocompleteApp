package com.project.autocompleteapp.data.model

import com.google.gson.annotations.SerializedName

data class RepositoryExtendedDto(
    val id: Int,
    val name: String,
    val owner: OwnerDto,
    val description: String?,
    val visibility: String?,
    @SerializedName("default_branch") val defaultBranch: String?
)