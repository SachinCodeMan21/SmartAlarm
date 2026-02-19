package com.example.smartalarm.feature.clock.presentation.event

import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.presentation.viewmodel.ClockViewModel

/**
 * Represents all UI events related to the Clock screen.
 *
 * These events are typically triggered by user interactions or UI lifecycle events
 * and are handled by the [ClockViewModel] to perform actions or side effects.
 */
sealed class ClockEvent {

    /**
     * Event to load and observe all saved time zones from the local database.
     *
     * Usually triggered when the screen starts or refreshes.
     */
    object LoadSelectedTimeZones : ClockEvent()

    /**
     * Event indicating the user wants to add a new time zone.
     *
     * This triggers navigation to the "Add Time Zone" screen.
     */
    object AddNewTimeZone : ClockEvent()

    /**
     * Event to stop ongoing clock UI updates (e.g., when screen is paused or stopped).
     */
    object StopClockUiUpdates : ClockEvent()

    /**
     * Event to delete a specific time zone from the saved list.
     *
     * @property deletedTimeZone The [PlaceModel] to be removed from the database.
     */
    data class DeleteTimeZone(val deletedTimeZone: PlaceModel) : ClockEvent()

    /**
     * Event to undo a previous time zone deletion by re-inserting it.
     *
     * @property deletedTimeZone The [PlaceModel] to be restored.
     */
    data class UndoDeletedTimeZone(val deletedTimeZone: PlaceModel) : ClockEvent()

    /**
     * Event to show a one-time toast message to the user.
     *
     * @property message The message content to be displayed.
     */
    data class ShowToastMessage(val message: String) : ClockEvent()
}
