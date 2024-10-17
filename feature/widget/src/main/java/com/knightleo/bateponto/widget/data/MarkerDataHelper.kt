package com.knightleo.bateponto.widget.data

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.knightleo.bateponto.domain.model.Time
import com.knightleo.bateponto.domain.model.Time.Companion.asTime
import io.github.aakira.napier.Napier

object MarkerDataHelper {

    private const val TIME_SPLITTER = "|||"

    fun addTime(context: Context, currentUserId: Int) {
        val worker = OneTimeWorkRequestBuilder<MarkerAddWorker>()
            .setInputData(workDataOf(USER_ID_KEY to currentUserId))
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "add_time",
                ExistingWorkPolicy.REPLACE,
                worker
            )
    }

    fun updateUser(context: Context) {
        WorkManager.getInstance(context).enqueueUniqueWork(
            "fetch_user",
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<MarkerUserWorker>().build()
        )
    }

    fun getTodayTimes(context: Context, currentUserId: Int) {
        val worker = OneTimeWorkRequestBuilder<MarkerFetchTimesWorker>()
            .setInputData(workDataOf(USER_ID_KEY to currentUserId))
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "fetch_today_times",
            ExistingWorkPolicy.REPLACE,
            worker
        )
    }

    internal fun List<Time>?.asString(): String = if (isNullOrEmpty()) ""
    else StringBuilder().apply {
        this@asString.forEachIndexed { i, time ->
            append(time.toString())
            if (i < this@asString.lastIndex) append(TIME_SPLITTER)
        }
    }.toString()

    internal fun String?.toTimeMarkList(): List<Time> = if (isNullOrBlank()) emptyList()
    else {
        Napier.d(tag = "Widget") { "Reading string: $this" }
        split(TIME_SPLITTER).map { s -> s.asTime() }
    }
}