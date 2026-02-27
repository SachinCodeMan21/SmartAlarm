package com.example.smartalarm.feature.stopwatch.domain.usecase.impl

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.stopwatch.domain.repository.StopwatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.DeleteStopwatchUseCase
import javax.inject.Inject

/**
 * Implementation of [DeleteStopwatchUseCase] that deletes a stopwatch
 * from the repository using its ID.
 *
 * @param repository The stopwatch repository handling data operations.
 */
class DeleteStopwatchUseCaseImpl @Inject constructor(
    private val repository: StopwatchRepository
) : DeleteStopwatchUseCase {

    override suspend fun invoke(): MyResult<Unit, DataError> {
        return repository.deleteStopwatch()
    }
}