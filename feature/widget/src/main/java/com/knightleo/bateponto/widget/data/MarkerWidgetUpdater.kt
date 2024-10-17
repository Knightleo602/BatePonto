package com.knightleo.bateponto.widget.data

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.knightleo.bateponto.data.entity.TimeMark
import com.knightleo.bateponto.widget.ui.MarkerWidget
import com.knightleo.bateponto.widget.ui.dataStore

interface MarkerWidgetUpdater {
    suspend fun updateAll()
    suspend fun updateTimeMarks(list: List<TimeMark>)
}

internal class MarkerWidgetUpdaterImpl(
    private val context: Context
) : MarkerWidgetUpdater {

    override suspend fun updateAll() {
        MarkerWidget().updateAll(context)
    }

    override suspend fun updateTimeMarks(list: List<TimeMark>) {
        MarkerWidget().run {
            context.dataStore.updateDayMarks(list)
        }
    }
}