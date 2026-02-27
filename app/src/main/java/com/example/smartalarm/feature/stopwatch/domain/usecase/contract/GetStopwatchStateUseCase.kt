package com.example.smartalarm.feature.stopwatch.domain.usecase.contract

import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import kotlinx.coroutines.flow.StateFlow


/**
 * A use case interface for retrieving the stopwatch state as a [StateFlow].
 */
interface GetStopwatchStateUseCase {

    /**
     * Invokes the use case to get the current [StateFlow] of the stopwatch state.
     * @return A [StateFlow] representing the current [StopwatchModel] state.
     */
    operator fun invoke(): StateFlow<StopwatchModel>
}

