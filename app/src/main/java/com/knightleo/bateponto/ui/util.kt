package com.knightleo.bateponto.ui

import androidx.compose.ui.Modifier
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

inline fun Modifier.condition(condition: Boolean, onTrue: (Modifier) -> Modifier): Modifier =
    if(condition) then(onTrue(Modifier))
    else this

fun OffsetDateTime.formattedDate() =
    "${format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))} - ${dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())}"

fun OffsetDateTime.formattedTime() =
    toOffsetTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
