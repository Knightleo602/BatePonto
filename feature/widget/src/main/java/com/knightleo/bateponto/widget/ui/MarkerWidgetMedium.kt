package com.knightleo.bateponto.widget.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.CircularProgressIndicator
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
import androidx.glance.text.FontStyle
import androidx.glance.text.Text
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
    times: List<TimeMark>,
    modifier: GlanceModifier = GlanceModifier,
    currentState: State = State.LOADING,
    onAddTime: () -> Unit = {},
    updateUser: () -> Unit = {},
) {
    val currentTimes = times.reversed()
    val context = LocalContext.current
    val padding = 13.dp
    val itemPadding = 6.dp
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
                    times.timeSpent.formatted()
                ),
                style = WidgetTypography.normalText
            )
        }
        Napier.i { "Showing ${currentTimes.size} items on state $currentState" }
        Box {
            LazyColumn {
                item {
                    Spacer(modifier = GlanceModifier.height(padding))
                }
                itemsIndexed(currentTimes) { index, timeMark ->
                    val next = currentTimes.getOrNull(index + 1)
                    Column(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.padding(start = padding).fillMaxWidth()
                    ) {
                        Item(index, timeMark)
                        if (next != null) {
                            BreakPeriod(
                                index,
                                currentTimes,
                                next,
                                timeMark
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = GlanceModifier.height(padding))
                }
            }
            Box(
                modifier = GlanceModifier.fillMaxSize()
                    .padding(bottom = padding, end = padding),
                contentAlignment = Alignment.BottomEnd
            ) {
                when (currentState) {
                    State.NO_USER -> NoUser(updateUser = updateUser)
                    State.LOADING -> CircularProgressIndicator()
                    else -> {
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

@Composable
private fun Item(
    index: Int,
    timeMark: TimeMark
) {
    val style = if (index == 0) WidgetTypography.normalText.copy(
        fontStyle = FontStyle.Italic
    )
    else WidgetTypography.normalText
    Text(
        timeMark.timeStamp.formattedTime(),
        style = style,
    )
}

@Composable
private fun BreakPeriod(
    index: Int,
    currentTimes: List<TimeMark>,
    next: TimeMark,
    timeMark: TimeMark,
    modifier: GlanceModifier = GlanceModifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.Start,
        modifier = GlanceModifier.fillMaxWidth().then(modifier)
    ) {
        if (index % 2 == currentTimes.size % 2) {
            Image(
                ImageProvider(R.drawable.rounded_arrow_right_24),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    GlanceTheme.colors.primary
                )
            )
        } else {
            Image(
                ImageProvider(R.drawable.rounded_arrow_left_24),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    GlanceTheme.colors.tertiary
                )
            )
        }
        Text(
            text = Duration.between(
                next.timeStamp,
                timeMark.timeStamp
            ).formatted(),
            style = WidgetTypography.subText
        )
    }
}

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
internal fun MediumContentWidgetPreview() {
    MediumContent(
        emptyList(),
        currentState = State.OK,
    )
}