package com.example.smartalarm.feature.stopwatch.domain.usecase.impl

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.DeleteStopwatchUseCase
import javax.inject.Inject

/**
 * Implementation of [DeleteStopwatchUseCase] that deletes a stopwatch
 * from the repository using its ID.
 *
 * @param repository The stopwatch repository handling data operations.
 */
class DeleteStopwatchUseCaseImpl @Inject constructor(
    private val repository: StopWatchRepository
) : DeleteStopwatchUseCase {

    override suspend fun invoke(): Result<Unit> {
        val stopwatch = repository.getCurrentStopwatchState()
        return repository.deleteStopwatch(stopwatch.id)
    }
}