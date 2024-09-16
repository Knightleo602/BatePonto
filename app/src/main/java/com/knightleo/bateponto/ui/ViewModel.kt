package com.knightleo.bateponto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knightleo.bateponto.data.DayMarkDAO
import com.knightleo.bateponto.data.entity.Date
import com.knightleo.bateponto.data.entity.DayTimeMark
import com.knightleo.bateponto.data.entity.Time
import com.knightleo.bateponto.data.entity.currentDateAndTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MarkState(
    val marks: List<DayTimeMark> = emptyList(),
    val sums: List<Time?> = emptyList()
)

class ViewModel(
    private val dayMarkDAO: DayMarkDAO
) : ViewModel() {

    private val _markState: MutableStateFlow<MarkState> = MutableStateFlow(MarkState())
    val markState: StateFlow<MarkState> get() = _markState

    init {
        viewModelScope.launch {
            loadMarks()
        }
    }

    private suspend fun loadMarks() {
        val marks = dayMarkDAO.getAll()
        val sums = MutableList(marks.size) { dayMarkDAO.sumTimeSpentOnDay(marks[it]) }
        _markState.update {
            it.copy(marks = marks, sums = sums)
        }
    }

    fun addNewMark() = viewModelScope.launch {
        val (date, time) = currentDateAndTime()
        dayMarkDAO.insertTime(time, date)
        loadMarks()
    }

    fun deleteDay(date: Date) = viewModelScope.launch {
        dayMarkDAO.deleteDay(date)
        loadMarks()
    }

    fun deleteTime(time: Time, day: Date) = viewModelScope.launch {
        dayMarkDAO.deleteTime(time, day)
        loadMarks()
    }

    fun sumOfDay(date: Date) = viewModelScope.launch {
        dayMarkDAO.sumTimeSpentOnDay(date)
    }
}