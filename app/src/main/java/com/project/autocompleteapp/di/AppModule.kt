package com.project.autocompleteapp.di

import com.project.autocompleteapp.data.repository.GithubRepositoryImpl
import com.project.autocompleteapp.domain.repository.GithubRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindGithubRepository(impl: GithubRepositoryImpl): GithubRepository
}
