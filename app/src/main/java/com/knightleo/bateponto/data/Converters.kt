package com.knightleo.bateponto.data

import androidx.room.TypeConverter
import com.knightleo.bateponto.data.entity.Day
import com.knightleo.bateponto.data.entity.Day.Companion.asDay
import java.time.OffsetTime
import java.time.format.DateTimeFormatter

class DayTypeConverter {
    @TypeConverter
    fun stringToDay(string: String): Day = string.asDay()

    @TypeConverter
    fun dayToString(day: Day): String = day.toString()
}

class OffsetTimeConverter {
    @TypeConverter
    fun stringToOffset(string: String): OffsetTime {
        val formater = DateTimeFormatter.ofPattern("HH-mm-ss")
        return OffsetTime.parse(string, formater)
    }

    @TypeConverter
    fun offsetToString(offsetTime: OffsetTime): String =
        "${offsetTime.hour}-${offsetTime.minute}-${offsetTime.second}"
}