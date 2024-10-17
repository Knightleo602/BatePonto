package com.knightleo.bateponto.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.knightleo.bateponto.data.OffsetTimeConverter
import com.knightleo.bateponto.data.entity.Day.Companion.asDay
import java.time.OffsetDateTime
import java.time.OffsetTime

data class Day(
    val day: Int = 0,
    val month: Int = 0,
    val year: Int = 0
) {
    override fun toString(): String = "$year-${month.toString().padStart(2, '0')}-${
        day.toString().padStart(2, '0')
    }"

    companion object {
        internal fun String.asDay(): Day {
            val s = split('-')
            return Day(
                year = s[0].toInt(),
                month = s[1].toInt(),
                day = s[2].toInt()
            )
        }

        fun OffsetDateTime.asDay() = Day(
            day = dayOfMonth,
            month = monthValue,
            year = year
        )

        fun Day.asModel() = com.knightleo.bateponto.domain.model.Day(
            day = day,
            month = month,
            year = year
        )

        fun com.knightleo.bateponto.domain.model.Day.asEntity() = Day(
            day = day,
            month = month,
            year = year
        )
    }
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Job::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("jobId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DayMark(
    @PrimaryKey val day: Day,
    @ColumnInfo(index = true) val jobId: Int
)

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DayMark::class,
            parentColumns = arrayOf("day"),
            childColumns = arrayOf("day"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class TimeMark(
    @PrimaryKey val timeStamp: OffsetTime,
    @ColumnInfo(index = true) val day: Day
) {
    override fun toString(): String = "${OffsetTimeConverter().offsetToString(timeStamp)}$SPLIT$day"

    companion object {
        private const val SPLIT = "///"
        fun String.asTimeMark(): TimeMark {
            val s = split(SPLIT)
            return TimeMark(
                timeStamp = OffsetTimeConverter().stringToOffset(s[0]),
                day = s[1].asDay()
            )
        }
    }
}

data class DayMarks(
    @Embedded val dayMark: DayMark,
    @Relation(
        parentColumn = "day",
        entityColumn = "day"
    )
    val times: List<TimeMark>
)