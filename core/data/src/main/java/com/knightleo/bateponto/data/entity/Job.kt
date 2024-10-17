package com.knightleo.bateponto.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.OffsetTime

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
data class Job(
    val name: String,
    @ColumnInfo(index = true) val userId: Int,
    val startTime: OffsetTime? = null,
    val lunchTime: OffsetTime? = null,
    val lunchEndTime: OffsetTime? = null,
    val endTime: OffsetTime? = null,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
