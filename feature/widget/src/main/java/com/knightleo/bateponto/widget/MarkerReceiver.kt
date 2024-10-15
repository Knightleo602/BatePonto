package com.knightleo.bateponto.widget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import com.knightleo.bateponto.data.repository.USER_ID_UPDATE_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class MarkerReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: MarkerWidget = MarkerWidget()

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.getBooleanExtra(USER_ID_UPDATE_KEY, false)) runBlocking(Dispatchers.Unconfined) {
            glanceAppWidget.updateAll(context)
        } else super.onReceive(context, intent)
    }
}