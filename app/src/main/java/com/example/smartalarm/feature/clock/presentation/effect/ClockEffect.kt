package com.example.smartalarm.feature.clock.presentation.effect

import com.example.smartalarm.feature.clock.domain.model.PlaceModel

/**
 * Represents one-time side effects from the Clock screen's ViewModel.
 *
 * These effects are consumed by the UI (e.g., navigation or toast messages)
 * and are not part of the persistent state.
 */
sealed class ClockEffect {

    /**
     * Triggers navigation to the screen where a new time zone can be added.
     */
    data object NavigateToAddTimeZoneScreen : ClockEffect()

    /**
     * Signals that a time zone was deleted and the UI may need to respond
     * (e.g., show an undo SnackBar).
     *
     * @property deletedTimeZone The [PlaceModel] that was deleted.
     */
    data class DeleteTimeZone(val deletedTimeZone: PlaceModel) : ClockEffect()

    /**
     * Instructs the UI to show a toast message.
     *
     * @property message The message text to be shown.
     */
    data class ShowToast(val message: String) : ClockEffect()
}
