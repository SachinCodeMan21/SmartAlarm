package com.example.smartalarm.feature.stopwatch.domain.usecase.contract

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel

fun interface DeleteStopwatchUseCase {

    /**
     * Deletes the specified stopwatch from persistence.
     *
     * @return [Result] indicating success or failure.
     */
    suspend operator fun invoke(): Result<Unit>
}
