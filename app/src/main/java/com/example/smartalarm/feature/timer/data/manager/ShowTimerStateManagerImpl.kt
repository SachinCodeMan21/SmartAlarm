package com.example.smartalarm.feature.timer.data.manager

import com.example.smartalarm.core.di.annotations.ApplicationScope
import com.example.smartalarm.feature.timer.domain.manager.ShowTimerStateManager
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.model.TimerState
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.utility.TimerTimeHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowTimerStateManagerImpl @Inject constructor(
    private val repository: TimerRepository, // Access to the "Permanent" Truth
    private val timerTimeHelper: TimerTimeHelper,
    @param:ApplicationScope private val externalScope: CoroutineScope // App-wide lifecycle
) : ShowTimerStateManager {

    // 1. Internal Mutable State: The "Live Feed" that everyone observes
    private val _timers = MutableStateFlow<List<TimerModel>>(emptyList())
    override fun getTimersFlow(): StateFlow<List<TimerModel>> = _timers.asStateFlow()

    init {
        // 2. AUTOMATIC SYNC: Observe the Database 24/7
        // Whenever a timer is added, deleted, or paused in the DB, this triggers.
        externalScope.launch {
//            repository.getTimerList().collect { databaseTimers ->
//                restoreTimers(databaseTimers)
//            }
        }
    }

    // 3. RESTORATION: Calculates the "Live" math when DB data arrives
    override fun restoreTimers(timers: List<TimerModel>) {
        val updatedTimers = timers.map { timer ->
            if (timer.isTimerRunning) {
                timer.copy(
                    remainingTime = timerTimeHelper.calculatePreciseRemainingTime(timer)
                )
            }
            else {
                timer
            }
        }
        _timers.update { updatedTimers }
    }

    // 4. TICKING: Called by the SERVICE every second
    override fun tickAllRunningTimers() {
        _timers.update { currentList ->
            currentList.map { timer ->
                if (timer.isTimerRunning) {
                    timer.copy(
                        remainingTime = timerTimeHelper.getRemainingTimeConsideringSnooze(timer)
                    )
                } else {
                    timer
                }
            }
        }
    }


    // --- Helper Methods (Reading from the Live Feed) ---

    override fun getTimers(): List<TimerModel> = _timers.value

    override fun getTimer(timerId: Int): TimerModel =
        _timers.value.first { it.timerId == timerId }


    override fun hasRunningTimers(): Boolean {
        return _timers.value.any { it.isTimerRunning && it.remainingTime > 0 }
    }
    override fun hasActiveTimers(): Boolean = _timers.value
        .any { it.remainingTime > 0 && (it.state == TimerState.RUNNING || it.state == TimerState.PAUSED) }
    override fun hasCompletedTimers(): Boolean = _timers.value
        .any { it.remainingTime <= 0 && (it.state == TimerState.RUNNING || it.state == TimerState.PAUSED) }


    override fun hasRunningActiveTimers(): Boolean = _timers.value
        .any { it.isTimerRunning && it.remainingTime > 0 }
    override fun hasRunningCompletedTimers(): Boolean = _timers.value
        .any { it.isTimerRunning && it.remainingTime <= 0 }

    override fun getActiveTimers(): List<TimerModel> =
        _timers.value
            .filter { it.remainingTime > 0 && (it.state == TimerState.RUNNING || it.state == TimerState.PAUSED) }
            .sortedWith(compareBy<TimerModel> { it.state != TimerState.RUNNING }.thenBy { it.remainingTime })
    override fun getCompletedTimers(): List<TimerModel> =
        _timers.value
            .filter { it.remainingTime <= 0 && (it.state == TimerState.RUNNING || it.state == TimerState.PAUSED) }
            .sortedBy { it.remainingTime }

    // Running Timer Helpers
    override fun getRunningActiveTimers(): List<TimerModel> = _timers.value
        .filter { it.isTimerRunning && it.remainingTime > 0 }
        .sortedBy { it.remainingTime }
    override fun getRunningCompletedTimers(): List<TimerModel> = _timers.value
        .filter { it.isTimerRunning && it.remainingTime <= 0 }
        .sortedBy { it.remainingTime }


}


/*
class ShowTimerStateManagerImpl @Inject constructor(
    private val timerTimeHelper: TimerTimeHelper
) : ShowTimerStateManager {

    // Internal mutable state, holding the list of timers in a thread-safe manner
    private val _timers = MutableStateFlow<List<TimerModel>>(emptyList())

    override fun getTimers(): List<TimerModel> = _timers.value

    override fun getTimer(timerId : Int) : TimerModel = _timers.value.first { it.timerId == timerId }

    override fun getTimersFlow(): StateFlow<List<TimerModel>> = _timers.asStateFlow()

    override fun restoreTimers(timers: List<TimerModel>) {
        val updatedTimers = timers.map { timer ->
            if (timer.isTimerRunning) {
                timer.copy(
                    remainingTime = timerTimeHelper.calculatePreciseRemainingTime(timer),
                    isTimerRunning = true,
                )
            } else {
                timer
            }
        }
        _timers.update { updatedTimers }
    }

    override fun updateTimer(timer: TimerModel) {
        _timers.update { currentList ->
            val index = currentList.indexOfFirst { it.timerId == timer.timerId }
            if (index != -1) {
                currentList.toMutableList().apply { this[index] = timer }
            } else {
                currentList + timer
            }
        }
    }

    override fun removeTimer(timerId: Int) {
        _timers.update { current -> current.filterNot { it.timerId == timerId } }
    }

    override fun clearTimers() {
        _timers.update { emptyList() }
    }

    override fun tickTimer(timerId: Int) {

        _timers.update { currentList ->

            val timer = currentList.find { it.timerId == timerId } ?: return@update currentList

            if (!timer.isTimerRunning) return@update currentList

            val updatedTimer = timer.copy(
                remainingTime = timerTimeHelper.getRemainingTimeConsideringSnooze(timer)
            )

            val index = currentList.indexOfFirst { it.timerId == timerId }
            if (index != -1) {
                currentList.toMutableList().apply { this[index] = updatedTimer }
            } else {
                currentList
            }
        }
    }

    override fun tickAllRunningTimers() {
        _timers.update { currentList ->
            currentList.map { timer ->
                if (timer.isTimerRunning) {
                    timer.copy(
                        remainingTime = timerTimeHelper.getRemainingTimeConsideringSnooze(timer),
                    )
                } else {
                    timer
                }
            }
        }
    }

    override fun hasRunningTimers(): Boolean {
        return _timers.value.any { it.isTimerRunning && it.remainingTime > 0 }
    }
    override fun hasActiveTimers(): Boolean = _timers.value
        .any { it.remainingTime > 0 && (it.state == TimerState.RUNNING || it.state == TimerState.PAUSED) }

    override fun hasCompletedTimers(): Boolean = _timers.value
        .any { it.remainingTime <= 0 && (it.state == TimerState.RUNNING || it.state == TimerState.PAUSED) }
    override fun hasRunningActiveTimers(): Boolean = _timers.value
        .any { it.isTimerRunning && it.remainingTime > 0 }

    override fun hasRunningCompletedTimers(): Boolean = _timers.value
        .any { it.isTimerRunning && it.remainingTime <= 0 }
    override fun getActiveTimers(): List<TimerModel> =
        _timers.value
            .filter { it.remainingTime > 0 && (it.state == TimerState.RUNNING || it.state == TimerState.PAUSED) }
            .sortedWith(compareBy<TimerModel> { it.state != TimerState.RUNNING }.thenBy { it.remainingTime })

    override fun getCompletedTimers(): List<TimerModel> =
        _timers.value
            .filter { it.remainingTime <= 0 && (it.state == TimerState.RUNNING || it.state == TimerState.PAUSED) }
            .sortedBy { it.remainingTime }

    override fun getRunningActiveTimers(): List<TimerModel> = _timers.value
            .filter { it.isTimerRunning && it.remainingTime > 0 }
            .sortedBy { it.remainingTime }

    override fun getRunningCompletedTimers(): List<TimerModel> = _timers.value
            .filter { it.isTimerRunning && it.remainingTime <= 0 }
            .sortedBy { it.remainingTime }


}*/
