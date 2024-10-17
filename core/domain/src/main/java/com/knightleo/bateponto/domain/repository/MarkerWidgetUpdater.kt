package com.knightleo.bateponto.domain.repository

import com.knightleo.bateponto.domain.model.Time

interface MarkerWidgetUpdater {
    suspend fun updateAll()
    suspend fun updateTimeMarks(list: List<Time>)
}