package com.example.smartalarm.feature.timer.data.manager

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import kotlinx.coroutines.flow.StateFlow


interface TimerInMemoryStateManager {

    // Internal state flow representing the list of timers
    val state: StateFlow<List<TimerModel>>

    /**
     * Retrieves the current list of timers.
     */
    fun getCurrentTimers(): List<TimerModel>

    /**
     * Updates the state from the Database.
     * This handles the "Restoration" logic: calculating precise remaining time
     * based on when the timer was paused/started vs now.
     *
     * @param dbTimers The list of [TimerModel] fetched from the LocalDataSource.
     */
    fun updateFromDatabase(dbTimers: List<TimerModel>)

    /**
     * Updates the state from the Ticker (Service).
     * This iterates through the current list and decrements time for running timers.
     */
    fun updateFromTicker()

}