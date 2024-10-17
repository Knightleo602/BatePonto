package com.knightleo.bateponto.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.knightleo.bateponto.data.entity.Day
import com.knightleo.bateponto.data.entity.Day.Companion.asDay
import com.knightleo.bateponto.data.entity.DayMark
import com.knightleo.bateponto.data.entity.DayMarks
import com.knightleo.bateponto.data.entity.TimeMark
import com.knightleo.bateponto.data.entity.User
import com.knightleo.bateponto.data.entity.UserWorkTimes
import java.time.Duration
import java.time.OffsetDateTime
import java.time.OffsetTime

@Dao
abstract class DayMarkDAO {

    @Transaction
    @Query("SELECT * FROM DayMark WHERE userId=:userId AND day BETWEEN :from AND :to ORDER BY day DESC")
    abstract suspend fun getDaysBetween(
        userId: Int,
        from: Day,
        to: Day
    ): List<DayMarks>

    @Transaction
    @Query("SELECT * FROM User WHERE id=:userId")
    abstract suspend fun getAllUserTimes(userId: Int): UserWorkTimes

    @Delete
    abstract suspend fun deleteUser(user: User)

    @Delete
    abstract suspend fun deleteDay(vararg dayMarks: DayMark)

    @Delete
    internal abstract suspend fun deleteTime(vararg timeMark: TimeMark)

    @Query("UPDATE TimeMark SET timeStamp=:newTime WHERE timeStamp=:oldTime AND day=:day")
    abstract suspend fun updateTime(oldTime: OffsetTime, newTime: OffsetTime, day: Day)

    @Query("UPDATE DayMark SET day=:newDate WHERE day=:oldDate AND userId=:userId")
    abstract suspend fun updateDate(userId: Int, oldDate: Day, newDate: Day)

    @Transaction
    open suspend fun deleteTimeFromDay(userId: Int, vararg timeMark: TimeMark) {
        timeMark.forEach { tm ->
            deleteTime(tm)
            if (getWorkTimesInDay(userId, tm.day).isEmpty()) {
                deleteDay(DayMark(tm.day, userId))
            }
        }
    }

    @Query("SELECT TimeMark.timeStamp, TimeMark.day FROM TimeMark INNER JOIN DayMark ON TimeMark.day=DayMark.day WHERE DayMark.userId=:userId AND TimeMark.day=:day")
    abstract suspend fun getWorkTimesInDay(userId: Int, day: Day): List<TimeMark>

    @Insert
    abstract suspend fun createUser(user: User): Long

    @Query("SELECT * FROM User LIMIT 1")
    abstract suspend fun getUser(): User?

    @Query("SELECT * FROM User WHERE id=:userId")
    abstract suspend fun getUser(userId: Int): User

    @Insert
    abstract suspend fun insertNewTimestamp(timeMark: TimeMark)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertNewDate(dayMarks: DayMark)

    @Transaction
    open suspend fun currentWeekDays(userId: Int): List<DayMarks> {
        val (start, now) = currentWeekRange()
        return getDaysBetween(userId, start, now)
    }

    @Transaction
    open suspend fun timeSpentInDay(userId: Int, day: Day): Duration {
        val times = getWorkTimesInDay(userId, day).let {
            if (it.size % 2 == 1) it.subList(0, it.size - 1)
            else it
        }
        var sum = Duration.ZERO
        for (i in times.indices step 2) {
            sum += Duration.between(times[i + 1].timeStamp, times[i].timeStamp)
        }
        return sum
    }

    @Transaction
    open suspend fun timeSpentInCurrentWeek(userId: Int): Duration {
        val now = OffsetDateTime.now()
        val startOfWeek = now.minusDays(now.dayOfWeek.ordinal.toLong())
        return timeSpentInPeriod(userId, startOfWeek.asDay(), now.asDay())
    }

    @Transaction
    open suspend fun timeSpentInPeriod(
        userId: Int,
        from: Day,
        to: Day
    ): Duration {
        val times = getDaysBetween(userId, from, to)
        var sum = Duration.ZERO
        for (day in times) {
            val l = day.times.let {
                if (it.size % 2 == 1) it.subList(0, it.size - 1)
                else it
            }
            for (i in l.indices step 2) sum += Duration.between(l[i + 1].timeStamp, l[i].timeStamp)
        }
        return sum
    }

    @Transaction
    open suspend fun insertCurrentTimeStamp(userId: Int) {
        val now = OffsetDateTime.now()
        val day = now.asDay()
        insertNewDate(DayMark(day, userId))
        insertNewTimestamp(TimeMark(now.toOffsetTime(), day))
    }
}