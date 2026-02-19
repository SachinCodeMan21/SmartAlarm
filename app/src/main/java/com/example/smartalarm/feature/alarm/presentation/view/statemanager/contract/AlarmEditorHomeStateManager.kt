package com.example.smartalarm.feature.alarm.presentation.view.statemanager.contract

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings
import kotlinx.coroutines.flow.StateFlow


/**
 * Manages the state and behavior of the alarm editor home screen.
 *
 * This interface provides a [StateFlow] to observe the current alarm state being edited,
 * and exposes methods to initialize, update, and modify various properties of the alarm,
 * such as time, label, repeat days, volume, vibration, ringtone, snooze settings, and missions.
 *
 * Implementations of this interface are responsible for maintaining and updating the alarm state,
 * allowing the UI layer to reactively observe changes and present the current alarm configuration.
 */
interface AlarmEditorHomeStateManager {

    /**
     * A [StateFlow] representing the current alarm being edited.
     *
     * Consumers can observe this flow to reflect changes in the alarm state within the UI.
     */
    val getAlarmState: StateFlow<AlarmModel>

    /**
     * Initializes the alarm state to a fresh default [AlarmModel].
     *
     * Sets the alarm time to the current local time (with seconds and nanoseconds cleared),
     * and sets the alarm sound to the default ringtone URI.
     * Also resets the previously selected days to an empty set.
     */
    fun initAlarmState()

    /**
     * Replaces the current alarm state with a new [AlarmModel].
     *
     * Typically used when loading an existing alarm into the editor.
     *
     * @param alarm The alarm model to set as the current editing state.
     */
    fun setAlarm(alarm: AlarmModel)

    /**
     * Updates the label or name of the alarm.
     *
     * @param label The new label to assign to the alarm.
     */
    fun updateLabel(label: String)

    /**
     * Updates the time of the alarm using a 12-hour clock format.
     *
     * @param hour The hour component (1–12).
     * @param minute The minute component (0–59).
     * @param amPm 0 for AM, 1 for PM.
     */
    fun updateTime(hour: Int, minute: Int, amPm: Int)

    /**
     * Sets whether the alarm should repeat daily.
     *
     * @param isDaily True if the alarm should repeat every day, false otherwise.
     */
    fun updateIsDaily(isDaily: Boolean)

    /**
     * Toggles the activation status of a specific day in the weekly schedule.
     *
     * @param dayIndex An integer representing the day of the week (0 = Sunday, 6 = Saturday).
     */
    fun toggleDay(dayIndex: Int)

    /**
     * Updates the volume level of the alarm.
     *
     * @param volume An integer value between 0 and 100.
     */
    fun updateVolume(volume: Int)

    /**
     * Enables or disables vibration for the alarm.
     *
     * @param enabled True to enable vibration, false to disable.
     */
    fun updateVibration(enabled: Boolean)

    /**
     * Updates the URI of the ringtone to be used for the alarm.
     *
     * @param uri A string representing the URI of the selected ringtone.
     */
    fun updateRingtone(uri: String)

    /**
     * Updates the snooze configuration of the alarm.
     *
     * @param snoozeSettings The new [SnoozeSettings] to apply.
     */
    fun updateSnooze(snoozeSettings: SnoozeSettings)

    /**
     * Adds or updates a mission at the specified position in the mission list.
     *
     * @param position The index at which to insert or update the mission.
     * @param mission The [Mission] object to add or update.
     */
    fun updateMission(position: Int, mission: Mission)

    /**
     * Removes the mission at the given position, if it exists.
     *
     * @param position The index of the mission to remove.
     */
    fun removeMissionAt(position: Int)

}
