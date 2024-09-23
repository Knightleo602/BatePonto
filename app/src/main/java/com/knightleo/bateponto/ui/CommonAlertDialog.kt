package com.knightleo.bateponto.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.knightleo.bateponto.R

@Composable
fun CommonAlertDialog(
    show: Boolean = true,
    text: String = "",
    title: String = "",
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    CommonAlertDialog(
        show,
        content = { Text(text = text) },
        title = { Text(text = title) },
        onConfirm = onConfirm,
        onCancel = onCancel
    )
}

@Composable
fun CommonAlertDialog(
    show: Boolean = true,
    title: @Composable () -> Unit,
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    if (show) AlertDialog(
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        onDismissRequest = onCancel,
        text = content,
        title = title
    )
}