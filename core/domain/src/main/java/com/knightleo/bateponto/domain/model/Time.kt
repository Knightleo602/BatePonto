package com.knightleo.bateponto.domain.model

import com.knightleo.bateponto.domain.model.Day.Companion.asDay
import java.time.OffsetTime

data class Time(
    val timeStamp: OffsetTime,
    val day: Day
) {
    override fun toString(): String = "$timeStamp||$day"

    companion object {
        fun String.asTime(): Time {
            val (time, day) = split("///")
            return Time(OffsetTime.parse(time), day.asDay())
        }
    }
}
