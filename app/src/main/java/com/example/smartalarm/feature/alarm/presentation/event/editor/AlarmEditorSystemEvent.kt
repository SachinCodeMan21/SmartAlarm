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
     * Event triggered when snooze settings have been updated.
     *
     * @param snoozeSettings The updated [com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings] object.
     */
    data class SnoozeUpdated(val snoozeSettings: SnoozeSettings) : AlarmEditorSystemEvent()

}