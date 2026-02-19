package com.example.smartalarm.feature.stopwatch.domain.usecase.contract

/**
 * Use case for updating the in-memory state of the stopwatch with the latest elapsed time and lap information.
 *
 * Typically invoked by the ticker job to periodically update the stopwatch state with the current elapsed time.
 */
interface UpdateStopwatchTickerStateUseCase {

    /**
     * Updates the in-memory stopwatch state with the latest elapsed time and lap data.
     *
     * This method is usually called by the ticker job to update the stopwatch's state in real-time.
     */
    operator fun invoke()
}
