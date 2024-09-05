package com.knightleo.bateponto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.knightleo.bateponto.ui.theme.BatePontoTheme
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val timeMarker = TimeMarker(File(filesDir, FILE_NAME))
        enableEdgeToEdge()
        setContent {
            val coroutine = rememberCoroutineScope()
            var breakPoints: BreakPoints by remember { mutableStateOf(timeMarker.readPreviousEntries()) }
            val lazyListState = rememberLazyListState()
            BatePontoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    timeMarker.saveCurrentTime()
                                    breakPoints = timeMarker.readPreviousEntries()
                                    coroutine.launch {
                                        lazyListState.animateScrollToItem(breakPoints.lastIndex)
                                    }
                                },
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_add_24),
                                    contentDescription = null
                                )
                            }
                            FloatingActionButton(
                                onClick = {
                                    timeMarker.reset()
                                    breakPoints = timeMarker.readPreviousEntries()
                                },
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_delete_24),
                                    contentDescription = null
                                )
                            }
                        }

                    },
                    floatingActionButtonPosition = FabPosition.Center
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        Greeting()
                        BreakPointsList(breakPoints, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Composable
fun DialogContent(
    showDialog: Boolean,
    breakPoints: BreakPoints,
    onDismiss: () -> Unit
) {
    if(showDialog) {
        Dialog(
            onDismissRequest = onDismiss
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
            ) {
                BreakPointsList(breakPoints)
            }
        }
    }
}

@Composable
fun BreakPointsList(
    breakPoints: BreakPoints,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier.padding(top = 10.dp).then(modifier),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(breakPoints) { dateItem ->
            Text(
                text = dateItem.first.formatted,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Column (
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                dateItem.second.forEach { timeItem ->
                    Text(
                        text = timeItem.formatted,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(70.dp)) }
    }
}

@Composable
fun Buttons(
    onMarkNewTime: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            onClick = onMarkNewTime,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Bater Ponto")
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Text(
        text = "Bate ponto",
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}