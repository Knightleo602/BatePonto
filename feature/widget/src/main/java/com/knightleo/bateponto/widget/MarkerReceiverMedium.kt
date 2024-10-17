package com.knightleo.bateponto.widget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import com.knightleo.bateponto.data.repository.USER_ID_UPDATE_KEY
import com.knightleo.bateponto.widget.ui.MarkerWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

internal inline fun GlanceAppWidgetReceiver.receive(
    context: Context,
    intent: Intent,
    onReceive: () -> Unit
) {
    if(intent.getBooleanExtra(USER_ID_UPDATE_KEY, false)) runBlocking(Dispatchers.Unconfined) {
        glanceAppWidget.updateAll(context)
    } else onReceive()
}

class MarkerReceiverMedium : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: MarkerWidget = MarkerWidget()

    override fun onReceive(context: Context, intent: Intent) = receive(context, intent) {
        super.onReceive(context, intent)
    }
}