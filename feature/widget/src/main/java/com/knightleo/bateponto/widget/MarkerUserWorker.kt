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
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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
): CoroutineWorker(context, parameters), KoinComponent {
    private val dayMarkDao: DayMarkDAO by inject()
    private val userId = inputData.getInt("user_id", 0)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Napier.i(tag = "AddWorker") {
            "Adding new mark"
        }
        dayMarkDao.insertCurrentTimeStamp(userId)
        MarkerWidget().apply {
            applicationContext.dataStore.updateState(State.SUCCESS)
        }
        Napier.i(tag = "AddWorker") {
            "New mark added"
        }
        Result.success()
    }
}