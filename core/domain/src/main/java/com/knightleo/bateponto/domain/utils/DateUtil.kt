package com.knightleo.bateponto.domain.utils

import com.knightleo.bateponto.domain.model.Day
import com.knightleo.bateponto.domain.model.Day.Companion.asDay
import java.time.LocalDateTime
import java.time.OffsetDateTime

fun currentWeekRange(): Pair<Day, Day> {
    val now = OffsetDateTime.now()
    val endOfWeek = now.plusDays(6 - now.dayOfWeek.ordinal.toLong())
    val startOfWeek = now.minusDays(now.dayOfWeek.ordinal.toLong())
    return startOfWeek.asDay() to endOfWeek.asDay()
}

fun today(): Day = OffsetDateTime.now().asDay()

fun Day.weekRange(): Pair<Day, Day> {
    val now = LocalDateTime.of(year, month, day, 0, 0)
    val startOfWeek = now.minusDays(now.dayOfWeek.ordinal.toLong())
    val startDay = Day(
        day = startOfWeek.dayOfMonth,
        month = startOfWeek.monthValue,
        year = startOfWeek.year
    )
    val endOfWeek = startOfWeek.plusDays(6)
    val endDay = Day(
        day = endOfWeek.dayOfMonth,
        month = endOfWeek.monthValue,
        year = endOfWeek.year
    )
    return startDay to endDay
}