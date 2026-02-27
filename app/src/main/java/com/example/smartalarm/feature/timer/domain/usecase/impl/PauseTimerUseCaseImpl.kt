package com.example.smartalarm.feature.timer.domain.usecase.impl

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.model.TimerStatus
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.domain.usecase.contract.PauseTimerUseCase
import com.example.smartalarm.feature.timer.utility.TimerTimeHelper
import com.example.smartalarm.feature.timer.framework.scheduler.TimerScheduler
import javax.inject.Inject

/**
 * Implementation of [PauseTimerUseCase] that pauses a running timer and persists the update.
 *
 * If the timer is currently running, it calculates the remaining time using [TimerTimeHelper],
 * updates the timer state to [TimerStatus.PAUSED], and saves the updated timer via [TimerRepository].
 *
 * If the timer is not running, it is returned as-is.
 * If saving fails, the error is returned via [MyResult.Error].
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
     * Pauses the given timer, cancels the system alarm, and persists the state.
     * * @return [MyResult.Success] if the operation completed, or [MyResult.Error] with [DataError].
     */
    override suspend fun invoke(timer: TimerModel): MyResult<Unit, DataError> {
        // 1. Validation: If it's not running, we consider the "action" a success (no-op)
        if (!timer.isTimerRunning) return MyResult.Success(Unit)

        // 2. Business Logic: Calculate the pause state
        val updatedTimer = timer.copy(
            endTime = timerTimeHelper.getCurrentTime(),
            remainingTime = timerTimeHelper.getRemainingTimeConsideringSnooze(timer),
            isTimerRunning = false,
            status = TimerStatus.PAUSED
        )

        // 3. Execution: Persist and handle side effects
        return when (val saveResult = timerRepository.persistTimer(updatedTimer)) {

            is MyResult.Success -> {
                // Side Effect: Remove all the scheduled alarm for this timer from the Android System AlarmManager
                timerScheduler.cancelAllScheduledTimers(timer.timerId)
                MyResult.Success(Unit)
            }
            is MyResult.Error -> {
                // Propagate the specific DataError (e.g., Local.DISK_FULL) back to the ViewModel
                MyResult.Error(saveResult.error)
            }
        }
    }
}