package com.knightleo.bateponto

import android.app.Application
import com.knightleo.bateponto.data.dataModule
import com.knightleo.bateponto.ui.screens.screensModule
import com.knightleo.bateponto.widget.widgetModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

class MainApplication : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(dataModule, screensModule, widgetModule)
            workManagerFactory()
        }
        Napier.base(DebugAntilog("BatePonto"))
    }
}