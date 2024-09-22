package com.knightleo.bateponto.ui.screens.daylist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import com.knightleo.bateponto.ui.CommonAlertDialog
import java.time.OffsetTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTimeDialog(
    initialTime: OffsetTime,
    show: Boolean,
    onDismiss: () -> Unit,
    onSelect: (Int, Int) -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute
    )
    CommonAlertDialog(
        show = show,
        onCancel = onDismiss,
        onConfirm = {
            onSelect(timePickerState.hour, timePickerState.minute)
        },
        title = {
            Text(text = "Atualizar hora")
        }
    ) {
        TimePicker(
            state = timePickerState
        )
    }
}