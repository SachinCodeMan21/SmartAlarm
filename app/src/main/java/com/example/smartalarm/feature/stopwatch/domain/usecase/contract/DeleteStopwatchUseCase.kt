package com.example.smartalarm.feature.stopwatch.domain.usecase.contract

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult

fun interface DeleteStopwatchUseCase {

    /**
     * Deletes the specified stopwatch from persistence.
     *
     * @return [MyResult] indicating success or failure.
     */
    suspend operator fun invoke(): MyResult<Unit, DataError>
}
