package com.knightleo.bateponto.widget

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MarkerReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MarkerWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        startKoin {
            androidContext(context)
            modules(module)
        }
    }
}