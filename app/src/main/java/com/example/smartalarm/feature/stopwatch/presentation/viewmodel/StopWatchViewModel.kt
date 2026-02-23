package com.example.smartalarm.feature.stopwatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.stopwatch.framework.jobmanager.contract.BlinkEffectJobManager
import com.example.smartalarm.feature.stopwatch.presentation.effect.StopwatchEffect
import com.example.smartalarm.feature.stopwatch.presentation.event.StopwatchEvent
import com.example.smartalarm.feature.stopwatch.presentation.mapper.StopwatchUiMapper
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import com.example.smartalarm.feature.stopwatch.domain.usecase.StopwatchUseCases
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
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
 * @property blinkEffectJobManager Manages the coroutine job that toggles time visibility when paused.
 */
@HiltViewModel
class StopWatchViewModel @Inject constructor(
    private val stopwatchUsecase: StopwatchUseCases,
    private val stopWatchUiMapper: StopwatchUiMapper,
    private val blinkEffectJobManager: BlinkEffectJobManager,
) : ViewModel() {

    /**
     * A [StateFlow] representing the current UI state of the stopwatch.
     * * It observes the domain layer, handles the logic for starting/stopping the
     * blinking animation, and maps the data to a [StopwatchUiModel].
     * * Uses [SharingStarted.WhileSubscribed] to ensure the flow remains active for 5 seconds
     * after the last collector disappears, preventing restarts during configuration changes.
     */
    val uiState: StateFlow<StopwatchUiModel> = stopwatchUsecase.getStopwatch()
        .onEach { model ->
            // Handle side effects separately from data mapping
            updateBlinkingState(model.isRunning, model.elapsedTime)
        }
        .map { model -> stopWatchUiMapper.mapToUiModel(model) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StopwatchUiModel()
        )

    /**
     * A [Channel] for one-time side effects that should not be part of the persistent state.
     * Examples: Starting/Stopping the foreground service or showing a Toast.
     */
    private val _uiEffect = Channel<StopwatchEffect>(Channel.BUFFERED)
    val uiEffect: Flow<StopwatchEffect> = _uiEffect.receiveAsFlow()


    /**
     * Publishes a one-off effect to the [uiEffect] stream.
     */
    private fun postEffect(effect: StopwatchEffect) {
        viewModelScope.launch { _uiEffect.send(effect) }
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
        val result = stopwatchUsecase.startStopwatch()
        if (result is MyResult.Error) {
            postEffect(StopwatchEffect.ShowError(result.error))
        } else {
            postEffect(StopwatchEffect.StartForegroundService)
        }
    }

    /**
     * Pauses the stopwatch via the domain layer.
     */
    private fun pauseStopwatch() = viewModelScope.launch {
        val result = stopwatchUsecase.pauseStopwatch()
        handleErrorResult(result)
    }

    /**
     * Records the current elapsed time as a lap.
     */
    private fun recordStopwatchLap() = viewModelScope.launch {
        val result = stopwatchUsecase.lapStopwatch()
        handleErrorResult(result)
    }

    /**
     * Resets the stopwatch to zero, clears laps, and stops the foreground service.
     */
    private fun resetStopwatch() = viewModelScope.launch {
        val result = stopwatchUsecase.deleteStopwatch()
        postEffect(StopwatchEffect.StopForegroundService)
        handleErrorResult(result)
    }



    //---------------------
    // Blink Job Methods
    //--------------------

    /**
     * Updates the blinking state based on the current stopwatch state.
     *
     * @param isRunning Whether the stopwatch is currently running.
     */
    private fun updateBlinkingState(isRunning: Boolean, elapsedTime: Long) {
        if (!isRunning && elapsedTime > 0) {
            startBlinkingJob()
        } else {
            stopBlinkingJob()
        }
    }


    /**
     * Initiates the blinking visual effect for the "Paused" state.
     */
    private fun startBlinkingJob() {
        blinkEffectJobManager.startBlinking(
            scope = viewModelScope,
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



    //---------------------
    // Helper Method
    // --------------------

    fun getIsStopwatchRunning() : Boolean {
        return stopwatchUsecase.getCurrentStopwatch().isRunning
    }


    /**
     * Helper to handle common result patterns and post errors to UI.
     */
    private fun handleErrorResult(result: MyResult<Unit, DataError>) {
        if (result is MyResult.Error) {
            postEffect(StopwatchEffect.ShowError(result.error))
        }
    }

}