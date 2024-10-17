package com.knightleo.bateponto.data.repository

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.knightleo.bateponto.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("bateponto_preferences")

class PreferencesRepositoryImpl(
    private val context: Context
) : PreferencesRepository {

    private val activeUserIdKey = intPreferencesKey(USER_ID_UPDATE_KEY)
    private val activeJobIdKey = intPreferencesKey(JOB_ID_UPDATE_KEY)

    override val activeUserId: Flow<Int?>
        get() = context.dataStore.data.map { it[activeUserIdKey] }

    override val activeJobId: Flow<Int?>
        get() = context.dataStore.data.map { it[activeJobIdKey] }

    override suspend fun setActiveUserId(id: Int) {
        var oldId: Int? = null
        context.dataStore.edit {
            oldId = it[activeUserIdKey]
            it[activeUserIdKey] = id
        }
        val intent = Intent().apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(USER_ID_UPDATE_KEY, oldId != id)
            setPackage(context.packageName)
        }
        context.sendBroadcast(intent)
    }

    override suspend fun setActiveJobId(id: Int) {
        var oldId: Int? = null
        context.dataStore.edit {
            oldId = it[activeJobIdKey]
            it[activeJobIdKey] = id
        }
        val intent = Intent().apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(JOB_ID_UPDATE_KEY, oldId != id)
            setPackage(context.packageName)
        }
        context.sendBroadcast(intent)
    }
}