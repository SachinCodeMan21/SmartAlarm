package com.example.smartalarm.feature.timer.domain.facade.contract

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.core.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Facade interface providing unified API for all timer-related use cases.
 *
 * Simplifies interaction by aggregating individual timer use cases
 * into a single entry point.
 */
interface TimerUseCasesFacade {

/*    *//**
     * Retrieves a flow emitting all saved timers.
     *
     * @return A [Flow] that emits lists of [TimerModel].
     *//*
    fun getAllTimers(): Flow<List<TimerModel>>

    *//**
     * Starts the given timer if it is not already running.
     *
     * @param timer The timer to start.
     * @return A [Result] containing the started [TimerModel] or an error.
     *//*
    suspend fun startTimer(timer: TimerModel): Result<TimerModel>

    *//**
     * Pauses the given timer if it is currently running.
     *
     * @param timer The timer to pause.
     * @return A [Result] containing the paused [TimerModel] or an error.
     *//*
    suspend fun pauseTimer(timer: TimerModel): Result<TimerModel>

    *//**
     * Snoozes the given timer by extending its remaining time.
     *
     * @param timer The timer to snooze.
     * @return A [Result] containing the snoozed [TimerModel] or an error.
     *//*
    suspend fun snoozeTimer(timer: TimerModel): Result<TimerModel>

    *//**
     * Restarts the given timer, resetting its remaining time to the target duration.
     *
     * @param timer The timer to restart.
     * @return A [Result] containing the restarted [TimerModel] or an error.
     *//*
    suspend fun restartTimer(timer: TimerModel): Result<TimerModel>

    *//**
     * Saves the given timer to the data source.
     *
     * @param timer The timer to save.
     * @return A [Result] indicating success or containing an error.
     *//*
    suspend fun saveTimer(timer: TimerModel): Result<Unit>

    *//**
     * Deletes the given timer from the data source by its ID.
     *
     * @param timer The timer to delete.
     * @return A [Result] indicating success or containing an error.
     *//*
    suspend fun deleteTimerById(timer: TimerModel): Result<Unit>*/
}