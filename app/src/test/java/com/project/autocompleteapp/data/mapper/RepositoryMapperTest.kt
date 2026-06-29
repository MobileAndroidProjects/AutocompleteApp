package com.project.autocompleteapp.data.mapper

import com.project.autocompleteapp.data.model.OwnerDto
import com.project.autocompleteapp.data.model.RepositoriesDto
import com.project.autocompleteapp.data.model.RepositoryDto
import com.project.autocompleteapp.data.model.RepositoryExtendedDto
import com.project.autocompleteapp.data.model.UserDto
import com.project.autocompleteapp.data.model.UserExtendedDto
import com.project.autocompleteapp.data.model.UsersDto
import com.project.autocompleteapp.domain.model.AutocompleteType
import org.junit.Assert.assertEquals
import org.junit.Test

class RepositoryMapperTest {

    @Test
    fun `UsersDto toDomain should map to list of AutocompleteListItem`() {
        val dto = UsersDto(
            items = listOf(
                UserDto(id = 1, login = "user1"),
                UserDto(id = 2, login = "user2")
            )
        )

        val result = dto.toDomain()

        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals("user1", result[0].login)
        assertEquals(AutocompleteType.USER, result[0].type)
        assertEquals(2, result[1].id)
        assertEquals("user2", result[1].login)
        assertEquals(AutocompleteType.USER, result[1].type)
    }

    @Test
    fun `RepositoriesDto toDomain should map to list of AutocompleteListItem`() {
        val dto = RepositoriesDto(
            items = listOf(
                RepositoryDto(id = 1, name = "repo1", owner = OwnerDto(login = "owner1")),
                RepositoryDto(id = 2, name = "repo2", owner = OwnerDto(login = "owner2"))
            )
        )

        val result = dto.toDomain()

        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals("owner1", result[0].login)
        assertEquals("repo1", result[0].repo)
        assertEquals(AutocompleteType.REPOSITORY, result[0].type)
        assertEquals(2, result[1].id)
        assertEquals("owner2", result[1].login)
        assertEquals("repo2", result[1].repo)
        assertEquals(AutocompleteType.REPOSITORY, result[1].type)
    }

    @Test
    fun `UserExtendedDto toDomain should map to UserExtendedItem`() {
        val dto = UserExtendedDto(
            id = 1,
            login = "user1",
            avatarUrl = "url",
            name = "name",
            company = "company",
            blog = "blog"
        )

        val result = dto.toDomain()

        assertEquals(dto.id, result.id)
        assertEquals(dto.login, result.login)
        assertEquals(dto.avatarUrl, result.avatarUrl)
        assertEquals(dto.name, result.name)
        assertEquals(dto.company, result.company)
        assertEquals(dto.blog, result.blog)
    }

    @Test
    fun `OwnerDto toDomain should map to OwnerItem`() {
        val dto = OwnerDto(login = "owner1")

        val result = dto.toDomain()

        assertEquals(dto.login, result.login)
    }

    @Test
    fun `RepositoryExtendedDto toDomain should map to RepositoryExtendedItem`() {
        val dto = RepositoryExtendedDto(
            id = 1,
            name = "repo1",
            owner = OwnerDto(login = "owner1"),
            description = "desc",
            visibility = "public",
            defaultBranch = "main"
        )

        val result = dto.toDomain()

        assertEquals(dto.id, result.id)
        assertEquals(dto.name, result.name)
        assertEquals(dto.owner.login, result.owner.login)
        assertEquals(dto.description, result.description)
        assertEquals(dto.visibility, result.visibility)
        assertEquals(dto.defaultBranch, result.defaultBranch)
    }
}
