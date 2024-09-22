package com.knightleo.bateponto.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.Instant
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.temporal.Temporal

data class Day(
    val day: Int,
    val month: Int,
    val year: Int
) {
    override fun toString(): String = "$year-$month-$day"

    companion object {
        fun String.asDay(): Day {
            val s = split('-')
            return Day(
                year = s[0].toInt(),
                month = s[1].toInt(),
                day = s[2].toInt()
            )
        }

        fun OffsetDateTime.asDay(): Day = Day(
            month = monthValue,
            year = year,
            day = dayOfMonth
        )
    }

    fun toTemporal(): Temporal = Instant.parse(toString())
    fun toOffsetDateTime(): OffsetDateTime = OffsetDateTime.parse(toString())
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DayMark(
    @PrimaryKey val day: Day,
    @ColumnInfo(index = true) val userId: Int
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
)

data class DayMarks(
    @Embedded val dayMark: DayMark,
    @Relation(
        parentColumn = "day",
        entityColumn = "day"
    )
    val times: List<TimeMark>

)