package com.example.smartalarm.feature.timer.domain.usecase.impl

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.model.TimerState
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.domain.usecase.contract.StartTimerUseCase
import com.example.smartalarm.feature.timer.utility.TimerTimeHelper
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.timer.framework.notification.manager.TimerNotificationManager
import com.example.smartalarm.feature.timer.framework.scheduler.TimerScheduler
import javax.inject.Inject

/**
 * Implementation of [StartTimerUseCase] that starts a timer and persists the updated state.
 *
 * This use case checks if the timer is already running. If not, it calculates an
 * adjusted start time using [TimerTimeHelper], sets the timer state to [TimerState.RUNNING],
 * and saves the updated timer using [TimerRepository].
 *
 * @property timerTimeHelper Utility to calculate accurate start times.
 * @property timerRepository Repository for persisting timer state.
 * @property timerScheduler Scheduler for managing timer alarms.
 */
class StartTimerUseCaseImpl @Inject constructor(
    private val timerTimeHelper: TimerTimeHelper,
    private val timerRepository: TimerRepository,
    private val timerScheduler: TimerScheduler,
    private val timerNotificationManager: TimerNotificationManager
) : StartTimerUseCase {

    /**
     * Starts the given timer if it's not already running.
     *
     * @param timer The timer to be started.
     * @return A [Result] indicating success or failure of the operation.
     */
    override suspend fun invoke(timer: TimerModel): Result<Unit> {

        // If the timer is already running, no action is needed
        if (timer.isTimerRunning) return Result.Success(Unit)

        // Calculate the adjusted start time
        val updatedTimer = timer.copy(
            startTime = timerTimeHelper.calculateAdjustedStartTime(timer),
            isTimerRunning = true,
            state = TimerState.RUNNING
        )

        // Save the updated timer to the repository
        return when (val saveResult = timerRepository.persistTimer(updatedTimer)) {

            is Result.Success -> {
                // Calculate the expiration time and schedule the timer
                val triggerAt = updatedTimer.startTime + updatedTimer.targetTime
                timerScheduler.scheduleTimer(updatedTimer.timerId, triggerAt)
                timerScheduler.scheduleTimerTimeout(updatedTimer.timerId, triggerAt)
                timerNotificationManager.cancelTimerNotification(timer.timerId)
                Result.Success(Unit) // Operation successful, no need to return updated model
            }

            is Result.Error -> Result.Error(saveResult.exception)
        }
    }
}