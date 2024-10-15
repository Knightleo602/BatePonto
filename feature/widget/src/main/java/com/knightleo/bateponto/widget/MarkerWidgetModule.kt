package com.knightleo.bateponto.widget

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val widgetModule = module {
    worker<MarkerUserWorker> { MarkerUserWorker(androidContext(), get()) }
    worker<MarkerAddWorker> { MarkerAddWorker(androidContext(), get()) }
}