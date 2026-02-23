package com.example.smartalarm.feature.timer.presentation.effect

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.feature.timer.presentation.view.activity.ShowTimerActivity
import com.example.smartalarm.feature.timer.presentation.viewmodel.ShowTimerViewModel


/**
 * Sealed class representing one-time UI effects in the [ShowTimerActivity] triggered
 * by the [ShowTimerViewModel]. These effects represent actions that result in side effects
 * or changes that should be performed once, such as navigation, notifications, or showing
 * a message to the user.
 *
 * Responsibilities:
 * - Define one-time UI effects triggered by the ViewModel to modify the UI or perform
 *   external actions such as starting notifications, navigating between screens, or showing toast messages.
 * - Each effect is handled only once, and the system will react to these events when they are emitted.
 *
 * Effects:
 * - **StartTimerForegroundNotification**: Starts a foreground notification for the timer service.
 * - **StopTimerForegroundNotification**: Stops the foreground notification for the timer service.
 * - **NavigateToAddTimer**: Triggers navigation to the "Add Timer" screen.
 * - **ShowToast**: Displays a toast message with the provided [String].
 *
 * These effects typically correspond to immediate user-facing actions or side effects
 * that should be triggered in response to certain events, such as timer changes or UI updates.
 *
 * @see ShowTimerViewModel
 * @see ShowTimerActivity
 */
sealed class ShowTimerEffect {


    /** Starts a foreground notification for the timer service. */
    object StartTimerForegroundNotification : ShowTimerEffect()

    /**
     * Represents an effect that triggers navigation to the "TimerScreen" screen.
     *
     * This event is typically used to navigate away from the current screen (Show Timer)
     * and transition to the screen where users can create or add a new timer.
     */
    object FinishActivity : ShowTimerEffect()


    /** Displays a toast message with the provided [error]. */
    data class ShowError(val error: DataError) : ShowTimerEffect()
}