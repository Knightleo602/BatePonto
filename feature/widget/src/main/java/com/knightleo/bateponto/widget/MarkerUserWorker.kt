package com.knightleo.bateponto.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.updateAll
import androidx.glance.unit.ColorProvider
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.knightleo.bateponto.data.DayMarkDAO
import com.knightleo.bateponto.data.repository.PreferencesRepository
import com.knightleo.bateponto.data.today
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal const val USER_ID_KEY = "user_id"

enum class State {
    LOADING {
        override val backgroundColor: ColorProvider
            @Composable get() = GlanceTheme.colors.widgetBackground
    },
    SUCCESS {
        override val backgroundColor: ColorProvider
            @Composable get() = GlanceTheme.colors.tertiaryContainer
    },
    OK {
        override val backgroundColor: ColorProvider
            @Composable get() = GlanceTheme.colors.widgetBackground
    },
    NO_USER {
        override val backgroundColor: ColorProvider
            @Composable get() = GlanceTheme.colors.error
    };

    abstract val backgroundColor: ColorProvider @Composable get
}

class MarkerUserWorker(
    context: Context,
    parameters: WorkerParameters,
) : CoroutineWorker(context, parameters), KoinComponent {
    private val preferences: PreferencesRepository by inject()
    override suspend fun doWork(): Result {
        Napier.i(tag = "UserWorker") {
            "Fetching active user"
        }
        val userId = preferences.activeUserid.first()
        Napier.i(tag = "UserWorker") {
            "User fetched: $userId"
        }
        return MarkerWidget().run {
            if (userId != null) {
                applicationContext.dataStore.updateState(State.OK)
                applicationContext.dataStore.setUserId(userId)
                updateAll(applicationContext)
                Result.success()
            } else {
                applicationContext.dataStore.updateState(State.NO_USER)
                updateAll(applicationContext)
                Result.failure()
            }
        }
    }
}

class MarkerAddWorker(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters), KoinComponent {
    private val dayMarkDao: DayMarkDAO by inject()
    private val userId = inputData.getInt(USER_ID_KEY, 0)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Napier.i(tag = "AddWorker") {
            "Adding new mark"
        }
        dayMarkDao.insertCurrentTimeStamp(userId)
        val times = dayMarkDao.getWorkTimesInDay(userId, today())
        MarkerWidget().run {
            applicationContext.dataStore.updateState(State.SUCCESS)
            applicationContext.dataStore.updateDayMarks(times)
            updateAll(applicationContext)
        }
        Napier.i(tag = "AddWorker") {
            "New mark added"
        }
        Result.success()
    }
}

class MarkerFetchTimesWorker(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters), KoinComponent {
    private val dayMarkDao: DayMarkDAO by inject()
    private val userId: Int = inputData.getInt(USER_ID_KEY, 0)
    override suspend fun doWork(): Result {
        Napier.i {
            "Fetching marks"
        }
        val times = dayMarkDao.getWorkTimesInDay(userId, today())
        Napier.i {
            "Marks fetched: $times"
        }
        MarkerWidget().run {
            applicationContext.dataStore.updateDayMarks(times)
            updateAll(applicationContext)
        }
        return Result.success()
    }
}