package com.project.autocompleteapp.data.api

import com.project.autocompleteapp.data.model.RepositoriesDto
import com.project.autocompleteapp.data.model.RepositoryExtendedDto
import com.project.autocompleteapp.data.model.UserExtendedDto
import com.project.autocompleteapp.data.model.UsersDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val ITEMS_PER_PAGE = 50

interface ApiService {

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): UserExtendedDto

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): RepositoryExtendedDto

    @GET("search/users")
    suspend fun getUsers(
        @Query("q") searchQuery: String,
        @Query("per_page") perPage: Int = ITEMS_PER_PAGE
    ): UsersDto

    @GET("search/repositories")
    suspend fun getRepositories(
        @Query("q") searchQuery: String,
        @Query("per_page") perPage: Int = ITEMS_PER_PAGE
    ): RepositoriesDto
}