package com.knightleo.bateponto.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.min

private inline val locale: Locale get() = Locale("pt", "br")

fun currentDateAndTime(): Pair<Date, Time> {
    val dateFormat = SimpleDateFormat("${Date.pattern}-${Time.pattern}", locale)
    val current = dateFormat.format(java.util.Calendar.getInstance().time).split("-")
    val date = DateConverters.fromDateString(current[0])
    val time = TimeConverter.fromTimeString(current[1])
    return date to time
}

data class Date(
    val year: String,
    val month: String,
    val day: String,
    val weekDayName: String
) {
    val formatted: String get() = "$day/$month/$year"
    companion object {
        fun Date.asDayMark(): DayMark = DayMark(date = this)
        internal val pattern: String get() = "yyyy_MM_dd_u"
    }
}

data class Time(
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0,
    val millis: Int = 0
): Comparable<Time> {
    val formatted: String get() = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
    companion object {
        fun Time.asTimeMark(date: Date): TimeMark = TimeMark(time = this, date = date)
        internal val pattern: String get() = "HH_mm_ss_SS"
    }

    private inline fun op(other: Time, diff: (Long, Long) -> Long): Time {
        val m1: Long = hour*3_600_000 + minute*60_000 + second * 1000 + millis.toLong()
        val m2: Long = other.hour*3_600_000 + other.minute*60_000 + other.second * 1000 + other.millis.toLong()
        val totalMilliseconds = diff(m1, m2).toInt()
        val newHours = totalMilliseconds / (3600 * 1000)
        var remainingMilliseconds = totalMilliseconds % (3600 * 1000)
        val newMinutes = remainingMilliseconds / (60 * 1000)
        remainingMilliseconds %= (60 * 1000)
        val newSeconds = remainingMilliseconds / 1000
        val newMilliseconds = remainingMilliseconds % 1000
        return Time(
            hour = newHours,
            minute = newMinutes,
            second = newSeconds,
            millis = newMilliseconds
        )
    }

    operator fun minus(other: Time) = op(other) { m1, m2 -> m1-m2 }

    operator fun plus(other: Time) = op(other) { m1, m2 -> m1+m2 }

    override fun compareTo(other: Time): Int = compareValuesBy(
        this,
        other,
        { it.hour },
        { it.minute },
        { it.second },
        { it.millis }
    )
}

object DateConverters {
    private val weekDays: Array<out String>
        get() = DateFormatSymbols.getInstance(Locale("pt", "br")).weekdays

    @TypeConverter
    fun fromDate(value: Date): String = value.run {
        val i = weekDays.indexOf(weekDayName)
        "${year}_${month}_${day}_$i"
    }
    @TypeConverter
    fun fromDateString(value: String): Date {
        val date = value.split("_")
        return Date(date[0], date[1], date[2], weekDays[date[3].toInt()])
    }
}

object TimeConverter {
    @TypeConverter
    fun fromTime(value: Time): String = value.run { "${hour}_${minute}_${second}_${millis}" }
    @TypeConverter
    fun fromTimeString(value: String): Time {
        val time = value.split("_")
        return Time(time[0].toInt(), time[1].toInt(), time[2].toInt(), time[3].toInt())
    }
}

data class DayTimeMark(
    @Embedded val dayMark: DayMark,
    @Relation(
        parentColumn = "date",
        entityColumn = "date"
    )
    val times: List<TimeMark>
)

@Entity
data class DayMark(
    @PrimaryKey(autoGenerate = false) val date: Date
)

@Entity(primaryKeys = ["time", "date"])
data class TimeMark(
    val time: Time,
    val date: Date
)