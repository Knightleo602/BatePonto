package com.knightleo.bateponto.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.knightleo.bateponto.data.entity.Day
import com.knightleo.bateponto.data.entity.Day.Companion.asDay
import com.knightleo.bateponto.data.entity.Day.Companion.asEntity
import com.knightleo.bateponto.data.entity.DayMark
import com.knightleo.bateponto.data.entity.DayMarks
import com.knightleo.bateponto.data.entity.Job
import com.knightleo.bateponto.data.entity.TimeMark
import com.knightleo.bateponto.data.entity.User
import com.knightleo.bateponto.data.entity.UserJobs
import com.knightleo.bateponto.domain.utils.currentWeekRange
import java.time.Duration
import java.time.OffsetDateTime
import java.time.OffsetTime

@Dao
abstract class DayMarkDAO {

    @Transaction
    @Query("SELECT * FROM DayMark WHERE jobId=:jobId AND day BETWEEN :from AND :to ORDER BY day DESC")
    abstract suspend fun getDaysBetween(
        jobId: Int,
        from: Day,
        to: Day
    ): List<DayMarks>

    @Transaction
    @Query("SELECT * FROM User WHERE id=:userId")
    abstract suspend fun getAllJobs(userId: Int): UserJobs

    @Delete
    abstract suspend fun deleteUser(user: User)

    @Delete
    abstract suspend fun deleteDay(vararg dayMarks: DayMark)

    @Delete
    internal abstract suspend fun deleteTime(vararg timeMark: TimeMark)

    @Query("UPDATE TimeMark SET timeStamp=:newTime WHERE timeStamp=:oldTime AND day=:day")
    abstract suspend fun updateTime(oldTime: OffsetTime, newTime: OffsetTime, day: Day)

    @Query("UPDATE DayMark SET day=:newDate WHERE day=:oldDate AND jobId=:jobId")
    abstract suspend fun updateDate(jobId: Int, oldDate: Day, newDate: Day)

    @Insert
    abstract suspend fun insertNewJob(job: Job)

    @Query("INSERT INTO Job (name, userId) VALUES (:name, :userId)")
    abstract suspend fun insertNewJob(name: String, userId: Int)

    @Query("SELECT * FROM Job WHERE id=:jobId")
    abstract suspend fun getJob(jobId: Int): Job

    @Query("DELETE FROM Job WHERE id=:jobId")
    abstract suspend fun deleteJob(jobId: Int)

    @Update
    abstract suspend fun updateJob(jobId: Job)

    @Transaction
    open suspend fun deleteTimeFromDay(jobId: Int, vararg timeMark: TimeMark) {
        timeMark.forEach { tm ->
            deleteTime(tm)
            if (getWorkTimesInDay(jobId, tm.day).isEmpty()) {
                deleteDay(DayMark(tm.day, jobId))
            }
        }
    }

    @Query("SELECT * FROM TimeMark INNER JOIN DayMark ON TimeMark.day=DayMark.day WHERE DayMark.jobId=:jobId AND TimeMark.day=:day")
    abstract suspend fun getWorkTimesInDay(jobId: Int, day: Day): List<TimeMark>

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
        return getDaysBetween(userId, start.asEntity(), now.asEntity())
    }

    @Transaction
    open suspend fun timeSpentInDay(jobId: Int, day: Day): Duration {
        val times = getWorkTimesInDay(jobId, day).let {
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
        jobId: Int,
        from: Day,
        to: Day
    ): Duration {
        val times = getDaysBetween(jobId, from, to)
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
    open suspend fun insertCurrentTimeStamp(jobId: Int) {
        val now = OffsetDateTime.now()
        val day = now.asDay()
        insertNewDate(DayMark(day, jobId))
        insertNewTimestamp(TimeMark(now.toOffsetTime(), day))
    }
}