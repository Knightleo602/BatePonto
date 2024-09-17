package com.knightleo.bateponto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.knightleo.bateponto.data.DatabaseHolder
import com.knightleo.bateponto.data.entity.DailyMarks
import com.knightleo.bateponto.data.entity.Date
import com.knightleo.bateponto.data.entity.Time
import com.knightleo.bateponto.ui.MarkState
import com.knightleo.bateponto.ui.ViewModel
import com.knightleo.bateponto.ui.condition
import com.knightleo.bateponto.ui.theme.BatePontoTheme

class MainActivity : ComponentActivity() {

    private val db by DatabaseHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = ViewModel(db.dayMarkDao())
            val state by viewModel.markState.collectAsStateWithLifecycle()
            BatePontoTheme {
                Content(
                    state,
                    onAdd = viewModel::addNewMark,
                    onTimeDelete = viewModel::deleteTime,
                    onDayDelete = viewModel::deleteDay
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    state: MarkState,
    onAdd: () -> Unit,
    onDayDelete: (Date) -> Unit,
    onTimeDelete: (Time, Date) -> Unit
) {
    var selectedDate: Date? by remember { mutableStateOf(null) }
    var selectedTime: Time? by remember { mutableStateOf(null) }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                selectedTime = null
                selectedDate = null
            },
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = {
                        if(selectedTime == null && selectedDate == null) {
                            onAdd()
                        } else {
                            selectedDate = null
                            selectedTime = null
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_24),
                        contentDescription = null
                    )
                }
                if(selectedDate != null) {
                    FloatingActionButton(
                        onClick = {
                            if(selectedTime == null) onDayDelete(selectedDate!!)
                            else {
                                onTimeDelete(selectedTime!!, selectedDate!!)
                                selectedTime = null
                            }
                            selectedDate = null
                        },
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_delete_24),
                            contentDescription = null
                        )
                    }
                }
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(text = "") },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_settings_24),
                            contentDescription = stringResource(R.string.config)
                        )
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        MarksList(
            state.marks.mapIndexed { index, dayTimeMark -> dayTimeMark to state.sums[index] },
            selectedDate,
            selectedTime,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onSelectTime = { selectedTime = it },
            onSelectDay = { selectedDate = it },
        )
    }
}

@Composable
fun MarksList(
    dayMarks: List<Pair<DailyMarks, Time?>>,
    selectedDay: Date?,
    selectedTime: Time?,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onSelectDay: (Date) -> Unit,
    onSelectTime: (Time?) -> Unit
) {
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(dayMarks) { dayTimeMark ->
            Column (
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 15.dp)
                    .fillMaxWidth()
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 10.dp)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .clickable {
                            onSelectDay(dayTimeMark.first.date.date)
                            onSelectTime(null)
                        }
                        .condition(selectedDay == dayTimeMark.first.date.date) {
                            it.background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        }
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${dayTimeMark.first.date.date.formatted} - ${dayTimeMark.first.date.weekDayName}",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    if(dayTimeMark.second != null) {
                        Text(
                            text = stringResource(R.string.time_worked, dayTimeMark.second!!.formatted),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 10.dp),
                    color = MaterialTheme.colorScheme.outline
                )
                dayTimeMark.first.times.forEach { timeItem ->
                    Text(
                        text = timeItem.time.formatted,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(16.dp))
                            .condition(selectedTime == timeItem.time) {
                                it.background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            }
                            .clickable {
                                onSelectDay(timeItem.date)
                                onSelectTime(timeItem.time)
                            }
                            .padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        item { Spacer(modifier = Modifier.height(70.dp)) }
    }
}