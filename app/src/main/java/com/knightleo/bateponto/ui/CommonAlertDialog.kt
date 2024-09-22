package com.knightleo.bateponto.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

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
                Text(text = "Cancelar")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Confirmar")
            }
        },
        onDismissRequest = onCancel,
        text = content,
        title = title
    )
}