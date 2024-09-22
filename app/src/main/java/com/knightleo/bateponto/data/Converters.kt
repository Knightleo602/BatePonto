package com.knightleo.bateponto.data

import androidx.room.TypeConverter
import com.knightleo.bateponto.data.entity.Day
import com.knightleo.bateponto.data.entity.Day.Companion.asDay
import java.time.OffsetTime

class DayTypeConverter {
    @TypeConverter
    fun stringToDay(string: String): Day = string.asDay()

    @TypeConverter
    fun dayToString(day: Day): String = day.toString()
}

class OffsetTimeConverter {
    @TypeConverter
    fun stringToOffset(string: String): OffsetTime = OffsetTime.parse(string)

    @TypeConverter
    fun offsetToString(offsetTime: OffsetTime): String = offsetTime.toString()
}