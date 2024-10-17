package com.knightleo.bateponto.ui

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import com.knightleo.bateponto.data.entity.Day
import com.knightleo.bateponto.data.entity.Day.Companion.asDay
import java.time.Instant
import java.time.OffsetTime

@OptIn(ExperimentalMaterial3Api::class)
val DatePickerState.selectedDay: Day
    get() {
        if (selectedDateMillis == null) return Day()
        val offsetDateTime = Instant
            .ofEpochMilli(selectedDateMillis!!).atOffset(OffsetTime.now().offset)
        return offsetDateTime.plusDays(1L).asDay()
    }