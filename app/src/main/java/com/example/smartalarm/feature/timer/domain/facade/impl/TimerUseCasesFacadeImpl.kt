package com.example.smartalarm.feature.timer.domain.facade.impl

import com.example.smartalarm.feature.timer.domain.facade.contract.TimerUseCasesFacade
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.domain.usecase.contract.DeleteTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.GetAllTimersUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.PauseTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.RestartTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.SaveTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.SnoozeTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.StartTimerUseCase
import com.example.smartalarm.core.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


/**
 * Implementation of [TimerUseCasesFacade] that delegates
 * calls to individual timer use case implementations.
 *
 * Provides a consistent API surface, forwarding calls to
 * corresponding use cases which all return [Result] types.
 *
 * @param getAll Use case to retrieve all timers.
 * @param start Use case to start a timer.
 * @param pause Use case to pause a timer.
 * @param snooze Use case to snooze a timer.
 * @param restart Use case to restart a timer.
 * @param save Use case to save a timer.
 * @param delete Use case to delete a timer.
 */
class TimerUseCasesFacadeImpl @Inject constructor(
    private val getAll: GetAllTimersUseCase,
    private val start: StartTimerUseCase,
    private val pause: PauseTimerUseCase,
    private val snooze: SnoozeTimerUseCase,
    private val restart: RestartTimerUseCase,
    private val save: SaveTimerUseCase,
    private val delete: DeleteTimerUseCase,
) : TimerUseCasesFacade {

/*    override fun getAllTimers(): Flow<List<TimerModel>> = getAll()

    override suspend fun startTimer(timer: TimerModel): Result<TimerModel> = start(timer)

    override suspend fun pauseTimer(timer: TimerModel): Result<TimerModel> = pause(timer)

    override suspend fun snoozeTimer(timer: TimerModel): Result<TimerModel> = snooze(timer)

    override suspend fun restartTimer(timer: TimerModel): Result<TimerModel> = restart(timer)

    override suspend fun saveTimer(timer: TimerModel): Result<Unit> = save(timer)

    override suspend fun deleteTimerById(timer: TimerModel): Result<Unit> = delete(timer)*/
}
