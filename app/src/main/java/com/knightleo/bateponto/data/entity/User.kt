package com.knightleo.bateponto.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String
)

data class UserWorkTimes(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val workTimes: List<DayMark>
)