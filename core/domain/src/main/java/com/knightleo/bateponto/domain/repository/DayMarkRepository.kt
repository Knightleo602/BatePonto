package com.knightleo.bateponto.domain.repository

import com.knightleo.bateponto.domain.model.Day
import com.knightleo.bateponto.domain.model.Job
import com.knightleo.bateponto.domain.model.Time
import com.knightleo.bateponto.domain.model.User
import java.time.Duration

typealias DayTimes = Pair<Day, List<Time>>

interface DayMarkRepository {
    suspend fun createUser(user: User): Int
    suspend fun getUser(id: Int): User
    suspend fun getAllJobs(userId: Int): List<Job>
    suspend fun getJob(id: Int): Job
    suspend fun getTimeInDaysBetween(jobId: Int, from: Day, to: Day): List<DayTimes>
    suspend fun getTimeInDay(jobId: Int, day: Day): List<Time>
    suspend fun getTimeSpentInDay(jobId: Int, day: Day): Duration
    suspend fun insertTimeNow(jobId: Int)
    suspend fun deleteTime(jobId: Int, day: Day, time: Time)
    suspend fun deleteDay(jobId: Int, day: Day)
    suspend fun updateTime(jobId: Int, day: Day, previousTime: Time, newTime: Time)
}