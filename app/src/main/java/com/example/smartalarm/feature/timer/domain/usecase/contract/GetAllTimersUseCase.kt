package com.example.smartalarm.feature.timer.domain.usecase.contract

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Use case for retrieving all timers as a reactive flow.
 */
fun interface GetAllTimersUseCase {
    /**
     * Returns a [StateFlow] of the list of all [TimerModel]s.
     *
     * @return A reactive stream of timers.
     */
    operator fun invoke(): StateFlow<List<TimerModel>>
}