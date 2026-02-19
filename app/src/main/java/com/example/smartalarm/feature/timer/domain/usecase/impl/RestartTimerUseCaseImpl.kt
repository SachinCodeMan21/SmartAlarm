package com.example.smartalarm.feature.timer.domain.usecase.impl

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.usecase.contract.RestartTimerUseCase
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.timer.domain.model.TimerState
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.framework.scheduler.TimerScheduler
import javax.inject.Inject

/**
 * Implementation of [RestartTimerUseCase] that resets the timer
 * to its original duration and persists the updated state.
 *
 * This use case clears any remaining time, resets the timer state to [TimerState.STOPPED],
 * and saves the new state using [TimerRepository]. It retains the original timer ID and target time.
 *
 * @property timerRepository Repository used to persist the restarted timer.
 * @property timerScheduler Scheduler used to manage timer cancellations.
 */
class RestartTimerUseCaseImpl @Inject constructor(
    private val timerRepository: TimerRepository,
    private val timerScheduler: TimerScheduler
) : RestartTimerUseCase {

    /**
     * Restarts the timer by resetting it to the target duration and saving the updated model.
     *
     * @param timer The timer to restart.
     * @return A [Result] indicating success or failure of the operation.
     */
    override suspend fun invoke(timer: TimerModel): Result<Unit> {
        // Create a restarted timer with the target time and reset state
        val restartedTimer = TimerModel(
            timerId = timer.timerId,
            startTime = 0L,
            remainingTime = timer.targetTime,
            targetTime = timer.targetTime,
            isTimerRunning = false,
            state = TimerState.STOPPED
        )

        // Save the restarted timer to the repository
        return when (val saveResult = timerRepository.persistTimer(restartedTimer)) {
            is Result.Success -> {
                // Cancel all scheduled timers (alarms, snooze, etc.)
                timerScheduler.cancelAllScheduledTimers(timer.timerId)
                Result.Success(Unit) // No need to return the updated model
            }
            is Result.Error -> Result.Error(saveResult.exception)
        }
    }
}
