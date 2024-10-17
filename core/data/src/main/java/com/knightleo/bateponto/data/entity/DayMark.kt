package com.knightleo.bateponto.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.knightleo.bateponto.data.OffsetTimeConverter
import com.knightleo.bateponto.data.entity.Day.Companion.asDay
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime
import java.time.OffsetTime

@Parcelize
data class Day(
    val day: Int = 0,
    val month: Int = 0,
    val year: Int = 0
) : Parcelable {
    override fun toString(): String = "$year-${month.toString().padStart(2, '0')}-${
        day.toString().padStart(2, '0')
    }"

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

        @JvmStatic
        fun now() = OffsetDateTime.now().asDay()
    }
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