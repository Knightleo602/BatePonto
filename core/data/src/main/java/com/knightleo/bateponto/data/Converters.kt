package com.knightleo.bateponto.data

import androidx.room.TypeConverter
import com.knightleo.bateponto.data.entity.Day
import com.knightleo.bateponto.data.entity.Day.Companion.asDay
import java.time.OffsetTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class DayTypeConverter {
    @TypeConverter
    fun stringToDay(string: String): Day = string.asDay()

    @TypeConverter
    fun dayToString(day: Day): String = day.toString()
}

class OffsetTimeConverter {
    private val formatter
        get() = DateTimeFormatter.ofPattern("HH:mm:ss.SSSXXXXX", Locale.US)

    @TypeConverter
    fun stringToOffset(string: String): OffsetTime =
        OffsetTime.parse(string, formatter)

    @TypeConverter
    fun offsetToString(offsetTime: OffsetTime): String = formatter.format(offsetTime)
}