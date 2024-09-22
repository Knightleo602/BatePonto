package com.knightleo.bateponto.ui.screens.daylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knightleo.bateponto.data.DayMarkDAO
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

data class MarkState(
    val marks: DaysAndTimesWorked = emptyList()
)

class DayListViewModel(
    private val dayMarkDAO: DayMarkDAO
) : ViewModel() {

    private val _markState: MutableStateFlow<MarkState> = MutableStateFlow(MarkState())
    private var user: User = User(0, "")
    val markState: StateFlow<MarkState> get() = _markState

    init {
        coroutineLaunch { loadMarks() }
    }

    private inline fun coroutineLaunch(
        crossinline block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) { block() }
    }

    private suspend fun loadMarks() {
        user = dayMarkDAO.getUser() ?: kotlin.run {
            val id = dayMarkDAO.createUser(User(0, "Bob"))
            dayMarkDAO.getUser(id.toInt())
        }
        val marks = dayMarkDAO.getAllUserTimes(user.id)
        val sums = MutableList(marks.workTimes.size) {
            dayMarkDAO.timeSpentInDay(
                user.id,
                marks.workTimes[it].day
            )
        }
        val times = marks.workTimes.mapIndexed { index, dayMarks ->
            val times = dayMarkDAO.getWorkTimesInDay(user.id, dayMarks.day)
            dayMarks.day to sums[index] to times.map { it.timeStamp }
        }
        _markState.update {
            it.copy(marks = times)
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
}