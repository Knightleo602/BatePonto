package com.knightleo.bateponto.widget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.knightleo.bateponto.widget.ui.MarkerWidget

class MarkerReceiverSmall : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: MarkerWidget = MarkerWidget()

    override fun onReceive(context: Context, intent: Intent) = receive(context, intent) {
        super.onReceive(context, intent)
    }
}