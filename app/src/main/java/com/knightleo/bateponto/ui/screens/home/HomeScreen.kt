package com.knightleo.bateponto.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.knightleo.bateponto.R
import com.knightleo.bateponto.data.currentWeekRange
import com.knightleo.bateponto.data.entity.Day
import com.knightleo.bateponto.domain.formatted
import com.knightleo.bateponto.domain.hourAndMinuteToOffsetTime
import com.knightleo.bateponto.ui.ActionButtons
import com.knightleo.bateponto.ui.AppTopBar
import com.knightleo.bateponto.ui.CommonAlertDialog
import org.koin.androidx.compose.koinViewModel
import java.time.OffsetTime

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val localDensity = LocalDensity.current
    val state by viewModel.markState.collectAsStateWithLifecycle()
    var showDeleteDayDialog by remember { mutableStateOf(false) }
    var showUpdateTimeDialog by remember { mutableStateOf(false) }
    var selectedDate: Day? by remember { mutableStateOf(null) }
    var selectedTime: OffsetTime? by remember { mutableStateOf(null) }
    var fabButtonsSize by remember { mutableStateOf(70.dp) }
    val lifecycle = rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(lifecycle.value) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.refresh()
                }

                Lifecycle.Event.ON_STOP -> {
                    viewModel.updateWidget()
                }

                else -> {}
            }
        }
        val l = lifecycle.value.lifecycle
        l.addObserver(observer)
        onDispose {
            l.removeObserver(observer)
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                selectedTime = null
                selectedDate = null
            }
            .then(modifier),
        floatingActionButton = {
            val showAddButton = state.selectedWeek == currentWeekRange()
            ActionButtons(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .onSizeChanged {
                        with(localDensity) {
                            fabButtonsSize = it.height.toDp() + 20.dp
                        }
                    },
                onAddClick = viewModel::addNewMark,
                showAddButton = showAddButton,
                showEditButton = selectedTime != null && showAddButton,
                showDeleteButton = selectedDate != null && showAddButton,
                onEditClick = {
                    showUpdateTimeDialog = true
                },
                onDeleteClick = {
                    if (selectedTime == null) showDeleteDayDialog = true
                    else {
                        viewModel.delete(selectedDate, selectedTime)
                        selectedTime = null
                        selectedDate = null
                    }
                }
            )
        },
        topBar = {
            AppTopBar()
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        if (state.jobs.isEmpty()) {
            // TODO show create job onboarding
        } else if (state.selectedJob == null) {
            // TODO show select job screen
        } else {
            DaysList(
                week = state.selectedWeek,
                userName = "viewModel.user.name",
                modifier = Modifier.padding(innerPadding),
                dayMarks = state.marks,
                onSelectTime = { day, time ->
                    selectedTime = time
                    selectedDate = day
                },
                onSelectDay = {
                    selectedDate = it
                    selectedTime = null
                },
                onSelectWeek = {
                    viewModel.changeWeek(it)
                    selectedDate = null
                    selectedTime = null
                },
                bottomPadding = fabButtonsSize,
                selectedTime = selectedTime,
                selectedDay = selectedDate,
                onDayLongClick = {
                    selectedDate = it
                    showDeleteDayDialog = true
                }
            )
        }
    }
    CommonAlertDialog(
        show = showDeleteDayDialog,
        title = stringResource(R.string.delete_day_prompt_title),
        text = stringResource(
            R.string.delete_day_prompt_description,
            selectedDate?.formatted.orEmpty()
        ),
        onConfirm = {
            viewModel.delete(selectedDate)
            selectedDate = null
            selectedTime = null
            showDeleteDayDialog = false
        },
        onCancel = {
            selectedDate = null
            showDeleteDayDialog = false
        }
    )
    if (selectedTime != null) UpdateTimeDialog(
        initialTime = selectedTime!!,
        show = showUpdateTimeDialog,
        onDismiss = {
            showUpdateTimeDialog = false
        },
        onSelect = { hour, minute ->
            viewModel.updateTime(
                selectedTime!!,
                hourAndMinuteToOffsetTime(hour, minute),
                selectedDate!!
            )
            selectedTime = null
            showUpdateTimeDialog = false
        }
    )
}