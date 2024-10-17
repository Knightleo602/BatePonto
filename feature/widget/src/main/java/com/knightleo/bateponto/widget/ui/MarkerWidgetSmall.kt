package com.knightleo.bateponto.widget.ui

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import com.knightleo.bateponto.widget.data.State

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
internal fun SmallContent(
    currentState: State = State.LOADING,
    modifier: GlanceModifier = GlanceModifier,
    onAddTime: () -> Unit = {},
    updateUser: () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = GlanceModifier
            .background(currentState.backgroundColor)
            .then(modifier)
    ) {
        when (currentState) {
            State.NO_USER -> NoUser(updateUser = updateUser)
            else -> AddTimeButton(onClick = onAddTime)
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
internal fun SmallContentWidgetPreview() {
    SmallContent(State.OK)
}