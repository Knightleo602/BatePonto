package com.knightleo.bateponto.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.knightleo.bateponto.R

@Composable
fun ActionButtons(
    modifier: Modifier = Modifier,
    showAddButton: Boolean = true,
    showEditButton: Boolean = false,
    showDeleteButton: Boolean = false,
    onAddClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val spacing = 19.dp
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing),
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = showEditButton,
            enter = slideInVertically { it + spacing.value.toInt() } + fadeIn(),
            exit = slideOutVertically { it + spacing.value.toInt() } + fadeOut()
        ) {
            FloatingActionButton(
                onClick = onEditClick,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            AnimatedVisibility(
                visible = showDeleteButton,
                enter = slideInHorizontally { it + spacing.value.toInt() } + fadeIn(),
                exit = slideOutHorizontally { it + spacing.value.toInt() } + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = onDeleteClick,
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_delete_24),
                        contentDescription = null
                    )
                }
            }
            AnimatedVisibility(
                visible = showAddButton
            ) {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_add_24),
                        contentDescription = null
                    )
                }
            }
        }
    }
}