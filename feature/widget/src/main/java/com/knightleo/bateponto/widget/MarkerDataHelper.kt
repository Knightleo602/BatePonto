package com.knightleo.bateponto.widget

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

object MarkerDataHelper {

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
}