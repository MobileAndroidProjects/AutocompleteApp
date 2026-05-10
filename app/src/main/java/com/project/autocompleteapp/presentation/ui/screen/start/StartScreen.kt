package com.project.autocompleteapp.presentation.ui.screen.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.project.autocompleteapp.R
import com.project.autocompleteapp.navigation.AppScreen
import com.project.autocompleteapp.ui.theme.Typography
import com.project.autocompleteapp.ui.theme.GreenLight

@Composable
fun StartScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = GreenLight
                )
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_m),
                    vertical = dimensionResource(R.dimen.padding_l)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                style = Typography.titleLarge,
                textAlign = TextAlign.Center,
                text = stringResource(R.string.start_screen_description)
            )

            ElevatedButton(
                modifier = Modifier
                    .padding(bottom = dimensionResource(R.dimen.padding_l)),
                onClick = {
                    navController.navigate(AppScreen.HomeScreen.route)
                }
            ) {
                Text(
                    modifier = Modifier
                        .padding(all = dimensionResource(R.dimen.padding_s)),
                    style = Typography.titleLarge,
                    text = stringResource(R.string.start_screen_btn_label)
                )
            }
        }
    }
}
