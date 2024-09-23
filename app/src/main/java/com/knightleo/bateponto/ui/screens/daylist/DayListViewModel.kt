package com.knightleo.bateponto.ui.screens.daylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knightleo.bateponto.data.DayMarkDAO
import com.knightleo.bateponto.data.currentWeekRange
import com.knightleo.bateponto.data.entity.Day
import com.knightleo.bateponto.data.entity.DayMark
import com.knightleo.bateponto.data.entity.TimeMark
import com.knightleo.bateponto.data.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.OffsetTime

typealias DayWorked = Pair<DayAndDurationWorked, List<OffsetTime>>
typealias DayAndDurationWorked = Pair<Day, Duration>
typealias DaysAndTimesWorked = List<DayWorked>
typealias Week = Pair<Day, Day>

data class MarkState(
    val marks: DaysAndTimesWorked = emptyList(),
    val selectedWeek: Week = Day() to Day()
)

class DayListViewModel(
    private val dayMarkDAO: DayMarkDAO
) : ViewModel() {

    private val _markState: MutableStateFlow<MarkState> = MutableStateFlow(MarkState())
    var user: User = User(0, "")
        private set
    val markState: StateFlow<MarkState> get() = _markState

    init {
        coroutineLaunch {
            user = dayMarkDAO.getUser() ?: kotlin.run {
                val id = dayMarkDAO.createUser(User(0, "Bob"))
                dayMarkDAO.getUser(id.toInt())
            }
            loadMarks()
        }
    }

    private inline fun coroutineLaunch(
        crossinline block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) { block() }
    }

    private suspend fun loadMarks(
        week: Week = currentWeekRange()
    ) {
        val marks = dayMarkDAO.getDaysBetween(user.id, week.first, week.second)
        val sums = MutableList(marks.size) {
            dayMarkDAO.timeSpentInDay(
                user.id,
                marks[it].dayMark.day
            )
        }
        val times = marks.mapIndexed { index, dayMarks ->
            val times = dayMarkDAO.getWorkTimesInDay(user.id, dayMarks.dayMark.day)
            dayMarks.dayMark.day to sums[index] to times.map { it.timeStamp }
        }
        _markState.update {
            it.copy(marks = times, selectedWeek = week)
        }
    }

    fun addNewMark() = coroutineLaunch {
        dayMarkDAO.insertCurrentTimeStamp(user.id)
        loadMarks()
    }

    fun delete(date: Day?, time: OffsetTime? = null) = coroutineLaunch {
        if (date == null) return@coroutineLaunch
        time?.let { deleteTime(date, it) } ?: deleteDay(date)
    }

    private fun deleteDay(date: Day) = coroutineLaunch {
        dayMarkDAO.deleteDay(DayMark(date, user.id))
        loadMarks()
    }

    private fun deleteTime(date: Day, time: OffsetTime) = coroutineLaunch {
        dayMarkDAO.deleteTimeFromDay(user.id, TimeMark(time, date))
        loadMarks()
    }

    fun updateTime(
        previousTime: OffsetTime,
        newTime: OffsetTime,
        date: Day,
    ) = coroutineLaunch {
        dayMarkDAO.updateTime(previousTime, newTime, date)
        loadMarks()
    }

    fun changeWeek(week: Week) = coroutineLaunch { loadMarks(week) }
}