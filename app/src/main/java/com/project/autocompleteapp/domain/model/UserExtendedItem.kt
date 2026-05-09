package com.project.autocompleteapp.domain.model

import com.google.gson.annotations.SerializedName

data class UserExtendedItem(
    val id: Int,
    val login: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    val name: String?,
    val company: String?,
    val blog: String?
)
