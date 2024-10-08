package com.knightleo.bateponto.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.knightleo.bateponto.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    currentPageTitle: String = stringResource(R.string.app_name),
    onMenuClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = currentPageTitle) },
//        navigationIcon = {
//            IconButton(
//                onClick = onMenuClicked
//            ) {
//                Icon(Icons.Default.Menu, contentDescription = "Menu")
//            }
//        },
//        actions = {
//            IconButton(
//                onClick = onSettingsClicked
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.baseline_settings_24),
//                    contentDescription = stringResource(R.string.config)
//                )
//            }
//        }
    )
}