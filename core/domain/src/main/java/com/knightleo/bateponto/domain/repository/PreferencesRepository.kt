package com.knightleo.bateponto.domain.repository

import kotlinx.coroutines.flow.Flow

const val USER_ID_UPDATE_KEY: String = "active_user_id"
const val JOB_ID_UPDATE_KEY: String = "active_job_id"

interface PreferencesRepository {
    val activeUserId: Flow<Int?>
    val activeJobId: Flow<Int?>

    suspend fun setActiveUserId(id: Int)
    suspend fun setActiveJobId(id: Int)
}