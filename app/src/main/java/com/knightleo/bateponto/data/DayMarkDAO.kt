package com.knightleo.bateponto.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.knightleo.bateponto.data.entity.DayWorkTimes
import com.knightleo.bateponto.data.entity.User
import com.knightleo.bateponto.data.entity.UserWorkTimes
import com.knightleo.bateponto.data.entity.WorkTime
import java.time.Duration
import java.time.OffsetDateTime
import java.time.OffsetTime

@Dao
abstract class DayMarkDAO {

    @Query("SELECT timeStamp as day FROM WorkTime WHERE userId=:userId AND date(timeStamp) BETWEEN date(:from) AND date(:to) GROUP BY date(timeStamp)")
    abstract suspend fun getDaysBetween(
        userId: Int,
        from: OffsetDateTime,
        to: OffsetDateTime
    ): List<DayWorkTimes>

    @Transaction
    suspend fun currentWeekDays(userId: Int): List<DayWorkTimes> {
        val now = OffsetDateTime.now()
        val startOfWeek = now.minusDays(now.dayOfWeek.ordinal.toLong())
        return getDaysBetween(userId, startOfWeek, now)
    }

    @Transaction
    suspend fun timeSpentInDay(userId: Int, day: OffsetDateTime): Duration {
        val times = getWorkTimesInDay(userId, day).let {
            if(it.size % 2 == 1) it.subList(0, it.size-1)
            else it
        }
        var sum = Duration.ZERO
        for(i in times.indices step 2) {
            sum += Duration.between(times[i+1].timeStamp, times[i].timeStamp)
        }
        return sum
    }

    @Transaction
    suspend fun timeSpentInCurrentWeek(userId: Int): Duration {
        val now = OffsetDateTime.now()
        val startOfWeek = now.minusDays(now.dayOfWeek.ordinal.toLong())
        return timeSpentInPeriod(userId, startOfWeek, now)
    }

    @Transaction
    suspend fun timeSpentInPeriod(userId: Int, from: OffsetDateTime, to: OffsetDateTime): Duration {
        val times = getDaysBetween(userId, from, to)
        var sum = Duration.ZERO
        for(day in times) {
            val l = day.times.let {
                if(it.size % 2 == 1) it.subList(0, it.size-1)
                else it
            }
            for(i in l.indices step 2) sum += Duration.between(l[i+1], l[i])
        }
        return sum
    }

    suspend fun insertCurrentTimeStamp(userId: Int) {
        val now = OffsetDateTime.now()
        insertNewTimestamp(now, userId)
    }

    @Query("SELECT * FROM User INNER JOIN WorkTime WHERE User.id=:userId GROUP BY date(WorkTime.timeStamp)")
    abstract suspend fun getAllUserTimes(userId: Int): UserWorkTimes

    @Delete
    abstract suspend fun deleteUser(user: User)

    @Query("SELECT * FROM WorkTime WHERE userId=:userId AND date(timeStamp)=date(:day)")
    abstract suspend fun getWorkTimesInDay(userId: Int, day: OffsetDateTime): List<WorkTime>

    @Query("DELETE FROM WorkTime WHERE date(timeStamp)=date(:day) AND userId=:userId")
    abstract suspend fun deleteTimesInDay(userId: Int, day: OffsetDateTime)

    @Insert
    abstract suspend fun createUser(user: User): Long

    @Query("SELECT * FROM User LIMIT 1")
    abstract suspend fun getUser(): User?

    @Query("SELECT * FROM User WHERE id=:userId")
    abstract suspend fun getUser(userId: Int): User

    @Query("INSERT INTO WorkTime (timestamp, userid) VALUES (:timeStamp, :userId)")
    abstract suspend fun insertNewTimestamp(timeStamp: OffsetDateTime, userId: Int)

    @Query("DELETE FROM WorkTime WHERE userId=:userId AND datetime(timeStamp)=datetime(:time)")
    abstract suspend fun deleteWorkTime(userId: Int, time: OffsetDateTime)
}