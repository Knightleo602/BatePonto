package com.knightleo.bateponto.widget.data

import com.knightleo.bateponto.domain.repository.MarkerWidgetUpdater
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val widgetModule = module {
    worker<MarkerUserWorker> { MarkerUserWorker(androidContext(), get()) }
    worker<MarkerAddWorker> { MarkerAddWorker(androidContext(), get()) }
    single<MarkerWidgetUpdater> { MarkerWidgetUpdaterImpl(get()) }
}