package com.knightleo.bateponto.widget.data

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.knightleo.bateponto.domain.model.Time
import com.knightleo.bateponto.domain.repository.MarkerWidgetUpdater
import com.knightleo.bateponto.widget.ui.MarkerWidget
import com.knightleo.bateponto.widget.ui.dataStore

internal class MarkerWidgetUpdaterImpl(
    private val context: Context
) : MarkerWidgetUpdater {

    override suspend fun updateAll() {
        MarkerWidget().updateAll(context)
    }

    override suspend fun updateTimeMarks(list: List<Time>) {
        MarkerWidget().run {
            context.dataStore.updateDayMarks(list)
        }
    }
}