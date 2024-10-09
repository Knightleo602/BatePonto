package com.knightleo.bateponto.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knightleo.bateponto.data.DayMarkDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object State {
    const val LOADING_KEY = "loading"
    const val NO_USER_KEY = "noUser"
}


class MarkerViewModel(
    private val dayMarkDAO: DayMarkDAO
) : ViewModel() {
    private inline fun coroutineLaunch(
        crossinline block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) { block() }
    private var userId = -1

    init {
        coroutineLaunch {
            dayMarkDAO.getUser()?.let {
                userId = it.id
            }
        }
    }

    fun addNewMark() = coroutineLaunch {
        dayMarkDAO.insertCurrentTimeStamp(userId)
    }
}