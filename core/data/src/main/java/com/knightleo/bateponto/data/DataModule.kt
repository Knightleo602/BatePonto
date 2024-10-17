package com.knightleo.bateponto.data

import com.knightleo.bateponto.data.repository.PreferencesRepositoryImpl
import com.knightleo.bateponto.domain.repository.PreferencesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<AppDatabase> { buildDatabase(androidContext()) }
    single<PreferencesRepository> { PreferencesRepositoryImpl(androidContext()) }
    factory<DayMarkDAO> {
        val db: AppDatabase = get()
        db.dayMarkDao()
    }
    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
}