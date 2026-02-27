package com.example.smartalarm.feature.stopwatch.domain.usecase.contract

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult

/**
 * Use case for recording a lap on the stopwatch.
 *
 * This interface encapsulates the logic of adding a lap to the stopwatch and returning the updated state.
 */
fun interface LapStopwatchUseCase {

    /**
     * Records a lap and returns the updated stopwatch model wrapped in a [MyResult].
     *
     * @return A [MyResult] indicating success or failure, with no result value (Unit) for success.
     */
    suspend operator fun invoke(): MyResult<Unit, DataError>
}
