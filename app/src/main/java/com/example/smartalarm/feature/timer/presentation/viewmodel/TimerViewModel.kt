package com.example.smartalarm.feature.timer.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.R
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.presentation.effect.TimerEffect
import com.example.smartalarm.feature.timer.presentation.event.TimerEvent
import com.example.smartalarm.feature.timer.presentation.model.TimerUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.timer.domain.model.TimerState
import com.example.smartalarm.feature.timer.domain.usecase.contract.GetAllTimersUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.SaveTimerUseCase
import com.example.smartalarm.feature.timer.presentation.view.statemanager.contract.TimerInputStateManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull

/**
 * ViewModel responsible for managing the Timer screen's state, user interactions,
 * and coordinating with domain/use-case layers.
 *
 * Delegates timer input management to [TimerInputStateManager], which handles input digits
 * and formatting, with persistence handled externally (e.g., via [SavedStateHandle]).
 *
 * Exposes the UI state as a [StateFlow] for reactive UI updates,
 * and one-time UI effects as a [Flow] for navigation and transient events.
 *
 * @property getAllTimersUseCase Use case for retrieving all saved timers.
 * @property saveTimerUseCase Use case for saving a new timer.
 * @property systemClockHelper Provides elapsed realtime for timer calculations.
 * @property timerInputStateManager Manager responsible for timer input state and formatting.
 */
@HiltViewModel
class TimerViewModel @Inject constructor(
    private val getAllTimersUseCase: GetAllTimersUseCase,
    private val saveTimerUseCase: SaveTimerUseCase,
    private val systemClockHelper: SystemClockHelper,
    private val timerInputStateManager: TimerInputStateManager,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    companion object {

        /** Key representing the backspace button label */
        const val KEY_BACKSPACE = "⌫"

        /** Key representing the zero digit */
        const val KEY_ZERO = "0"

        /** Key representing double zero digits */
        const val KEY_DOUBLE_ZERO = "00"

    }

    /** Backing mutable state flow holding the UI state of the timer screen */
    private val _uiState = MutableStateFlow(TimerUiModel())
    val uiState: StateFlow<TimerUiModel> = _uiState

    /** SharedFlow for one-time UI effects such as navigation or SnackBars */
    private val _uiEffect = MutableSharedFlow<TimerEffect>(0)
    val uiEffect: Flow<TimerEffect> = _uiEffect.asSharedFlow()


    // ---------------------------------------------------------------------------
    // UI & Effect State Management
    // ---------------------------------------------------------------------------

    /**
     * Updates the UI state based on the current timer input and whether any timers are running.
     *
     * - This method retrieves the formatted timer string from [timerInputStateManager] and updates
     * the visibility of the "Start Timer" and "Delete Timer" buttons based on the input and running timers.
     * - It also allows for optionally passing a flag to determine if any timers are running,
     * which controls the visibility of the "Delete Timer" button.
     *
     * @param hasRunningTimers A flag indicating whether any timers are currently running.
     * By default, it uses the current state of isDeleteTimerButtonVisible to determine this.
     */
    private fun updateUiState(hasRunningTimers: Boolean = _uiState.value.isDeleteTimerButtonVisible) {
        val formatted = timerInputStateManager.getFormattedTime()
        _uiState.update {
            it.copy(
                formattedTime = formatted,
                isStartButtonVisible = timerInputStateManager.isStartButtonVisible(),
                isDeleteTimerButtonVisible = hasRunningTimers
            )
        }
    }


    /**
     * Sends a one-time UI effect event to be observed by the UI layer.
     *
     * Launches a coroutine within [viewModelScope] to emit the effect asynchronously.
     *
     * @param effect The [TimerEffect] event to send.
     */
    private fun postEffect(effect: TimerEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }


    // ---------------------------------------------------------------------------
    // Timer Fragment Events Handler
    // ---------------------------------------------------------------------------

    /**
     * Handles timer-related UI events dispatched from the fragment or view.
     *
     * Delegates input handling, navigation, and timer start logic based on event type.
     *
     * @param action The [TimerEvent] representing the user action or system event.
     */
    fun handleEvent(action: TimerEvent) {
        when (action) {
            is TimerEvent.InitTimerUIState -> initTimerUIState()
            is TimerEvent.HandleKeypadClick -> handleKeypadClick(action.label)
            is TimerEvent.HandleStartTimerClick -> handleStartTimer()
            is TimerEvent.HandleDeleteTimerClick -> postEffect(TimerEffect.NavigateToShowTimerScreen)
        }
    }


    // ---------------------------------------------------------------------------
    // Action Handlers
    // ---------------------------------------------------------------------------

    /**
     * Initializes the timer UI state by fetching the list of timers.
     *
     * This method checks if there are any active timers and updates the UI accordingly.
     * Specifically, it determines whether the "Delete Timer" button should be visible,
     * based on whether there are timers present or not.
     *
     * In case of an error while fetching the timers, a user-friendly error message
     * is displayed through a SnackBar.
     */
    fun initTimerUIState() = viewModelScope.launch {
        // Collect the first emission of the timers and then stop listening
        getAllTimersUseCase()
            .catch { e ->
                // Show error message if timers fail to load
                postEffect(TimerEffect.ShowSnackBar(resourceProvider.getString(R.string.failed_to_load_timers)))
            }
            .firstOrNull()
            ?.let { timers ->
                val hasRunningTimers = timers.isNotEmpty()
                // Update UI to reflect whether there are active timers or not
                updateUiState(hasRunningTimers)
            }
    }


    /**
     * Processes keypad button clicks for timer input.
     *
     * Supports digit appending and backspace removal through [timerInputStateManager].
     * Prevents leading zeros by only allowing "0" or "00" if there is already input.
     *
     * @param label The text label of the clicked keypad button.
     */
    private fun handleKeypadClick(label: String) {
        val isZero = resourceProvider.getString(R.string._0)
        val isDoubleZero = resourceProvider.getString(R.string._00)

        when (label) {
            KEY_BACKSPACE -> timerInputStateManager.removeLastDigit()
            isZero, isDoubleZero -> {
                if (timerInputStateManager.isStartButtonVisible()) {
                    timerInputStateManager.appendDigit(label)
                }
            }

            else -> timerInputStateManager.appendDigit(label)
        }

        updateUiState()
    }


    /**
     * Handles starting a new timer based on the current input digits.
     *
     * Converts input digits to milliseconds, creates a new [TimerModel],
     * saves it through the use case, and upon success clears input and triggers navigation.
     */
    private fun handleStartTimer() = viewModelScope.launch {

        val timerDurationMillis = timerInputStateManager.timerInputToMillis()

        if (timerDurationMillis <= 0) return@launch

        val timer = TimerModel(
            startTime = systemClockHelper.getCurrentTime(),
            targetTime = timerDurationMillis,
            remainingTime = timerDurationMillis,
            isTimerRunning = true,
            state = TimerState.IDLE
        )

        when (saveTimerUseCase(timer)) {
            is Result.Success -> {
                timerInputStateManager.clearInput()
                postEffect(TimerEffect.NavigateToShowTimerScreen)
            }

            is Result.Error -> {
                postEffect(TimerEffect.ShowSnackBar(resourceProvider.getString(R.string.unable_to_start_the_timer)))
            }
        }
    }

}


