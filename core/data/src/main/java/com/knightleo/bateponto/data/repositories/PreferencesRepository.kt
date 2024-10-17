package com.knightleo.bateponto.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit

interface PreferencesRepository {
    var currentSelectedJobId: Int?
}

class PreferencesRepositoryImpl(
    private val preferences: SharedPreferences
) : PreferencesRepository {
    override var currentSelectedJobId: Int?
        get() = preferences.getInt(CURRENT_SELECTED_JOB_ID, -1).let { if (it == -1) null else it }
        set(value) {
            preferences.edit {
                value?.let { putInt(CURRENT_SELECTED_JOB_ID, it) }
                    ?: remove(CURRENT_SELECTED_JOB_ID)
            }
        }

    private companion object {
        private const val CURRENT_SELECTED_JOB_ID = "CURRENT_SELECTED_JOB_ID"
    }
}