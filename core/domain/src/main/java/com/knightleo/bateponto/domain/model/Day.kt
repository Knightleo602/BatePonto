package com.knightleo.bateponto.domain.model

import java.time.OffsetDateTime

data class Day(
    val day: Int = 0,
    val month: Int = 0,
    val year: Int = 0
) {
    override fun toString(): String = "$day.$month.$year"

    companion object {
        fun OffsetDateTime.asDay(): Day = Day(
            month = monthValue,
            year = year,
            day = dayOfMonth
        )

        fun String.asDay() = split(".").let {
            Day(
                day = it[0].toInt(),
                month = it[1].toInt(),
                year = it[2].toInt()
            )
        }

        @JvmStatic
        fun now() = OffsetDateTime.now().asDay()
    }
}
