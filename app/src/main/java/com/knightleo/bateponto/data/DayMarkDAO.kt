package com.knightleo.bateponto.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.knightleo.bateponto.data.entity.Date
import com.knightleo.bateponto.data.entity.Date.Companion.asDayMark
import com.knightleo.bateponto.data.entity.DayMark
import com.knightleo.bateponto.data.entity.DayTimeMark
import com.knightleo.bateponto.data.entity.Time
import com.knightleo.bateponto.data.entity.Time.Companion.asTimeMark
import com.knightleo.bateponto.data.entity.TimeMark

@Dao
abstract class DayMarkDAO {
    @Query("SELECT * FROM daymark")
    abstract suspend fun getAll(): List<DayTimeMark>

    @Query("SELECT * FROM timemark WHERE date = :date")
    abstract suspend fun getAllMarksInDay(date: Date): List<TimeMark>

    @Query("SELECT * FROM daymark WHERE date >= :from AND date <= :to")
    abstract suspend fun getDaysBetween(from: Date, to: Date): List<DayTimeMark>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertDate(dayMark: DayMark)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertTime(timeMark: TimeMark)

    @Delete
    abstract suspend fun deleteTime(time: TimeMark)

    @Delete
    abstract suspend fun deleteDay(dayMark: DayMark)

    @Query("DELETE FROM timemark WHERE date=:day")
    abstract suspend fun deleteTimesInDay(day: Date)

    suspend fun sumTimeSpentOnDay(date: Date): Time? {
        val times = getAllMarksInDay(date)
        val l = if(times.size < 2) return null
        else if(times.size % 2 == 1) times.subList(0, times.lastIndex-1)
        else times
        var sum = Time()
        for(i in l.indices step 2) sum += l[i].time + l[i+1].time
        return sum
    }

    fun sumTimeSpentOnDay(date: DayTimeMark): Time? {
        val times = date.times
        val l = if(times.size < 2) return null
        else if(times.size % 2 == 1) times.subList(0, times.size-1)
        else times
        var sum = Time()
        for(i in l.indices step 2) sum += l[i + 1].time - l[i].time
        return sum
    }

    @Transaction
    @Delete
    suspend fun deleteDay(date: Date) {
        deleteTimesInDay(date)
        deleteDay(date.asDayMark())
    }

    @Transaction
    @Delete
    suspend fun deleteTime(time: Time, date: Date) {
        deleteTime(time.asTimeMark(date))
        if(getAllMarksInDay(date).isEmpty()) deleteDay(date)
    }

    @Transaction
    @Insert
    suspend fun insertDate(date: Date) {
        insertDate(dayMark = date.asDayMark())
    }

    @Transaction
    @Insert
    suspend fun insertTime(time: Time, date: Date): TimeMark {
        insertDate(date)
        val timeMark = time.asTimeMark(date)
        insertTime(timeMark)
        return timeMark
    }
}