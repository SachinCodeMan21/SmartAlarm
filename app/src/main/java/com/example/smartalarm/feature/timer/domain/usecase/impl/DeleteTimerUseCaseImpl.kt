package com.example.smartalarm.feature.timer.domain.usecase.impl

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.domain.usecase.contract.DeleteTimerUseCase
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

    /**
     * Deletes a timer from storage and clears associated system resources.
     * * @param timer The timer model to be removed.
     * @return [MyResult.Success] if deleted, or [MyResult.Error] with [DataError].
     */
    override suspend fun invoke(timer: TimerModel): MyResult<Unit, DataError> {
        // 1. Attempt to delete from the repository
        val result = repository.deleteTimerById(timer.timerId)

        return when (result) {
            is MyResult.Success -> {
                // 2. Successful deletion: Now safe to clear OS tasks.
                // This prevents "ghost" alarms for a timer that no longer exists in DB.
                timerScheduler.cancelAllScheduledTimers(timer.timerId)
                timerNotificationManager.cancelTimerNotification(timer.timerId)

                MyResult.Success(Unit)
            }
            is MyResult.Error -> {
                // 3. Deletion failed: Pass the DataError (e.g. Local.NOT_FOUND or BUSY)
                // back to the ViewModel to notify the user.
                MyResult.Error(result.error)
            }
        }
    }
}