package com.example.smartalarm.feature.timer.domain.usecase.impl

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.domain.usecase.contract.DeleteTimerUseCase
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.timer.framework.notification.manager.TimerNotificationManager
import com.example.smartalarm.feature.timer.framework.scheduler.TimerScheduler
import javax.inject.Inject


/**
 * Implementation of [DeleteTimerUseCase] that deletes a timer by ID.
 *
 * Uses [TimerRepository] to remove the timer from the data source.
 *
 * @property repository Repository used to access timer data.
 */
class DeleteTimerUseCaseImpl @Inject constructor(
    private val repository: TimerRepository,
    private val timerScheduler: TimerScheduler,
    private val timerNotificationManager: TimerNotificationManager
) : DeleteTimerUseCase {

    override suspend fun invoke(timer: TimerModel): Result<Unit> {
        // 1. Delete from the repository
        val result = repository.deleteTimerById(timer.timerId)

        return when (result) {
            is Result.Success -> {
                // 2. Clear all scheduled alarms/tasks from the OS
                // This prevents ghost notifications for a deleted timer.
                timerScheduler.cancelAllScheduledTimers(timer.timerId)
                timerNotificationManager.cancelTimerNotification(timer.timerId)
                Result.Success(Unit)
            }
            is Result.Error -> Result.Error(result.exception)
        }
    }
}
