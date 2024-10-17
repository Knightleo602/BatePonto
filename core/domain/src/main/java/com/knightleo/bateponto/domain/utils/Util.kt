package com.knightleo.bateponto.domain.utils

import androidx.compose.ui.Modifier
import com.knightleo.bateponto.domain.model.Day
import com.knightleo.bateponto.domain.model.Time
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

val List<Time>.timeSpent: Duration
    get() {
        if (size <= 1) return Duration.ZERO
        var spent = Duration.ZERO
        for (i in indices step 2) {
            val start = get(i)
            val end = getOrNull(i + 1) ?: break
            spent += Duration.between(start.timeStamp, end.timeStamp)
        }
        return spent
    }

