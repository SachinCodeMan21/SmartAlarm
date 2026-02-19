package com.example.smartalarm.feature.alarm.presentation.viewmodel.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
import com.example.smartalarm.feature.alarm.presentation.effect.mission.MissionEffect
import com.example.smartalarm.feature.alarm.presentation.event.mission.ShakeMissionEvent
import com.example.smartalarm.feature.alarm.presentation.model.mission.ShakeMissionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the state and logic of the shake mission.
 * Handles events like initializing the mission, tracking accelerometer input,
 * and updating the UI state based on user interactions.
 *
 * @property vibrationManager The [VibrationManager] used to provide haptic feedback during the shake mission.
 */
@HiltViewModel
class ShakeMissionViewModel @Inject constructor(
    private val systemClockHelper: SystemClockHelper,
    private val vibrationManager: VibrationManager
) : ViewModel()
{

    companion object {
        private const val SHAKE_THRESHOLD = 12.0   // The threshold acceleration value to trigger a shake detection
        private const val SHAKE_DELAY_MS = 500L     // Minimum time interval between shakes to prevent multiple detections
        private const val VIBRATION_DURATION_MS = 100L  // Duration of the vibration feedback in milliseconds
        private const val COMPLETION_DELAY_MS = 1000L  // Delay after completing the mission before triggering a completion effect
    }

    private val _uiState = MutableStateFlow(ShakeMissionUiModel())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MissionEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private var lastShakeTime = 0L

    // ---------------------------------------------------------------------
    // Shake Mission UI Handler
    // ---------------------------------------------------------------------

    /**
     * Handles the events that drive changes in the shake mission.
     *
     * @param event The [ShakeMissionEvent] representing a user interaction or system event.
     */
    fun handleEvent(event: ShakeMissionEvent){
        when(event){
            is ShakeMissionEvent.InitializeMission -> initialize(event.mission)
            is ShakeMissionEvent.AccelerationChanged -> handleAcceleration(event.acceleration)
        }
    }


    // ---------------------------------------------------------------------
    // Event Handler Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes the mission state with the total number of shakes to be completed.
     * This method is called when the mission is initialized, and it sets the
     * target shake count based on the provided [Mission] object.
     *
     * @param mission The [Mission] instance containing the mission's parameters,
     * such as the number of rounds (shakes) required to complete the mission.
     */
    private fun initialize(mission: Mission) {
        _uiState.value = _uiState.value.copy(totalShakes = mission.rounds)
    }

    /**
     * Handles accelerometer input by detecting whether the device's acceleration
     * exceeds the defined threshold. If the threshold is surpassed, it checks if
     * enough time has passed since the last detected shake, and then triggers
     * the shake handling logic.
     *
     * @param acceleration The current acceleration value detected by the device's sensors.
     * This value is compared against the [SHAKE_THRESHOLD] to determine if the device has been shaken.
     */
    private fun handleAcceleration(acceleration: Double) {
        if (acceleration > SHAKE_THRESHOLD) {
            val now = systemClockHelper.getCurrentTime()
            // Ensures that shakes are not counted too frequently (prevents duplicate shakes).
            if (now - lastShakeTime > SHAKE_DELAY_MS) {
                lastShakeTime = now
                handleShake()
            }
        }
    }

    /**
     * Increments the shake count by one along with a vibrate each time a valid shake is detected.
     * If the required number of shakes (totalShakes) has been reached, the method
     * triggers the completion sequence and sends a completion effect to notify the UI.
     *
     * @see vibrate to trigger haptic feedback after each valid shake.
     */
    private fun handleShake() = viewModelScope.launch {

        val current = _uiState.value.shakeCount
        val total = _uiState.value.totalShakes

        // Prevent going beyond the target shake count.
        if (current >= total) return@launch

        val newCount = current + 1
        _uiState.value = _uiState.value.copy(shakeCount = newCount)
        vibrate()

        // If the user has completed the required number of shakes, send a mission completion effect.
        if (newCount == total) {
            viewModelScope.launch {
                delay(COMPLETION_DELAY_MS) // Wait briefly before completing the mission.
                _uiEffect.emit(MissionEffect.MissionCompleted)
            }
        }
    }

    /**
     * Triggers a short vibration feedback to the user, signaling that a valid shake
     * has been detected. This provides haptic feedback to confirm the action.
     */
    private fun vibrate() {
        vibrationManager.vibrateOneShot(durationMs = VIBRATION_DURATION_MS, amplitude = 200)
    }

}