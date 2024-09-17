package com.knightleo.bateponto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knightleo.bateponto.data.DayMarkDAO
import com.knightleo.bateponto.data.entity.User
import com.knightleo.bateponto.data.entity.WorkTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.OffsetDateTime
import java.time.OffsetTime

data class MarkState(
    val marks: List<Pair<Pair<OffsetDateTime, Duration>, List<OffsetTime>>> = emptyList()
)

class ViewModel(
    private val dayMarkDAO: DayMarkDAO
) : ViewModel() {

    private val _markState: MutableStateFlow<MarkState> = MutableStateFlow(MarkState())
    private var user: User = User(0, "")
    val markState: StateFlow<MarkState> get() = _markState

    init {
        viewModelScope.launch {
            loadMarks()
        }
    }

    private suspend fun loadMarks() {
        user = dayMarkDAO.getUser() ?: kotlin.run {
            val id = dayMarkDAO.createUser(User(0, "Bob"))
            dayMarkDAO.getUser(id.toInt())
        }
        val marks = dayMarkDAO.getAllUserTimes(user.id)
        val sums = MutableList(marks.workTimes.size) { dayMarkDAO.timeSpentInDay(user.id, marks.workTimes[it].day) }
        _markState.update {
            it.copy(marks = marks.workTimes.mapIndexed { index, wt ->  (wt.day to sums[index]) to wt.times })
        }
    }

    fun addNewMark() = viewModelScope.launch {
        dayMarkDAO.insertCurrentTimeStamp(user.id)
        loadMarks()
    }

    fun deleteDay(date: WorkTime) = viewModelScope.launch {
        dayMarkDAO.deleteTimesInDay(user.id, date.timeStamp)
        loadMarks()
    }

    fun deleteTime(time: WorkTime) = viewModelScope.launch {
        dayMarkDAO.deleteWorkTime(user.id, time.timeStamp)
        loadMarks()
    }

    fun sumOfDay(date: WorkTime) = viewModelScope.launch {
        dayMarkDAO.timeSpentInDay(user.id, date.timeStamp)
    }
}