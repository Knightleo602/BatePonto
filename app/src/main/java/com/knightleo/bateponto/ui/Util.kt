package com.knightleo.bateponto.ui

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import com.knightleo.bateponto.data.entity.Day
import com.knightleo.bateponto.data.entity.Day.Companion.asDay
import java.time.Duration
import java.time.Instant
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
    val secondsAbs = seconds.absoluteValue
    val v = "%d:%02d".format(
        secondsAbs / 3600,
        (secondsAbs % 3600) / 60
    )
    return if (seconds < 0) "-$v" else v
}

fun hourAndMinuteToOffsetTime(hour: Int, minute: Int): OffsetTime = OffsetTime.of(
    hour, minute, 59, 0, OffsetTime.now().offset
)

@OptIn(ExperimentalMaterial3Api::class)
val DatePickerState.selectedDay: Day
    get() {
        if (selectedDateMillis == null) return Day()
        val offsetDateTime = Instant
            .ofEpochMilli(selectedDateMillis!!).atOffset(OffsetTime.now().offset)
        return offsetDateTime.plusDays(1L).asDay()
    }

