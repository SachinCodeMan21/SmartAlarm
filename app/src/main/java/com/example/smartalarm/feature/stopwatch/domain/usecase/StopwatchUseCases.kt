package com.example.smartalarm.feature.stopwatch.domain.usecase

import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.DeleteStopwatchUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.GetCurrentStopwatchStateUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.GetStopwatchStateUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.LapStopwatchUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.PauseStopwatchUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.StartStopwatchUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.UpdateStopwatchTickerStateUseCase
import javax.inject.Inject

/**
 * A domain-level container that bundles all stopwatch-related use cases into a single injection point.
 *
 * ### Why this exists:
 * In Clean Architecture, as a feature grows, the number of individual use cases can lead to
 * "Constructor Bloat" in ViewModels and Services. This wrapper simplifies dependency
 * management by providing a unified API for the Stopwatch domain.
 *
 * ### Key Benefits:
 * - **Maintainability:** Adding or removing a use case only requires a change in this class
 * rather than updating every consumer's constructor.
 * - **Discoverability:** Provides a "Table of Contents" for all actions possible within
 * the stopwatch feature, making the code easier for new developers to navigate.
 * - **Clean Code:** Keeps ViewModels focused on UI state by reducing the boilerplate
 * required for dependency injection.
 *
 * ### Usage:
 * ```
 * class StopwatchViewModel @Inject constructor(
 * private val stopwatchUseCases: StopwatchUseCases
 * ) : ViewModel() {
 * fun start() = viewModelScope.launch { stopwatchUseCases.startStopwatch() }
 * }
 * ```
 *
 * @property getStopwatch Provides a reactive stream of the stopwatch state from the database.
 * @property getCurrentStopwatch Provides a one-shot snapshot of the current state.
 * @property startStopwatch Logic for transitioning the stopwatch to a running state.
 * @property pauseStopwatch Logic for halting the timer while preserving elapsed time.
 * @property deleteStopwatch Logic for purging the stopwatch session and associated laps.
 * @property lapStopwatch Logic for recording lap splits and calculating lap indices.
 * @property updateStopwatchTicker High-frequency logic for updating the in-memory timer UI.
 */
data class StopwatchUseCases @Inject constructor(
    val getStopwatch: GetStopwatchStateUseCase,
    val getCurrentStopwatch: GetCurrentStopwatchStateUseCase,
    val startStopwatch: StartStopwatchUseCase,
    val pauseStopwatch: PauseStopwatchUseCase,
    val deleteStopwatch: DeleteStopwatchUseCase,
    val lapStopwatch: LapStopwatchUseCase,
    val updateStopwatchTicker: UpdateStopwatchTickerStateUseCase
)

