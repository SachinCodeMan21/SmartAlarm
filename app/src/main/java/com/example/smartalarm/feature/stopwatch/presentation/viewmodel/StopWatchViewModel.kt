package com.example.smartalarm.feature.stopwatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.R
import com.example.smartalarm.core.framework.analytics.AnalyticsHelper
import com.example.smartalarm.core.framework.analytics.ErrorLogger
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.framework.jobmanager.contract.BlinkEffectJobManager
import com.example.smartalarm.feature.stopwatch.presentation.effect.StopwatchEffect
import com.example.smartalarm.feature.stopwatch.presentation.event.StopwatchEvent
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import com.example.smartalarm.feature.stopwatch.domain.usecase.StopwatchUseCases
import com.example.smartalarm.feature.stopwatch.presentation.mapper.toUiModel
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchAnalyticsEvent
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
 * State Orchestrator for the Stopwatch feature.
 *
 * This ViewModel serves as the central hub of the feature's **Unidirectional Data Flow (UDF)**.
 * It encapsulates the transformation of domain-level signals into reactive UI states
 * while managing transient side effects that fall outside the persistent state scope.
 *
 * ### Responsibilities:
 * - **State Projection**: Converts raw domain models into [StopwatchUiModel] snapshots.
 * - **Lifecycle Persistence**: Utilizes [SharingStarted.WhileSubscribed] to bridge
 * configuration changes (rotations) without state loss.
 * - **Side-Effect Coordination**: Manages high-frequency UI tasks (blinking) and
 * system-level integrations (Foreground Services).
 * - **Domain Delegation**: Routes UI events to specialized [StopwatchUseCases].
 */
@HiltViewModel
class StopWatchViewModel @Inject constructor(
    private val stopwatchUsecase: StopwatchUseCases,
    private val blinkEffectJobManager: BlinkEffectJobManager,
    private val resourceProvider: ResourceProvider,
    private val errorLogger: ErrorLogger,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    /**
     * Authority of truth for the View layer.
     * * Projects a cold domain Flow into a hot [StateFlow]. It applies a 5000ms
     * keep-alive timeout to maintain continuity during Fragment/Activity
     * recreation, ensuring a seamless user experience.
     */
    val uiState: StateFlow<StopwatchUiModel> = stopwatchUsecase.getStopwatch()
        .onEach { model ->
            // Handle side effects separately from data mapping
            updateBlinkingState(model.isRunning, model.elapsedTime)
        }
        .map { model -> model.toUiModel() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StopwatchModel().toUiModel()
        )

    /**
     * Transient effect stream for one-off UI events.
     * * Uses a Buffered [Channel] to ensure that navigation commands, service
     * triggers, and user-facing error messages are delivered exactly once.
     */
    private val _uiEffect = Channel<StopwatchEffect>(Channel.BUFFERED)
    val uiEffect: Flow<StopwatchEffect> = _uiEffect.receiveAsFlow()


    /**
     * Publishes a one-off effect to the [uiEffect] stream.
     */
    private fun postEffect(effect: StopwatchEffect) {
        viewModelScope.launch { _uiEffect.send(effect) }
    }

    init {
        analyticsHelper.logEvent(StopwatchAnalyticsEvent.SCREEN_VIEWED.eventName)
    }


    //---------------------------------------------------------------------
    // Event Pipeline
    //---------------------------------------------------------------------

    /**
     * Primary interface for the View to communicate user intent.
     * * Maps semantic [StopwatchEvent]s to internal business logic execution,
     * ensuring the View layer remains purely declarative.
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
        if (uiState.value.isRunning){ pauseStopwatch() }
        else {startStopwatch()}
    }

    /**
     * Toggles the operational state. If entering a 'Running' state, triggers
     * a Foreground Service promotion to ensure durability.
     */
    private fun startStopwatch() = viewModelScope.launch {
        val result = stopwatchUsecase.startStopwatch()
        if (result is MyResult.Error) {
            postEffect(StopwatchEffect.ShowError(resourceProvider.getString(R.string.failed_to_start_stopwatch)))
        } else {
            analyticsHelper.logEvent(StopwatchAnalyticsEvent.START_STOPWATCH.eventName)
            analyticsHelper.logEvent(StopwatchAnalyticsEvent.START_FOREGROUND_SERVICE.eventName)
            postEffect(StopwatchEffect.StartForegroundService)
        }
    }

    /**
     * Pauses the stopwatch via the domain layer.
     */
    private fun pauseStopwatch() = viewModelScope.launch {
        val result = stopwatchUsecase.pauseStopwatch()
        analyticsHelper.logEvent(StopwatchAnalyticsEvent.PAUSE_STOPWATCH.eventName)
        handleErrorResult(result,R.string.failed_to_pause_stopwatch_state)
    }

    /**
     * Records the current elapsed time as a lap.
     */
    private fun recordStopwatchLap() = viewModelScope.launch {
        val result = stopwatchUsecase.lapStopwatch()
        analyticsHelper.logEvent(StopwatchAnalyticsEvent.LAP_STOPWATCH.eventName)
        handleErrorResult(result,R.string.failed_to_record_lap_stopwatch_state)
    }

    /**
     * Clears persistent session data and signals the service to demote
     * to a background state.
     */
    private fun resetStopwatch() = viewModelScope.launch {
        val result = stopwatchUsecase.deleteStopwatch()
        analyticsHelper.logEvent(StopwatchAnalyticsEvent.RESET_STOPWATCH.eventName)
        analyticsHelper.logEvent(StopwatchAnalyticsEvent.STOP_FOREGROUND_SERVICE.eventName)
        postEffect(StopwatchEffect.StopForegroundService)
        handleErrorResult(result, R.string.failed_to_reset_stopwatch_state)
    }



    //---------------------
    // Blink Job Methods
    //--------------------

    /**
     * Evaluates state criteria to manage the 'Paused' blinking animation.
     * Orchestrates the [BlinkEffectJobManager] to maintain UI responsiveness
     * without cluttering the primary [uiState].
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

    fun getCurrentStopwatch() : StopwatchModel {
        return stopwatchUsecase.getCurrentStopwatch()
    }


    /**
     * Centralized error handling.
     * Logs the context to Crashlytics while showing the UI message to the user.
     */
    private fun handleErrorResult(result: MyResult<Unit, DataError>, errorMessageResId: Int) {

        if (result is MyResult.Error) {

            val error = result.error
            val userFriendlyMessage = resourceProvider.getString(errorMessageResId)

            // 4. Log the error details silently
            errorLogger.log("Action Failed: $userFriendlyMessage | DataError: $error")

            // 5. Record the actual exception/non-fatal
            val throwable = if (error is DataError.Unexpected) error.throwable
            else Exception("UI_ACTION_ERROR: $userFriendlyMessage ($error)")

            errorLogger.recordException(throwable)

            // 6. Notify the user via UI Effect
            postEffect(StopwatchEffect.ShowError(userFriendlyMessage))
        }
    }

}