package com.example.smartalarm.feature.timer.domain.usecase.impl

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.model.TimerStatus
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.domain.usecase.contract.StartTimerUseCase
import com.example.smartalarm.feature.timer.utility.TimerTimeHelper
import com.example.smartalarm.feature.timer.framework.notification.manager.TimerNotificationManager
import com.example.smartalarm.feature.timer.framework.scheduler.TimerScheduler
import javax.inject.Inject

/**
 * Implementation of [StartTimerUseCase] that starts a timer and persists the updated state.
 *
 * This use case checks if the timer is already running. If not, it calculates an
 * adjusted start time using [TimerTimeHelper], sets the timer state to [TimerStatus.RUNNING],
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
     * Starts a timer, calculates its end time, and registers system alarms.
     * * @return [MyResult.Success] if persisted and scheduled, or [MyResult.Error] with [DataError].
     */
    override suspend fun invoke(timer: TimerModel): MyResult<Unit, DataError> {

        // 1. Validation: Prevent double-starting
        if (timer.isTimerRunning) return MyResult.Success(Unit)

        // 2. State Calculation: Determine exactly when this timer "started"
        val updatedTimer = timer.copy(
            startTime = timerTimeHelper.calculateAdjustedStartTime(timer),
            isTimerRunning = true,
            status = TimerStatus.RUNNING
        )

        // 3. Persistence: Attempt to save to DB first
        return when (val saveResult = timerRepository.persistTimer(updatedTimer)) {
            is MyResult.Success -> {
                // 4. System Side-Effects: Only schedule if the DB save was successful
                val triggerAt = updatedTimer.startTime + updatedTimer.targetTime

                timerScheduler.scheduleTimer(updatedTimer.timerId, triggerAt)
                timerScheduler.scheduleTimerTimeout(updatedTimer.timerId, triggerAt)

                // Clear any "Timer Finished" notifications that might still be showing
                timerNotificationManager.cancelTimerNotification(timer.timerId)

                MyResult.Success(Unit)
            }

            is MyResult.Error -> {
                // Propagate the specific DataError (e.g. Local.DISK_FULL)
                MyResult.Error(saveResult.error)
            }
        }
    }
}