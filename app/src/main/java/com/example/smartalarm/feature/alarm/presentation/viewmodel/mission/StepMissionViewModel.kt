package com.example.smartalarm.feature.alarm.presentation.viewmodel.mission

import android.os.VibrationEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
import com.example.smartalarm.feature.alarm.presentation.effect.mission.MissionEffect
import com.example.smartalarm.feature.alarm.presentation.event.mission.StepMissionEvent
import com.example.smartalarm.feature.alarm.presentation.model.mission.StepMissionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing step-based mission logic, including step detection, validation, and UI updates.
 *
 * This ViewModel:
 * - Tracks the current step count and mission progress.
 * - Validates detected steps based on acceleration and time intervals to reduce false positives.
 * - Sends mission completion events when the step goal is reached.
 * - Provides UI state updates and mission completion effects to the UI layer.
 *
 * Dependencies:
 * - [VibrationManager] is injected to provide haptic feedback when a step is detected.
 *
 * Configuration constants:
 * - `MIN_STEP_INTERVAL_MS`: Minimum time (in milliseconds) between consecutive valid steps.
 * - `ACCELERATION_THRESHOLD`: Minimum acceleration magnitude required to qualify as a valid step.
 */
@HiltViewModel
class StepMissionViewModel @Inject constructor(
    private val systemClockHelper: SystemClockHelper,
   private val vibrationManager: VibrationManager
): ViewModel() {

    companion object {

        // Minimum time between valid steps
        private const val MIN_STEP_INTERVAL_MS = 500L

        // Minimum acceleration to qualify as a valid step
        private const val ACCELERATION_THRESHOLD = 10f

    }

    private val _uiState = MutableStateFlow(StepMissionUiModel())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MissionEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    /** Last timestamp when a valid step was detected. Used to filter false positives. */
    private var lastStepTime = 0L

    /** Last measured acceleration magnitude. Used to validate step detection. */
    private var lastAcceleration = 0f



    // ---------------------------------------------------------------------
    // Event Handling
    // ---------------------------------------------------------------------

    /**
     * Central handler for all [StepMissionEvent] types.
     *
     * @param event The user or sensor event to process.
     */
    fun handleEvent(event: StepMissionEvent) {
        when (event) {
            is StepMissionEvent.InitializeMission -> initialize(event.mission)
            is StepMissionEvent.AccelerationChanged -> lastAcceleration = event.magnitude
            is StepMissionEvent.StepDetected -> handleStepDetected()
        }
    }

    /**
     * Initializes mission state with total steps based on the [Mission] configuration.
     */
    private fun initialize(mission: Mission) {
        _uiState.value = _uiState.value.copy(totalSteps = mission.rounds)
    }


    /**
     * Processes a detected step, applying validation and updating state.
     *
     * Validates step detection using:
     * - Minimum interval since last step
     * - Sufficient acceleration magnitude
     *
     * On valid step:
     * - Increments step count (up to max)
     * - Sends haptic feedback
     * - Completes mission when step goal is reached
     */
    private fun handleStepDetected() = viewModelScope.launch {

        val currentTime = systemClockHelper.getCurrentTime()

        if ((currentTime - lastStepTime >= MIN_STEP_INTERVAL_MS && lastAcceleration > ACCELERATION_THRESHOLD)) {

            lastStepTime = currentTime

            val current = _uiState.value.stepCount
            val safeCount = (current + 1).coerceAtMost(_uiState.value.totalSteps)

            _uiState.update { it.copy(stepCount = safeCount) }

            vibrationManager.vibrateOneShot(50L, VibrationEffect.DEFAULT_AMPLITUDE)

            if (safeCount == _uiState.value.totalSteps) {
                delay(500L)
                _uiEffect.emit(MissionEffect.MissionCompleted)
            }

        }

    }




}