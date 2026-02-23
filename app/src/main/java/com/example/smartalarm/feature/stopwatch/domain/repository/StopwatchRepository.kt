package com.example.smartalarm.feature.stopwatch.domain.repository

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.stopwatch.data.manager.StopwatchInMemoryStateManager
import com.example.smartalarm.feature.stopwatch.data.datasource.contract.StopwatchLocalDataSource
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import kotlinx.coroutines.flow.StateFlow

/**
 * Gateway for all stopwatch-related data operations, coordinating between
 * high-speed in-memory state and persistent local storage.
 *
 * ### Architectural Role:
 * This interface acts as the "Single Point of Contact" for the Domain/UI layers.
 * It abstracts the decision-making process of when to use the [StopwatchInMemoryStateManager]
 * for performance and when to use the [StopwatchLocalDataSource] for reliability.
 */
interface StopwatchRepository {

    /**
     * A reactive stream of the current stopwatch session.
     * * ViewModels and other observers should collect from this Flow to stay
     * synchronized with the most recent state, regardless of whether the
     * update came from a ticker or the database.
     */
    val stopwatchState: StateFlow<StopwatchModel>

    /**
     * Retrieves the current snapshot of the stopwatch session.
     * * Use this for immediate business logic checks where a reactive
     * stream is not required.
     */
    fun getCurrentStopwatchState(): StopwatchModel

    /**
     * Dispatches high-frequency, transient updates to the in-memory state.
     * * ### Purpose:
     * Primarily called by the timer engine/ticker to provide real-time
     * increments. This bypasses the database to ensure high performance
     * and smooth UI updates without disk I/O bottlenecks.
     */
    fun updateTickerState(updatedStopwatch: StopwatchModel)

    /**
     * Commits the current stopwatch session and its associated laps to
     * persistent storage.
     * * ### Why:
     * Use this when the stopwatch state changes significantly (e.g., Paused,
     * Lap Created, or Reset) to ensure data survives app process death or
     * system reboots.
     * * @param stopwatchModel The pure domain model to be mapped and saved.
     * @return A [MyResult] indicating if the persistence operation succeeded.
     */
    suspend fun persistStopwatch(stopwatchModel: StopwatchModel): MyResult<Unit, DataError>

    /**
     * Purges the active stopwatch session from the local database.
     * * Use this during a full reset or when the user clears their history.
     * * @return A [MyResult] indicating the success of the deletion.
     */
    suspend fun deleteStopwatch(): MyResult<Unit, DataError>
}