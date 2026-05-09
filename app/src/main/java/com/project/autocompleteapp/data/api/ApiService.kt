package com.project.autocompleteapp.data.api

import com.project.autocompleteapp.domain.model.RepositoriesDto
import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.model.UserExtendedItem
import com.project.autocompleteapp.domain.model.UsersDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val ITEMS_PER_PAGE = 50

interface ApiService {

    @GET("user/{userId}")
    suspend fun getUser(
        @Path("userId") userId: Int
    ): Response<UserExtendedItem>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Response<RepositoryExtendedItem>

    @GET("search/users")
    suspend fun getUsers(
        @Query("q") searchQuery: String,
        @Query("per_page") perPage: Int = ITEMS_PER_PAGE
    ): Response<UsersDto>

    @GET("search/repositories")
    suspend fun getRepositories(
        @Query("q") searchQuery: String,
        @Query("per_page") perPage: Int = ITEMS_PER_PAGE
    ): Response<RepositoriesDto>
}