package com.knightleo.bateponto.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knightleo.bateponto.domain.model.Day
import com.knightleo.bateponto.domain.model.Job
import com.knightleo.bateponto.domain.model.Time
import com.knightleo.bateponto.domain.model.User
import com.knightleo.bateponto.domain.repository.DayMarkRepository
import com.knightleo.bateponto.domain.repository.MarkerWidgetUpdater
import com.knightleo.bateponto.domain.repository.PreferencesRepository
import com.knightleo.bateponto.domain.utils.currentWeekRange
import com.knightleo.bateponto.domain.utils.today
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.OffsetTime

typealias DayWorked = Pair<DayAndDurationWorked, List<OffsetTime>>
typealias DayAndDurationWorked = Pair<Day, Duration>
typealias DaysAndTimesWorked = List<DayWorked>
typealias Week = Pair<Day, Day>

data class MarkState(
    val marks: DaysAndTimesWorked = emptyList(),
    val selectedWeek: Week = Day() to Day(),
    val jobs: List<Job> = emptyList(),
    val selectedJob: Job? = null,
    val user: User? = null,
)

data class ScreenState(
    val loading: Boolean = false,
)

class HomeViewModel(
    private val dayMarkRepository: DayMarkRepository,
    private val preferencesRepository: PreferencesRepository,
    private val widgetUpdater: MarkerWidgetUpdater
) : ViewModel() {

    private val _markState: MutableStateFlow<MarkState> = MutableStateFlow(MarkState())
    val markState: StateFlow<MarkState> get() = _markState
    private val _screenState: MutableStateFlow<ScreenState> = MutableStateFlow(ScreenState())
    val screenState: StateFlow<ScreenState> get() = _screenState

    private val currentJob: Job?
        inline get() = _markState.value.selectedJob

    private val currentUser: User?
        inline get() = _markState.value.user

    init {
        coroutineLaunch {
            val userIdFlow = preferencesRepository.activeUserId
            val jobIdFlow = preferencesRepository.activeJobId
            if (userIdFlow.firstOrNull() == null) {
                val id = dayMarkRepository.createUser(User(0, "Bob"))
                changeUser(id)
            }
            withContext(Dispatchers.IO) {
                launch {
                    userIdFlow.collect {
                        if (it != null) {
                            val user = dayMarkRepository.getUser(it)
                            reLoadJobs()
                            loadMarks()
                            _markState.update { s ->
                                s.copy(user = user)
                            }
                        }
                    }
                }
                jobIdFlow.collect {
                    if (it != null && it != currentJob?.id && currentUser != null) {
                        val job = dayMarkRepository.getJob(it)
                        val jobs = dayMarkRepository.getAllJobs(currentUser!!.id)
                        _markState.update { s ->
                            s.copy(jobs = jobs, selectedJob = job)
                        }
                    }
                }
            }
        }
    }

    private fun coroutineLaunch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _screenState.update {
                    it.copy(loading = true)
                }
                block()
            } catch (e: Exception) {
                Napier.e("Error in coroutineLaunch: ${e.message}", throwable = e)
            } finally {
                _screenState.update {
                    it.copy(loading = false)
                }
            }
        }
    }

    private suspend fun reLoadJobs() {
        if (currentUser == null) return
        val jobs = dayMarkRepository.getAllJobs(currentUser!!.id)
        _markState.update {
            it.copy(jobs = jobs)
        }
    }

    private suspend fun loadMarks(week: Week = currentWeekRange()) {
        if (currentJob == null) return
        val jobId = currentJob!!.id
        val marks = dayMarkRepository.getTimeInDaysBetween(
            jobId,
            week.first,
            week.second
        )
        val sums = MutableList(marks.size) {
            dayMarkRepository.getTimeSpentInDay(
                jobId,
                marks[it].first
            )
        }
        val times = marks.mapIndexed { index, dayMarks ->
            val times = dayMarkRepository.getTimeInDay(jobId, dayMarks.first)
            dayMarks.first to sums[index] to times.map { it.timeStamp }
        }
        _markState.update {
            it.copy(marks = times, selectedWeek = week)
        }
    }

    fun refresh() = coroutineLaunch {
        loadMarks()
    }

    fun addNewMark() = coroutineLaunch {
        dayMarkRepository.insertTimeNow(currentJob!!.id)
        loadMarks()
    }

    fun delete(date: Day?, time: Time? = null) = coroutineLaunch {
        if (date == null) return@coroutineLaunch
        time?.let { deleteTime(date, it) } ?: deleteDay(date)
    }

    private fun deleteDay(date: Day) = coroutineLaunch {
        dayMarkRepository.deleteDay(currentJob!!.id, date)
        loadMarks()
    }

    private fun deleteTime(date: Day, time: Time) = coroutineLaunch {
        if (currentJob == null) return@coroutineLaunch
        dayMarkRepository.deleteTime(currentJob!!.id, date, time)
        loadMarks()
    }

    fun updateTime(
        previousTime: Time,
        newTime: Time,
        date: Day,
    ) = coroutineLaunch {
        if (currentJob == null) return@coroutineLaunch
        dayMarkRepository.updateTime(currentJob!!.id, date, previousTime, newTime)
        loadMarks()
    }

    fun updateWidget() = coroutineLaunch {
        if (currentJob == null) return@coroutineLaunch
        val todayMarks = dayMarkRepository.getTimeInDay(currentJob!!.id, today())
        Napier.i {
            "Updating widget with ${todayMarks.size} marks: $todayMarks"
        }
        widgetUpdater.updateTimeMarks(todayMarks)
        widgetUpdater.updateAll()
    }

    fun changeWeek(week: Week) = coroutineLaunch { loadMarks(week = week) }

    fun changeUser(id: Int) = coroutineLaunch {
        val user = dayMarkRepository.getUser(id)
        _markState.update {
            it.copy(user = user)
        }
        preferencesRepository.setActiveUserId(id)
    }
}