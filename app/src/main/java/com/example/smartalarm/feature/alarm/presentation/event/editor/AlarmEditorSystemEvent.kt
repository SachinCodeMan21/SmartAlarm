package com.example.smartalarm.feature.alarm.presentation.event.editor

import com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings

/**
 * Represents all the possible user events or actions on the Alarm Editor screen.
 *
 * These events are typically consumed by the ViewModel to perform business logic,
 * update state, or trigger navigation.
 */
sealed class AlarmEditorSystemEvent {

    /**
     * Event to initialize the alarm editor state, either by creating a new alarm or loading an existing one.
     *
     * - If [existingAlarmId] is `0`, a new alarm is initialized.
     * - If [existingAlarmId] is greater than 0 an existing alarm with the given ID is loaded.
     *
     * @param existingAlarmId The ID of the alarm to load.
     */
    data class InitializeAlarmEditorState(val existingAlarmId : Int) : AlarmEditorSystemEvent()

    /**
     * Represents the event triggered when the user has granted the **Post Notification** permission.
     *
     * Used to resume any previously pending actions that required this permission.
     */
    object PostNotificationPermissionGranted : AlarmEditorSystemEvent()

    /**
     * Represents the event triggered when the user has granted the **Exact Alarm** permission.
     *
     * Used to resume any previously pending actions that required this permission.
     */
    object ExactAlarmPermissionGranted : AlarmEditorSystemEvent()

    /**
     * Represents a system event to retry a pending save or update action in the Alarm Editor..
     */
    object RetryPendingSaveAction : AlarmEditorSystemEvent()

    /**
     * Event triggered when snooze settings have been updated.
     *
     * @param snoozeSettings The updated [com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings] object.
     */
    data class SnoozeUpdated(val snoozeSettings: SnoozeSettings) : AlarmEditorSystemEvent()

}