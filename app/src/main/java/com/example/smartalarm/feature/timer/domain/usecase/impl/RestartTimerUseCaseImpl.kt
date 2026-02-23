package com.example.smartalarm.feature.timer.domain.usecase.impl

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.usecase.contract.RestartTimerUseCase
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.timer.domain.model.TimerStatus
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import com.example.smartalarm.feature.timer.framework.scheduler.TimerScheduler
import javax.inject.Inject

/**
 * Implementation of [RestartTimerUseCase] that resets the timer
 * to its original duration and persists the updated state.
 *
 * This use case clears any remaining time, resets the timer state to [TimerStatus.STOPPED],
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
     * Resets a timer to its full duration, stops it, and clears any active alarms.
     * * @param timer The timer model to be reset.
     * @return [MyResult.Success] on completion, or [MyResult.Error] with [DataError].
     */
    override suspend fun invoke(timer: TimerModel): MyResult<Unit, DataError> {
        // 1. Logic: Prepare the "Clean Slate" state
        val restartedTimer = timer.copy(
            startTime = 0L,
            remainingTime = timer.targetTime,
            // targetTime remains the same
            isTimerRunning = false,
            status = TimerStatus.STOPPED
        )

        // 2. Persistence: Save the reset state to local storage
        return when (val saveResult = timerRepository.persistTimer(restartedTimer)) {
            is MyResult.Success -> {
                // 3. Side Effect: Once saved, ensure no alarms trigger for the old timing
                timerScheduler.cancelAllScheduledTimers(timer.timerId)
                MyResult.Success(Unit)
            }
            is MyResult.Error -> {
                // Propagate the specific DataError (e.g., Local.DATABASE)
                MyResult.Error(saveResult.error)
            }
        }
    }
}
