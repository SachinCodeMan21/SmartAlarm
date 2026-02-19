package com.example.smartalarm.feature.timer.domain.usecase.impl

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.model.TimerState
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.domain.usecase.contract.PauseTimerUseCase
import com.example.smartalarm.feature.timer.utility.TimerTimeHelper
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.timer.framework.scheduler.TimerScheduler
import javax.inject.Inject

/**
 * Implementation of [PauseTimerUseCase] that pauses a running timer and persists the update.
 *
 * If the timer is currently running, it calculates the remaining time using [TimerTimeHelper],
 * updates the timer state to [TimerState.PAUSED], and saves the updated timer via [TimerRepository].
 *
 * If the timer is not running, it is returned as-is.
 * If saving fails, the error is returned via [Result.Error].
 *
 * @property timerTimeHelper Utility to calculate current time and remaining duration.
 * @property timerRepository Repository used to persist the timer state.
 * @property timerScheduler Scheduler used to manage timer cancellations.
 */
class PauseTimerUseCaseImpl @Inject constructor(
    private val timerTimeHelper: TimerTimeHelper,
    private val timerRepository: TimerRepository,
    private val timerScheduler: TimerScheduler
) : PauseTimerUseCase {

    /**
     * Pauses the given timer if it's currently running.
     *
     * @param timer The timer to pause.
     * @return A [Result] indicating success or failure.
     */
    override suspend fun invoke(timer: TimerModel): Result<Unit> {
        // Check if the timer is running. If it's not, no changes are made.
        if (!timer.isTimerRunning) return Result.Success(Unit)

        // Calculate the updated timer values
        val updatedTimer = timer.copy(
            endTime = timerTimeHelper.getCurrentTime(),
            remainingTime = timerTimeHelper.getRemainingTimeConsideringSnooze(timer),
            isTimerRunning = false,
            state = TimerState.PAUSED
        )

        // Save the updated timer to the repository
        return when (val saveResult = timerRepository.persistTimer(updatedTimer)) {
            is Result.Success -> {
                // Cancel any scheduled actions for the timer
                timerScheduler.cancelAllScheduledTimers(timer.timerId)
                Result.Success(Unit) // No need to return the updated model; state is handled in-memory
            }
            is Result.Error -> Result.Error(saveResult.exception)
        }
    }
}
