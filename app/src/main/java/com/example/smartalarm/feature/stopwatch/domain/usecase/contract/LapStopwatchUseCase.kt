package com.example.smartalarm.feature.stopwatch.domain.usecase.contract

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel

/**
 * Use case for recording a lap on the stopwatch.
 *
 * This interface encapsulates the logic of adding a lap to the stopwatch and returning the updated state.
 */
fun interface LapStopwatchUseCase {

    /**
     * Records a lap and returns the updated stopwatch model wrapped in a [MyResult].
     *
     * @return A [Result] indicating success or failure, with no result value (Unit) for success.
     */
    suspend operator fun invoke(): MyResult<Unit, DataError>
}
