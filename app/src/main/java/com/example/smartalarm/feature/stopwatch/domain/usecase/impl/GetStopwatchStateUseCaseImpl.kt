package com.example.smartalarm.feature.stopwatch.domain.usecase.impl

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.GetStopwatchStateUseCase
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


/**
 * Implementation of [GetStopwatchStateUseCase] that retrieves the current stopwatch state
 * from the [StopWatchRepository].
 */
class GetStopwatchStateUseCaseImpl @Inject constructor(
    private val repository: StopWatchRepository
) : GetStopwatchStateUseCase {

    /**
     * Retrieves the current [StateFlow] of the stopwatch state from the repository.
     * @return A [StateFlow] representing the current [StopwatchModel] state.
     */
    override operator fun invoke(): StateFlow<StopwatchModel> = repository.stopwatchState
}


