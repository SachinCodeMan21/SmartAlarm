package com.example.smartalarm.feature.stopwatch.domain.usecase.contract

import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel

/**
 * A use case interface for retrieving the current state of the stopwatch.
 */
interface GetCurrentStopwatchStateUseCase {

    /**
     * Invokes the use case to get the current [StopwatchModel].
     * @return The current state of the stopwatch as a [StopwatchModel].
     */
    operator fun invoke(): StopwatchModel
}
