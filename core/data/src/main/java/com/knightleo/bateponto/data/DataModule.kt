package com.knightleo.bateponto.data

import com.knightleo.bateponto.data.repository.PreferencesRepository
import com.knightleo.bateponto.data.repository.PreferencesRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<AppDatabase> { buildDatabase(androidContext()) }
    single<PreferencesRepository> { PreferencesRepositoryImpl(androidContext()) }
    factory<DayMarkDAO> {
        val db: AppDatabase = get()
        db.dayMarkDao()
    }
}