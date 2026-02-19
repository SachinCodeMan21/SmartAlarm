package com.example.smartalarm.feature.timer.domain.usecase

import com.example.smartalarm.feature.timer.domain.usecase.contract.DeleteTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.GetAllTimersUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.PauseTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.RestartTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.SnoozeTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.StartTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.TickTimerUsecase
import javax.inject.Inject

/**
 * Data class that bundles all the dependencies needed for the Timer-related use cases.
 *
 * This reduces the need to pass each individual dependency to the ViewModel constructor,
 * making the ViewModel cleaner and easier to maintain.
 */
data class TimerUseCase @Inject constructor(
    val getAllTimers: GetAllTimersUseCase,
    val startTimer: StartTimerUseCase,
    val pauseTimer: PauseTimerUseCase,
    val snoozeTimer: SnoozeTimerUseCase,
    val tickTimerUseCase: TickTimerUsecase,
    val restartTimer: RestartTimerUseCase,
    val deleteTimer: DeleteTimerUseCase
)
