package com.knightleo.bateponto.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.SquareIconButton
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.knightleo.bateponto.data.entity.TimeMark
import com.knightleo.bateponto.data.entity.TimeMark.Companion.asTimeMark
import com.knightleo.bateponto.domain.formatted
import com.knightleo.bateponto.domain.formattedTime
import com.knightleo.bateponto.domain.timeSpent
import com.knightleo.bateponto.widget.MarkerDataHelper.addTime
import com.knightleo.bateponto.widget.MarkerDataHelper.getTodayTimes
import com.knightleo.bateponto.widget.MarkerDataHelper.updateUser
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Duration

val Context.dataStore by preferencesDataStore(name = "marker_widget_datastore")

class MarkerWidget : GlanceAppWidget() {
    private val dayMarksKey = stringPreferencesKey("day_marks")
    private val stateKey = intPreferencesKey("state")
    private val userIdKey = intPreferencesKey("user_id")

    companion object {
        private val SMALL_BTN = DpSize(80.dp, 80.dp)
        private val MEDIUM_TIMES = DpSize(130.dp, 100.dp)
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(
            SMALL_BTN,
            MEDIUM_TIMES
        )
    )

    private data class CurrentState(
        val state: State?,
        val userId: Int?,
        val times: List<TimeMark>? = null
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

    suspend fun DataStore<Preferences>.updateDayMarks(times: List<TimeMark>?) {
        edit {
            it[dayMarksKey] = StringBuilder().apply {
                times?.forEach { time ->
                    append(time.toString())
                    append("|")
                }
            }.toString()
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
                    it[stateKey]?.let { stateKey -> State.entries[stateKey] },
                    it[userIdKey],
                    it[dayMarksKey]?.let { string ->
                        if (string.isEmpty()) return@let null
                        val m = mutableListOf<TimeMark>()
                        string.split("|").forEach { s ->
                            m.add(s.asTimeMark())
                        }
                        m
                    } ?: emptyList()
                )
            }
            .stateIn(this)
        if (currentState.value.userId == null) updateUser(context)
        provideContent {
            val state by currentState.collectAsState()
            val currentSize = LocalSize.current
            val addTime = { addTime(context, state.userId ?: 0) }
            val updateUser = { updateUser(context) }
            WidgetTheme {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    if (currentSize.width > SMALL_BTN.width && currentSize.height > SMALL_BTN.height) {
                        if (state.times == null) getTodayTimes(
                            context,
                            state.userId!!
                        )
                        MediumContent(
                            currentTimes = state.times.orEmpty(),
                            currentState = state.state ?: State.LOADING,
                            onAddTime = addTime,
                            updateUser = updateUser
                        )
                    } else {
                        SmallContent(
                            state.state ?: State.LOADING,
                            onAddTime = addTime,
                            updateUser = updateUser
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    private fun MediumContent(
        currentTimes: List<TimeMark>,
        currentState: State = State.LOADING,
        modifier: GlanceModifier = GlanceModifier,
        onAddTime: () -> Unit = {},
        updateUser: () -> Unit = {}
    ) {
        val context = LocalContext.current
        val padding = 13.dp
        val itemPadding = 6.dp
        val textStyle = TextStyle(color = GlanceTheme.colors.onBackground)
        Column(
            modifier = GlanceModifier
                .background(GlanceTheme.colors.widgetBackground)
                .then(modifier)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(GlanceTheme.colors.surface)
                    .padding(vertical = itemPadding)
            ) {
                Text(
                    text = context.resources.getString(
                        R.string.spent_today_format_1s,
                        currentTimes.timeSpent.formatted()
                    ),
                    style = TextStyle(color = GlanceTheme.colors.onSurface)
                )
            }
            when (currentState) {
                State.NO_USER -> NoUser(updateUser = updateUser)
                else -> {
                    Box {
                        LazyColumn(
                            modifier = GlanceModifier.fillMaxWidth().padding(start = padding)
                        ) {
                            for (it in currentTimes.indices step 2) {
                                item {
                                    Spacer(modifier = GlanceModifier.height(itemPadding))
                                }
                                items(currentTimes.size) {
                                    val timeMark = currentTimes[it]
                                    val next = currentTimes.getOrNull(it + 1)
                                    Column {
                                        Text(
                                            timeMark.timeStamp.formattedTime(),
                                            style = textStyle
                                        )
                                        if (next != null) Row {
                                            if (it % 2 == 0) {
                                                Image(
                                                    ImageProvider(R.drawable.rounded_arrow_right_24),
                                                    contentDescription = null,
                                                    colorFilter = ColorFilter.tint(
                                                        ColorProvider(
                                                            Color.Green
                                                        )
                                                    )
                                                )
                                            } else {
                                                Image(
                                                    ImageProvider(R.drawable.rounded_arrow_left_24),
                                                    contentDescription = null,
                                                    colorFilter = ColorFilter.tint(
                                                        ColorProvider(
                                                            Color.Red
                                                        )
                                                    )
                                                )
                                            }
                                            Text(
                                                text = Duration.between(
                                                    timeMark.timeStamp,
                                                    next.timeStamp
                                                ).formatted(),
                                                style = textStyle
                                            )
                                        }
                                    }
                                }
                                item {
                                    Spacer(modifier = GlanceModifier.height(itemPadding))
                                }
                            }
                        }
                        Box(
                            modifier = GlanceModifier.fillMaxSize()
                                .padding(bottom = padding, end = padding + 3.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            SquareIconButton(
                                imageProvider = ImageProvider(R.drawable.outline_more_time_24),
                                contentDescription = context.getString(R.string.add_new_time_description),
                                onClick = onAddTime
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview
    @Composable
    private fun SmallContent(
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
    private fun NoUser(
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
    private fun AddTimeButton(
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