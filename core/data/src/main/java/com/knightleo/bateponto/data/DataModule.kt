package com.knightleo.bateponto.data

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<AppDatabase> { buildDatabase(androidContext()) }
    factory<DayMarkDAO> {
        val db: AppDatabase = get()
        db.dayMarkDao()
    }
}