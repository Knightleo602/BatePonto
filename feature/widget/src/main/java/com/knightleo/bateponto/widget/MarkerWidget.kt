package com.knightleo.bateponto.widget

import android.content.Context
import android.graphics.drawable.Icon
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.SquareIconButton
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.knightleo.bateponto.data.AppDatabase

class MarkerWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*>? = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            MainContent()
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
fun MainContent(
    modifier: GlanceModifier = GlanceModifier
) {
    val preferences = currentState<Preferences>()
    val loading = preferences[booleanPreferencesKey(State.LOADING_KEY)] == true
    val noUser = preferences[booleanPreferencesKey(State.NO_USER_KEY)] == true

    GlanceTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier
                .background(GlanceTheme.colors.widgetBackground)
        ) {
            when {
                loading -> CircularProgressIndicator()
                noUser -> NoUser()
                else -> AddTimeButton()
            }
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
fun NoUser(modifier: GlanceModifier = GlanceModifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            provider = ImageProvider(R.drawable.outline_more_time_24),
            contentDescription = "No user"
        )
        Text(text = "There is no connected user")
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
fun AddTimeButton(
    onClick: () -> Unit = {},
    modifier: GlanceModifier = GlanceModifier
) {
    SquareIconButton(
        imageProvider = ImageProvider(R.drawable.outline_more_time_24),
        contentDescription = "Add new time",
        onClick = onClick,
        modifier = modifier
    )
}