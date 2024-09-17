package com.knightleo.bateponto.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.OffsetDateTime
import java.time.OffsetTime

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String
)

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
data class WorkTime(
    @PrimaryKey val timeStamp: OffsetDateTime,
    @ColumnInfo(index = true)
    private val userId: Int
)

data class UserWorkTimes(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val workTimes: List<DayWorkTimes>
)

data class DayWorkTimes(
    val day: OffsetDateTime,
    val times: List<OffsetTime>
)