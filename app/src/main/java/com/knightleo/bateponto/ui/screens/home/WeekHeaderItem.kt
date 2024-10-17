package com.knightleo.bateponto.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.knightleo.bateponto.R
import com.knightleo.bateponto.domain.utils.weekRange
import com.knightleo.bateponto.domain.selectedDay
import com.knightleo.bateponto.domain.utils.formatted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekHeaderItem(
    week: Week,
    modifier: Modifier = Modifier,
    onSelectWeek: (Week) -> Unit = {},
) {
    var showDatePicker by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedTextField(
            value = stringResource(
                R.string.week_range_format,
                week.first.formatted,
                week.second.formatted
            ),
            readOnly = true,
            onValueChange = {},
            trailingIcon = {
                IconButton(
                    onClick = { showDatePicker = !showDatePicker }
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.select_date)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .focusProperties { canFocus = false },
            singleLine = true,
            label = { Text(text = stringResource(R.string.week)) },
        )

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                confirmButton = {
                    TextButton(
                        onClick = {
                            onSelectWeek(datePickerState.selectedDay.weekRange())
                            showDatePicker = false
                        },
                        enabled = datePickerState.selectedDateMillis != null
                    ) {
                        Text(text = stringResource(R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDatePicker = false }
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                },
                onDismissRequest = { showDatePicker = false },
                modifier = Modifier.padding(horizontal = 30.dp)
            ) {
                DatePicker(datePickerState)
            }
        }
    }
}
