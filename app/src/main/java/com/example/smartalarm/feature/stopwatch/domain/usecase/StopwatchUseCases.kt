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
 * Domain Facade that aggregates all stopwatch-related interactors into a single injection point.
 *
 * This wrapper implements the 'Use Case Container' pattern to mitigate constructor bloat in
 * Presentation layer components (ViewModels, Services). It centralizes the stopwatch
 * domain's capabilities, providing a unified API for consumers while maintaining the
 * granular separation required by Clean Architecture.
 *
 * ### Architectural Benefits:
 * - **Dependency Orchestration:** Simplifies Dagger/Hilt injection by reducing the
 * surface area of the Domain layer.
 * - **Interface Segregation:** Provides a cohesive 'Table of Contents' for feature
 * capabilities, improving discoverability and onboarding for new contributors.
 * - **Refactoring Safety:** Decouples consumer constructors from individual interactor
 * lifecycles; adding or deprecating a Use Case only impacts this aggregate.
 *
 * @property getStopwatch Reactive stream providing real-time state synchronization with persistent storage.
 * @property getCurrentStopwatch One-shot query for the active session snapshot.
 * @property startStopwatch Business logic for session initialization and state transition to ACTIVE.
 * @property pauseStopwatch Business logic for session suspension and persistence of elapsed intervals.
 * @property deleteStopwatch Atomic operation for purging session records and relational lap data.
 * @property lapStopwatch Logic for split-time calculations and lap sequence indexing.
 * @property updateStopwatchTicker Optimized, high-frequency conduit for non-persistent UI updates.
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
