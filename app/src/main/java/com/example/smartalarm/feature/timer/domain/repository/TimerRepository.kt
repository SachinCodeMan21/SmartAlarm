package com.example.smartalarm.feature.timer.domain.repository

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.core.model.Result
import kotlinx.coroutines.flow.StateFlow


/**
 * Interface for managing timer data.
 */
interface TimerRepository {

    /**
     * Observes the current state of the timers.
     * This flow emits changes to the list of timers in memory.
     */
    val timerListState: StateFlow<List<TimerModel>>

    /**
     * Retrieves the current list of timers from memory.
     *
     * @return List of current timers.
     */
    fun getCurrentTimerList(): List<TimerModel>

    /**
     * Updates the in-memory state for all running timers.
     * Typically triggered by a "tick" from a service or background task.
     */
    fun tickAllRunningTimers()

    /**
     * Persists a new timer to the local data source.
     *
     * @param timerModel The timer model to be saved.
     * @return Result indicating success or failure.
     */
    suspend fun persistTimer(timerModel: TimerModel): Result<Unit>

    /**
     * Deletes a timer from the local data source by its ID.
     *
     * @param timerId The ID of the timer to be deleted.
     * @return Result indicating success or failure.
     */
    suspend fun deleteTimerById(timerId: Int): Result<Unit>

}
