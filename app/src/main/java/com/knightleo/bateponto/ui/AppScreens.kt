package com.knightleo.bateponto.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.knightleo.bateponto.ui.screens.daylist.ListScreen

@Composable
fun AppScreens(modifier: Modifier = Modifier) {
    ListScreen(modifier = modifier)
}