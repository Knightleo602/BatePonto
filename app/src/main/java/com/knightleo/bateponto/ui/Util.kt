package com.knightleo.bateponto.ui

import androidx.compose.ui.Modifier
import com.knightleo.bateponto.data.entity.Day
import java.time.Duration
import java.time.OffsetTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.absoluteValue

inline fun Modifier.condition(condition: Boolean, onTrue: (Modifier) -> Modifier): Modifier =
    if (condition) then(onTrue(Modifier))
    else this

fun Int.stringPadded(length: Int = 2) = toString().padStart(length, '0')

val Day.formatted: String get() = "${day.stringPadded()}/${month.stringPadded()}/${year.stringPadded()}"

fun OffsetTime.formattedTime(): String =
    format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

fun Duration.formatted(): String {
    val seconds = seconds.absoluteValue
    return "%d:%02d".format(
        seconds / 3600,
        (seconds % 3600) / 60
    )
}

fun hourAndMinuteToOffsetTime(hour: Int, minute: Int): OffsetTime = OffsetTime.of(
    hour, minute, 59, 0, OffsetTime.now().offset
)

