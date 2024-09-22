package com.knightleo.bateponto.ui.screens.daylist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.knightleo.bateponto.data.entity.Day
import java.time.OffsetTime

@Composable
internal fun DaysList(
    dayMarks: DaysAndTimesWorked,
    selectedDay: Day?,
    selectedTime: OffsetTime?,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onSelectDay: (Day) -> Unit,
    onSelectTime: (Day, OffsetTime) -> Unit,
    onTimeLongClick: (Day, OffsetTime) -> Unit = { _, _ -> },
    onDayLongClick: (Day) -> Unit = {}
) {
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(dayMarks) { dayTimeMark ->
            val (day, _) = dayTimeMark
            DayItem(
                item = dayTimeMark,
                onSelect = onSelectDay,
                selected = selectedDay == day.first,
                onSelectTime = onSelectTime,
                selectedTimes = selectedTime?.let { listOf(it) } ?: emptyList(),
                onTimeLongClick = onTimeLongClick,
                onLongClick = onDayLongClick
            )
        }
        item { Spacer(modifier = Modifier.height(70.dp)) }
    }
}