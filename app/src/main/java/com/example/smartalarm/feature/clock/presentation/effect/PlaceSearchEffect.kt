package com.example.smartalarm.feature.clock.presentation.effect

import com.example.smartalarm.feature.clock.presentation.viewmodel.PlaceSearchViewModel

/**
 * Represents one-time side effects triggered by the [PlaceSearchViewModel] that should be handled by the UI layer.
 *
 * Unlike state, effects are not persisted and are meant for transient operations such as navigation or toasts/snackbars.
 */
sealed class PlaceSearchEffect {

    /**
     * Signals the UI to finish the current activity or pop the back stack.
     */
    data object Finish : PlaceSearchEffect()

    /**
     * Navigates the user to the home screen after a successful place selection or completion.
     */
    data object NavigateToHome : PlaceSearchEffect()

    /**
     * Triggers a SnackBar with the given [message] to provide feedback to the user.
     *
     * @property message The message to be displayed in the SnackBar.
     */
    data class ShowSnackBarMessage(val message: String) : PlaceSearchEffect()
}
