package com.example.smartalarm.feature.stopwatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.feature.stopwatch.framework.jobmanager.contract.BlinkEffectJobManager
import com.example.smartalarm.feature.stopwatch.presentation.effect.StopwatchEffect
import com.example.smartalarm.feature.stopwatch.presentation.event.StopwatchEvent
import com.example.smartalarm.feature.stopwatch.presentation.mapper.StopwatchUiMapper
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchUiModel
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import com.example.smartalarm.core.di.annotations.DefaultDispatcher
import com.example.smartalarm.feature.stopwatch.domain.usecase.StopwatchUseCases
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject


/**
 * ViewModel responsible for managing stopwatch state and coordinating UI behavior.
 *
 * Acts as the **single source of truth** for the stopwatch feature in an MVVM architecture.
 * It processes user-driven [StopwatchEvent]s, manages background jobs, and exposes
 * lifecycle-safe streams of UI state and one-off UI effects.
 *
 * @property stopwatchUsecase Encapsulates domain operations (start, pause, lap, delete).
 * @property stopWatchUiMapper Converts domain models into UI-friendly models (e.g., formatting time strings).
 * @property resourceProvider Helper to fetch localized strings or system resources.
 * @property blinkEffectJobManager Manages the coroutine job that toggles time visibility when paused.
 * @property defaultDispatcher The coroutine dispatcher used for non-UI intensive background tasks.
 */
@HiltViewModel
class StopWatchViewModel @Inject constructor(
    private val stopwatchUsecase: StopwatchUseCases,
    private val stopWatchUiMapper: StopwatchUiMapper,
    private val resourceProvider: ResourceProvider,
    private val blinkEffectJobManager: BlinkEffectJobManager,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    /**
     * A [StateFlow] representing the current UI state of the stopwatch.
     * * It observes the domain layer, handles the logic for starting/stopping the
     * blinking animation, and maps the data to a [StopwatchUiModel].
     * * Uses [SharingStarted.WhileSubscribed] to ensure the flow remains active for 5 seconds
     * after the last collector disappears, preventing restarts during configuration changes.
     */
    val uiState: StateFlow<StopwatchUiModel> = stopwatchUsecase.getStopwatch()
        .map { model ->
            // Logic: Blink if paused with time on the clock, otherwise stay solid.
            if (!model.isRunning && model.elapsedTime > 0) {
                startBlinkingJob()
            } else {
                stopBlinkingJob()
            }
            stopWatchUiMapper.mapToUiModel(model)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StopwatchUiModel()
        )

    /**
     * A [SharedFlow] for one-time side effects that should not be part of the persistent state.
     * Examples: Starting/Stopping the foreground service or showing a Toast.
     */
    private val _uiEffect = MutableSharedFlow<StopwatchEffect>(0)
    val uiEffect: Flow<StopwatchEffect> = _uiEffect.asSharedFlow()


    /**
     * Publishes a one-off effect to the [uiEffect] stream.
     */
    private fun postEffect(effect: StopwatchEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }



    //------------------
    // Handle UI Events
    //------------------
    /**
     * Entry point for all UI interactions.
     * Maps incoming [StopwatchEvent]s to specific internal business logic.
     */
    fun handleEvent(event: StopwatchEvent) {
        when (event) {
            StopwatchEvent.ToggleRunState -> toggleRunState()
            StopwatchEvent.ResetStopwatch -> resetStopwatch()
            StopwatchEvent.RecordStopwatchLap -> recordStopwatchLap()
            StopwatchEvent.MoveToBackground -> stopBlinkingJob()
        }
    }



    //-------------------------
    // Stopwatch Action Methods
    //-------------------------
    /**
     * Switches the stopwatch between Running and Paused states.
     */
    private fun toggleRunState() {
        val state = stopwatchUsecase.getCurrentStopwatch()
        if (state.isRunning) pauseStopwatch() else startStopwatch()
    }

    /**
     * Starts the stopwatch and triggers the foreground service effect to ensure
     * persistence when the UI is hidden.
     */
    private fun startStopwatch() = viewModelScope.launch {
        stopwatchUsecase.startStopwatch()
        postEffect(StopwatchEffect.StartForegroundService)
    }

    /**
     * Pauses the stopwatch via the domain layer.
     */
    private fun pauseStopwatch() = viewModelScope.launch {
        stopwatchUsecase.pauseStopwatch()
    }

    /**
     * Records the current elapsed time as a lap.
     */
    private fun recordStopwatchLap() = viewModelScope.launch {
        stopwatchUsecase.lapStopwatch()
    }

    /**
     * Resets the stopwatch to zero, clears laps, and stops the foreground service.
     */
    private fun resetStopwatch() = viewModelScope.launch {
        stopwatchUsecase.deleteStopwatch()
        postEffect(StopwatchEffect.StopForegroundService)
    }


    //---------------------
    // Blink Job Methods
    //--------------------
    /**
     * Initiates the blinking visual effect for the "Paused" state.
     * Runs on the [defaultDispatcher] to keep the main thread clear.
     */
    private fun startBlinkingJob() = viewModelScope.launch(defaultDispatcher) {
        blinkEffectJobManager.startBlinking(
            scope = this,
            onVisibilityChanged = { postEffect(StopwatchEffect.BlinkVisibilityChanged(it)) }
        )
    }

    /**
     * Stops the blinking visual effect and ensures the UI is reset to visible.
     */
    private fun stopBlinkingJob() {
        blinkEffectJobManager.stopBlinking()
        postEffect(StopwatchEffect.BlinkVisibilityChanged(true))
    }
}