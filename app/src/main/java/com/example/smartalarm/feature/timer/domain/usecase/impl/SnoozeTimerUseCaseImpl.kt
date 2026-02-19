package com.example.smartalarm.feature.timer.domain.usecase.impl

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.model.TimerState
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.domain.usecase.contract.SnoozeTimerUseCase
import com.example.smartalarm.feature.timer.utility.TimerTimeHelper
import java.util.concurrent.TimeUnit
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.timer.framework.scheduler.TimerScheduler
import javax.inject.Inject

/**
 * Implementation of [SnoozeTimerUseCase] that snoozes a paused timer
 * by extending its remaining time and persisting the updated state.
 *
 * A fixed snooze duration is added to the calculated remaining time.
 * The timer is marked as snoozed, paused, and its new snoozed time is saved using [TimerRepository].
 *
 * @property timerTimeHelper Utility for calculating remaining time with snooze logic.
 * @property timerRepository Repository for saving the updated timer.
 * @property timerScheduler Scheduler for managing the timer alarms.
 */
class SnoozeTimerUseCaseImpl @Inject constructor(
    private val timerTimeHelper: TimerTimeHelper,
    private val timerRepository: TimerRepository,
    private val timerScheduler: TimerScheduler
) : SnoozeTimerUseCase {

    /**
     * Snoozes the given timer by extending its remaining time
     * and persisting the changes to the repository.
     *
     * @param timer The timer to snooze.
     * @return A [Result] indicating success or failure of the operation.
     */
    override suspend fun invoke(timer: TimerModel): Result<Unit> {
        // Define the snooze duration (e.g., 1 minute)
        val snoozeDuration = TimeUnit.MINUTES.toMillis(1)
        val remaining = timer.remainingTime + snoozeDuration

        // Create the updated timer with snoozed state
        val updatedTimer = timer.copy(
            startTime = timerTimeHelper.getCurrentTime(),
            isTimerRunning = true,
            isTimerSnoozed = true,
            remainingTime = remaining,
            snoozedTargetTime = remaining,
            state = TimerState.RUNNING
        )

        // Save the updated timer to the repository (in-memory state will be updated)
        return when (val saveResult = timerRepository.persistTimer(updatedTimer)) {
            is Result.Success -> {
                // Clean up any old alarms (to prevent multiple alarms)
                timerScheduler.cancelAllScheduledTimers(timer.timerId)

                // Schedule the new snooze alarm
                timerScheduler.scheduleSnoozeTimer(timer.timerId, snoozeDuration)
                timerScheduler.scheduleTimerTimeout(timer.timerId, remaining)

                Result.Success(Unit) // No need to return the updated model
            }
            is Result.Error -> Result.Error(saveResult.exception)
        }
    }
}