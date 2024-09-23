package com.knightleo.bateponto.ui.screens.daylist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.knightleo.bateponto.R
import com.knightleo.bateponto.data.entity.Day
import com.knightleo.bateponto.ui.condition
import com.knightleo.bateponto.ui.formatted
import com.knightleo.bateponto.ui.formattedTime
import java.time.OffsetTime

@Composable
fun DayItem(
    item: DayWorked,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    selectedTimes: List<OffsetTime>,
    onSelect: (Day) -> Unit,
    onLongClick: (Day) -> Unit,
    onSelectTime: (Day, OffsetTime) -> Unit,
    onTimeLongClick: (Day, OffsetTime) -> Unit
) {
    val (day, times) = item
    Column(
        modifier = Modifier
            .then(modifier)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(16.dp)
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DayTitle(
            day = day,
            selected = selected,
            onSelect = onSelect,
            onLongClick = onLongClick
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .padding(bottom = 10.dp),
            color = MaterialTheme.colorScheme.outline
        )
        times.forEach { timeItem ->
            TimeWorked(
                time = timeItem,
                selected = selectedTimes.contains(timeItem),
                onSelect = { onSelectTime(day.first, it) },
                onLongClick = { onTimeLongClick(day.first, it) }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DayTitle(
    day: DayAndDurationWorked,
    selected: Boolean,
    onSelect: (Day) -> Unit,
    onLongClick: (Day) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = {
                    onSelect(day.first)
                },
                onLongClick = {
                    onLongClick(day.first)
                }
            )
            .condition(selected) {
                it.background(MaterialTheme.colorScheme.surfaceContainerHighest)
            }
            .padding(vertical = 10.dp)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.first.formatted,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.time_worked, day.second.formatted()),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimeWorked(
    time: OffsetTime,
    modifier: Modifier = Modifier,
    selected: Boolean,
    onSelect: (OffsetTime) -> Unit,
    onLongClick: (OffsetTime) -> Unit
) {
    Text(
        text = time.formattedTime(),
        style = MaterialTheme.typography.labelMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .condition(selected) {
                it.background(MaterialTheme.colorScheme.surfaceContainerHighest)
            }
            .combinedClickable(
                onClick = { onSelect(time) },
                onLongClick = { onLongClick(time) },
            )
            .padding(horizontal = 20.dp)
            .padding(8.dp)
            .then(modifier)
    )
}