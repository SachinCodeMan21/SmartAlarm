package com.example.smartalarm.feature.timer.presentation.event

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.presentation.view.activity.ShowTimerActivity
import com.example.smartalarm.feature.timer.presentation.viewmodel.ShowTimerViewModel


/**
 * Sealed class representing the various events that can be triggered in the
 * [ShowTimerActivity] and handled by the [ShowTimerViewModel]. These events
 * represent actions related to timers, UI state changes, and service management.
 *
 * Responsibilities:
 * - Represent different types of actions or events in the timer system.
 * - Define UI state changes (such as navigating to the add timer screen,
 *   starting/stopping timers, showing a toast, etc.).
 * - Events can be consumed by the ViewModel and trigger corresponding side effects
 *   or updates to the UI.
 *
 * Events:
 * - **StartTimerForegroundService**: Triggers the start of a foreground timer service.
 * - **StopTimerForegroundService**: Triggers the stop of the foreground timer service.
 * - **NavigateToAddTimer**: Navigates to the screen for adding a new timer.
 * - **RestoreTimerState**: Restores the state of the timers from persistent storage.
 * - **StopTimerUiUpdates**: Stops updates to the UI related to the timers.
 * - **StartTimer**: Starts a timer with the given [TimerModel] data.
 * - **PauseTimer**: Pauses the timer with the given [TimerModel] data.
 * - **SnoozeTimer**: Snoozes the timer with the given [TimerModel] data.
 * - **RestartTimer**: Restarts the timer with the given [TimerModel] data.
 * - **StopTimer**: Stops the timer with the given [TimerModel] data.
 * - **ShowToast**: Displays a toast message with the given [TimerModel].
 *
 * Each event may result in a change in UI state, trigger a service action, or
 * interact with the timer data in some way.
 *
 * @see ShowTimerViewModel
 * @see ShowTimerActivity
 */
sealed class ShowTimerEvent {

    /**
     * Represents an event triggered when the list of timers is empty.
     */
    object HandleEmptyTimerList : ShowTimerEvent()

    /** Event triggered when the back button on the toolbar is pressed */
    object HandleToolbarBackPressed : ShowTimerEvent()

    /** Event triggered when a new timer is added */
    object AddNewTimer : ShowTimerEvent()

    /** Stops the UI updates related to the timer. */
    data object StopTimerUiUpdates : ShowTimerEvent()

    /** Starts a specific timer with the given [timer] data. */
    data class StartTimer(val timer: TimerModel) : ShowTimerEvent()

    /** Pauses a specific timer with the given [timer] data. */
    data class PauseTimer(val timer: TimerModel) : ShowTimerEvent()

    /** Snoozes a specific timer with the given [timer] data. */
    data class SnoozeTimer(val timer: TimerModel) : ShowTimerEvent()

    /** Restarts a specific timer with the given [timer] data. */
    data class RestartTimer(val timer: TimerModel) : ShowTimerEvent()

    /** Stops a specific timer with the given [timer] data. */
    data class StopTimer(val timer: TimerModel) : ShowTimerEvent()


    /** Starts the foreground timer service. */
    data object StartTimerForegroundService : ShowTimerEvent()

    /** Stops the foreground timer service. */
    data object StopTimerForegroundService : ShowTimerEvent()

}
