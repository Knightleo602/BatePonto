package com.knightleo.bateponto.widget.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.components.SquareIconButton
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.knightleo.bateponto.data.entity.TimeMark
import com.knightleo.bateponto.domain.formatted
import com.knightleo.bateponto.domain.formattedTime
import com.knightleo.bateponto.domain.timeSpent
import com.knightleo.bateponto.widget.R
import com.knightleo.bateponto.widget.data.State
import io.github.aakira.napier.Napier
import java.time.Duration

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
internal fun MediumContent(
    currentTimes: List<TimeMark>,
    currentState: State = State.LOADING,
    modifier: GlanceModifier = GlanceModifier,
    onAddTime: () -> Unit = {},
    updateUser: () -> Unit = {}
) {
    val context = LocalContext.current
    val padding = 13.dp
    val itemPadding = 6.dp
    val textStyle = TextStyle(color = GlanceTheme.colors.onBackground)
    Column(
        modifier = GlanceModifier
            .background(GlanceTheme.colors.widgetBackground)
            .then(modifier)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(GlanceTheme.colors.surface)
                .padding(vertical = itemPadding)
        ) {
            Text(
                text = context.resources.getString(
                    R.string.spent_today_format_1s,
                    currentTimes.timeSpent.formatted()
                ),
                style = TextStyle(color = GlanceTheme.colors.onSurface)
            )
        }
        when (currentState) {
            State.NO_USER -> NoUser(updateUser = updateUser)
            else -> {
                Napier.i { "Showing ${currentTimes.size} items" }
                Box {
                    LazyColumn(
                        modifier = GlanceModifier.fillMaxWidth().padding(start = padding)
                    ) {
                        item {
                            Spacer(modifier = GlanceModifier.height(itemPadding))
                        }
                        itemsIndexed(currentTimes) { index, timeMark ->
                            val next = currentTimes.getOrNull(index + 1)
                            Column {
                                Text(
                                    timeMark.timeStamp.formattedTime(),
                                    style = textStyle
                                )
                                if (next != null) Row {
                                    if (index % 2 == 0) {
                                        Image(
                                            ImageProvider(R.drawable.rounded_arrow_right_24),
                                            contentDescription = null,
                                            colorFilter = ColorFilter.tint(
                                                ColorProvider(
                                                    Color(context.getColor(R.color.arrow_green))
                                                )
                                            )
                                        )
                                    } else {
                                        Image(
                                            ImageProvider(R.drawable.rounded_arrow_left_24),
                                            contentDescription = null,
                                            colorFilter = ColorFilter.tint(
                                                ColorProvider(
                                                    Color(context.getColor(R.color.arrow_red))
                                                )
                                            )
                                        )
                                    }
                                    Text(
                                        text = Duration.between(
                                            timeMark.timeStamp,
                                            next.timeStamp
                                        ).formatted(),
                                        style = textStyle
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(modifier = GlanceModifier.height(itemPadding))
                        }
                    }
                    Box(
                        modifier = GlanceModifier.fillMaxSize()
                            .padding(bottom = padding, end = padding + 3.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        SquareIconButton(
                            imageProvider = ImageProvider(R.drawable.outline_more_time_24),
                            contentDescription = context.getString(R.string.add_new_time_description),
                            onClick = onAddTime
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
internal fun MediumContentWidgetPreview() {
    MediumContent(
        emptyList(),
        State.OK
    )
}