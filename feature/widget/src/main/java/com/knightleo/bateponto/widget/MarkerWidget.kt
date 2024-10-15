package com.knightleo.bateponto.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.SquareIconButton
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

val Context.dataStore by preferencesDataStore(name = "marker_widget_datastore")

class MarkerWidget : GlanceAppWidget() {
    private val stateKey = intPreferencesKey("state")
    private val userIdKey = intPreferencesKey("user_id")

    private data class CurrentState(
        val state: State?,
        val userId: Int?
    )

    suspend fun DataStore<Preferences>.setUserId(id: Int) {
        edit {
            it[userIdKey] = id
        }
    }
    suspend fun DataStore<Preferences>.updateState(state: State) {
        edit {
            it[stateKey] = state.ordinal
        }
    }

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) = coroutineScope {
        val store = context.dataStore
        val currentState = store.data
            .map {
                CurrentState(
                    it[stateKey]?.let { State.entries[it] },
                    it[userIdKey]
                )
            }
            .stateIn(this)
        if (currentState.value.userId == null) {
            val worker = OneTimeWorkRequestBuilder<MarkerUserWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "fetch_user",
                ExistingWorkPolicy.REPLACE,
                worker
            )
        }
        provideContent {
            val state by currentState.collectAsState()
            WidgetTheme {
                MainContent(state.state ?: State.LOADING, state.userId ?: 0, context)
            }
        }
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    fun MainContent(
        currentState: State,
        currentUser: Int,
        context: Context,
        modifier: GlanceModifier = GlanceModifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier
                .background(currentState.backgroundColor)
                .then(modifier)
        ) {
            when(currentState) {
                State.NO_USER -> NoUser()
                else -> AddTimeButton(
                    onClick = { addTime(context, currentUser) }
                )
            }
        }
    }

    private fun addTime(context: Context, currentUserId: Int) {
        val worker = OneTimeWorkRequestBuilder<MarkerAddWorker>()
            .setInputData(workDataOf("user_id" to currentUserId))
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "add_time",
                ExistingWorkPolicy.REPLACE,
                worker
            )
    }


    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    fun NoUser(modifier: GlanceModifier = GlanceModifier) {
        val context = LocalContext.current
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.clickable {
                val worker = OneTimeWorkRequestBuilder<MarkerUserWorker>().build()
                WorkManager.getInstance(context).enqueueUniqueWork(
                    "fetch_user",
                    ExistingWorkPolicy.REPLACE,
                    worker
                )
            }
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
    fun AddTimeButton(
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
}