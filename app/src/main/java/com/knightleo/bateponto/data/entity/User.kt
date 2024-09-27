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

data class UserJobs(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val jobs: List<Job>
)