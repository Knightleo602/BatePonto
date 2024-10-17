package com.knightleo.bateponto.widget.ui

import androidx.compose.runtime.Composable
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.components.SquareIconButton
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import com.knightleo.bateponto.widget.R

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
internal fun NoUser(
    modifier: GlanceModifier = GlanceModifier,
    updateUser: () -> Unit
) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(updateUser)
    ) {
        Image(
            provider = ImageProvider(R.drawable.error),
            contentDescription = context.getString(R.string.no_user_description),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.onError)
        )
        Text(
            text = context.getString(R.string.no_user),
            style = TextDefaults.defaultTextStyle.copy(
                textAlign = TextAlign.Center,
                color = GlanceTheme.colors.onError
            )
        )
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
internal fun AddTimeButton(
    onClick: () -> Unit = {},
    modifier: GlanceModifier = GlanceModifier
) {
    val context = LocalContext.current
    SquareIconButton(
        imageProvider = ImageProvider(R.drawable.outline_more_time_24),
        contentDescription = context.getString(R.string.add_new_time_description),
        onClick = onClick,
        modifier = modifier
    )
}