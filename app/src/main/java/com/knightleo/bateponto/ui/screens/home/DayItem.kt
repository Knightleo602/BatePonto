package com.knightleo.bateponto.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.constraintlayout.compose.ConstraintLayout
import com.knightleo.bateponto.R
import com.knightleo.bateponto.data.entity.Day
import com.knightleo.bateponto.domain.condition
import com.knightleo.bateponto.domain.formatted
import com.knightleo.bateponto.domain.formattedTime
import java.time.Duration
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
    val itemSpacing = 4.dp
    val localDensity = LocalDensity.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
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
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
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
                .padding(bottom = 5.dp),
            color = MaterialTheme.colorScheme.outline
        )
        (times.indices step 2).forEach { timeIndex ->
            val time = times[timeIndex]
            val nextTime = times.getOrNull(timeIndex + 1)
            ConstraintLayout(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
            ) {
                val (timeRef, dividerRef) = createRefs()
                var textHeight by remember { mutableStateOf(0.dp) }
                val defPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                Column(
                    verticalArrangement = Arrangement.spacedBy(itemSpacing),
                    modifier = Modifier.constrainAs(timeRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimeWorked(
                        time = time,
                        selected = selectedTimes.contains(time),
                        onSelect = { onSelectTime(day.first, time) },
                        onLongClick = { onTimeLongClick(day.first, time) },
                        modifier = Modifier
                            .onPlaced {
                                with(localDensity) {
                                    textHeight = it.size.height.toDp()
                                }
                            }
                            .padding(defPadding)
                    )
                    if (nextTime != null) {
                        TimeWorked(
                            time = nextTime,
                            selected = selectedTimes.contains(nextTime),
                            onSelect = { onSelectTime(day.first, nextTime) },
                            onLongClick = { onTimeLongClick(day.first, nextTime) },
                            modifier = Modifier.padding(defPadding)
                        )
                    }
                }
                if (nextTime != null) {
                    val duration = Duration.between(time, nextTime)
                    Row(
                        modifier = Modifier
                            .constrainAs(dividerRef) {
                                start.linkTo(timeRef.end)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .padding(start = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TimeConnector(
                            modifier = Modifier
                                .padding(vertical = max((textHeight / 2) - 1.dp, 0.dp))
                        )
                        Text(
                            text = duration.formatted(),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun TimeConnector(
    modifier: Modifier = Modifier,
    width: Dp = 1.dp,
    connectionWidth: Dp = 7.dp
) {
    val cw = min(5.dp, connectionWidth)
    val color = LocalContentColor.current
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .then(modifier),
        horizontalAlignment = Alignment.End
    ) {
        Box(
            modifier = Modifier
                .size(height = width, width = cw)
                .background(color)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .width(width)
                .background(color)
        )
        Box(
            modifier = Modifier
                .size(height = width, width = cw)
                .background(color)
        )
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
            text = stringResource(R.string.time_worked, day.second.abs().formatted()),
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
    onSelect: () -> Unit,
    onLongClick: () -> Unit
) {
    Text(
        text = time.formattedTime(),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .condition(selected) {
                it.background(MaterialTheme.colorScheme.surfaceContainerHighest)
            }
            .combinedClickable(
                onClick = onSelect,
                onLongClick = onLongClick,
            )
            .then(modifier)
    )
}

@Preview
@Composable
private fun DayItemPreview() {
    var selected by remember { mutableStateOf(false) }
    val timesList = listOf(
        OffsetTime.now(),
        OffsetTime.now(),
        OffsetTime.now(),
        OffsetTime.now(),
    )
    DayItem(
        item = Day.now() to Duration.ofMinutes(120) to timesList,
        onLongClick = {},
        onSelect = { selected = !selected },
        onSelectTime = { _, _ -> },
        onTimeLongClick = { _, _ -> },
        selected = selected,
        selectedTimes = emptyList()
    )
}

@Preview
@Composable
private fun DayItemPreview2() {
    var selected by remember { mutableStateOf(false) }
    val timesList = listOf(
        OffsetTime.now(),
        OffsetTime.now(),
        OffsetTime.now(),
    )
    DayItem(
        item = Day.now() to Duration.ofMinutes(120) to timesList,
        onLongClick = {},
        onSelect = { selected = !selected },
        onSelectTime = { _, _ -> },
        onTimeLongClick = { _, _ -> },
        selected = selected,
        selectedTimes = emptyList()
    )
}

@Preview
@Composable
private fun DayTitlePreview() {
    DayTitle(
        day = Day.now() to Duration.ofMinutes(120),
        selected = true,
        onSelect = {},
        onLongClick = {}
    )
}

@Composable
@Preview
private fun TimeWorkedPreview() {
    TimeWorked(
        time = OffsetTime.now(),
        selected = true,
        onSelect = {},
        onLongClick = {}
    )
}