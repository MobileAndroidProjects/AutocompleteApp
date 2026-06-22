package com.project.autocompleteapp.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.project.autocompleteapp.R
import com.project.autocompleteapp.ui.theme.Typography
import com.project.autocompleteapp.domain.model.RepositoryExtendedItem
import com.project.autocompleteapp.domain.model.UserExtendedItem

@Composable
fun DetailsField(
    selectedUser: UserExtendedItem?,
    selectedRepository: RepositoryExtendedItem?
) {
    selectedUser?.let {

        Text(
            style = Typography.titleLarge,
            text = stringResource(R.string.selected_user_txt)
        )

        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = dimensionResource(R.dimen.padding_s))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.padding_m))
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
            ) {
                DetailsTextRow(
                    text = stringResource(R.string.id_row, it.id.toString())
                )

                DetailsTextRow(
                    text = stringResource(R.string.login_row, it.login)
                )

                if (!it.name.isNullOrEmpty()) {
                    DetailsTextRow(
                        text = stringResource(R.string.name_row, it.name)
                    )
                }

                if (!it.company.isNullOrEmpty()) {
                    DetailsTextRow(
                        text = stringResource(R.string.company_row, it.company)
                    )
                }

                if (!it.blog.isNullOrEmpty()) {
                    DetailsTextRow(
                        style = Typography.bodyMedium,
                        text = stringResource(R.string.blog_row, it.blog)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (!it.avatarUrl.isNullOrEmpty()) {
                    AsyncImage(
                        modifier = Modifier
                            .padding(
                                vertical = dimensionResource(R.dimen.padding_m)
                            )
                            .fillMaxWidth(),
                        model = it.avatarUrl,
                        contentDescription = null
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.padding_s))
        )
    }

    selectedRepository?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.padding_m))
        ) {
            Text(
                style = Typography.titleLarge,
                text = stringResource(R.string.selected_repository_txt)
            )

            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = dimensionResource(R.dimen.padding_s))
            )

            DetailsTextRow(
                text = stringResource(R.string.id_row, it.id.toString())
            )

            DetailsTextRow(
                text = stringResource(R.string.name_row, it.name)
            )

            DetailsTextRow(
                text = stringResource(R.string.owner_row, it.owner.login)
            )

            if (!it.visibility.isNullOrEmpty()) {
                DetailsTextRow(
                    text = stringResource(R.string.visibility_row, it.visibility)
                )
            }

            if (!it.defaultBranch.isNullOrEmpty()) {
                DetailsTextRow(
                    text = stringResource(R.string.default_branch_row, it.defaultBranch)
                )
            }

            if (!it.description.isNullOrEmpty()) {
                DetailsTextRow(
                    text = stringResource(R.string.description_row, it.description)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.padding_s))
        )
    }
}
