package com.example.smartalarm.feature.timer.data.manager

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.model.TimerState
import com.example.smartalarm.feature.timer.utility.TimerTimeHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [TimerInMemoryStateManager] that manages the list of timers in memory.
 * This is the "Single Source of Truth" for the UI and Service.
 */
@Singleton
class TimerInMemoryStateManagerImpl @Inject constructor(
    private val timerTimeHelper: TimerTimeHelper
) : TimerInMemoryStateManager
{

    // Internal state flow representing the list of timers
    private val _timers = MutableStateFlow<List<TimerModel>>(emptyList())

    override val state: StateFlow<List<TimerModel>> = _timers.asStateFlow()

    /**
     * Retrieves the current list of timers.
     */
    override fun getCurrentTimers(): List<TimerModel> = _timers.value

    /**
     * Updates the state from the Database.
     * This handles the "Restoration" logic: calculating precise remaining time
     * based on when the timer was paused/started vs now.
     *
     * @param dbTimers The list of [TimerModel] fetched from the LocalDataSource.
     */
    override fun updateFromDatabase(dbTimers: List<TimerModel>) {
        // Calculate the "Live" math when DB data arrives
        val updatedTimers = dbTimers.map { timer ->
            if (timer.isTimerRunning) {
                timer.copy(
                    remainingTime = timerTimeHelper.calculatePreciseRemainingTime(timer)
                )
            } else {
                timer
            }
        }
        _timers.value = updatedTimers
    }

    /**
     * Updates the state from the Ticker (Service).
     * This iterates through the current list and decrements time for running timers.
     */
    override fun updateFromTicker() {
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

    override fun getTimer(timerId: Int): TimerModel? =
        _timers.value.find { it.timerId == timerId }

    override fun hasRunningTimers(): Boolean {
        return _timers.value.any { it.isTimerRunning && it.remainingTime > 0 }
    }

    override fun getActiveTimers(): List<TimerModel> =
        _timers.value
            .filter { it.remainingTime > 0 && (it.state == TimerState.RUNNING || it.state == TimerState.PAUSED) }
            .sortedWith(compareBy<TimerModel> { it.state != TimerState.RUNNING }.thenBy { it.remainingTime })

    override fun getCompletedTimers(): List<TimerModel> =
        _timers.value
            .filter { it.remainingTime <= 0 && (it.state == TimerState.RUNNING || it.state == TimerState.PAUSED) }
            .sortedBy { it.remainingTime }
}