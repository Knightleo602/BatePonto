package com.knightleo.bateponto.data

import com.knightleo.bateponto.data.repository.PreferencesRepository
import com.knightleo.bateponto.data.repository.PreferencesRepositoryImpl
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.knightleo.bateponto.data.repositories.PreferencesRepository
import com.knightleo.bateponto.data.repositories.PreferencesRepositoryImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val Application.sharedPreferences
    get() = getSharedPreferences("prefs", Context.MODE_PRIVATE)

val dataModule = module {
    single<AppDatabase> { buildDatabase(androidContext()) }
    single<PreferencesRepository> { PreferencesRepositoryImpl(androidContext()) }
    factory<DayMarkDAO> {
        val db: AppDatabase = get()
        db.dayMarkDao()
    }
    single<SharedPreferences> { androidApplication().sharedPreferences }
    single<SharedPreferences.Editor> { androidApplication().sharedPreferences.edit() }
    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }
}