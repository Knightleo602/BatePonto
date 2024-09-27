package com.knightleo.bateponto.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.knightleo.bateponto.data.entity.Day
import java.time.OffsetTime

@Composable
internal fun DaysList(
    userName: String,
    week: Week,
    dayMarks: DaysAndTimesWorked,
    selectedDay: Day?,
    selectedTime: OffsetTime?,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 70.dp,
    lazyListState: LazyListState = rememberLazyListState(),
    onSelectDay: (Day) -> Unit = {},
    onSelectTime: (Day, OffsetTime) -> Unit = { _, _ -> },
    onSelectWeek: (Week) -> Unit = {},
    onTimeLongClick: (Day, OffsetTime) -> Unit = { _, _ -> },
    onDayLongClick: (Day) -> Unit = {}
) {
    val horizontalPadding = 25.dp
    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        item {
            WeekHeaderItem(
                week = week,
                onSelectWeek = onSelectWeek,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
                    .padding(bottom = 5.dp)
            )
        }
        items(
            dayMarks,
            key = { it.first.first }
        ) { dayTimeMark ->
            val (day, _) = dayTimeMark
            DayItem(
                item = dayTimeMark,
                onSelect = onSelectDay,
                selected = selectedDay == day.first,
                onSelectTime = onSelectTime,
                selectedTimes = selectedTime?.let { listOf(it) } ?: emptyList(),
                onTimeLongClick = onTimeLongClick,
                onLongClick = onDayLongClick,
                modifier = Modifier
                    .padding(horizontal = horizontalPadding)
                    .fillMaxWidth()
            )
        }
        item { Spacer(modifier = Modifier.height(bottomPadding)) }
    }
}