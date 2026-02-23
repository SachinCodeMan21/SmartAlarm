package com.example.smartalarm.feature.stopwatch.domain.usecase.impl

import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopwatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.GetCurrentStopwatchStateUseCase
import javax.inject.Inject

/**
 * Implementation of [GetCurrentStopwatchStateUseCase] that retrieves the current stopwatch state
 * from the [StopwatchRepository].
 */
class GetCurrentStopwatchStateUseCaseImpl @Inject constructor(
    private val repository: StopwatchRepository
) : GetCurrentStopwatchStateUseCase {

    /**
     * Retrieves the current [StopwatchModel] by invoking the repository.
     * @return The current state of the stopwatch as a [StopwatchModel].
     */
    override operator fun invoke(): StopwatchModel = repository.getCurrentStopwatchState()
}
