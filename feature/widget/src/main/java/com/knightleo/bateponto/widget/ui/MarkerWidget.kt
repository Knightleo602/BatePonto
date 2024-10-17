package com.knightleo.bateponto.widget.ui

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.GlanceId
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import com.knightleo.bateponto.data.entity.TimeMark
import com.knightleo.bateponto.widget.data.MarkerDataHelper.addTime
import com.knightleo.bateponto.widget.data.MarkerDataHelper.asString
import com.knightleo.bateponto.widget.data.MarkerDataHelper.getTodayTimes
import com.knightleo.bateponto.widget.data.MarkerDataHelper.toTimeMarkList
import com.knightleo.bateponto.widget.data.MarkerDataHelper.updateUser
import com.knightleo.bateponto.widget.data.State
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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
            it[dayMarksKey] = times.asString()
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
                    it[dayMarksKey].toTimeMarkList()
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
}