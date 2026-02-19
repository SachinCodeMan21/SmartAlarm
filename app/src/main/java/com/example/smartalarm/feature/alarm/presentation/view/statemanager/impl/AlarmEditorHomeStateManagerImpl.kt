package com.example.smartalarm.feature.alarm.presentation.view.statemanager.impl

import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings
import com.example.smartalarm.feature.alarm.presentation.view.statemanager.contract.AlarmEditorHomeStateManager
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime
import javax.inject.Inject

/**
 * Concrete implementation of [AlarmEditorHomeStateManager] responsible for managing
 * the state of an alarm being edited in the UI.
 *
 * This class holds the alarm state in a [MutableStateFlow], allowing reactive updates
 * and efficient recomposition in Jetpack Compose or similar frameworks.
 *
 * @constructor Injects dependencies such as [AlarmTimeHelper].
 * @param alarmTimeHelper A helper for converting 12-hour time inputs to the appropriate format.
 */
class AlarmEditorHomeStateManagerImpl @Inject constructor(
    private val alarmRingtoneHelper: AlarmRingtoneManager,
    private val alarmTimeHelper: AlarmTimeHelper
) : AlarmEditorHomeStateManager
{

    private val _alarmState =  MutableStateFlow(AlarmModel())

    /**
     * Public immutable [StateFlow] exposing the current alarm being edited.
     */
    override val getAlarmState: StateFlow<AlarmModel>
        get() = _alarmState.asStateFlow()

    /**
     * Holds the previously selected days of the week before switching to "daily" alarm mode.
     *
     * This is used to restore custom day selections when "daily" mode is disabled.
     */
    private var previousSelectedDays: Set<DayOfWeek> = emptySet()


    /**
     * Initializes the alarm state to a fresh default [AlarmModel].
     *
     * Sets the alarm time to the current local time (with seconds and nanoseconds cleared),
     * and sets the alarm sound to the default ringtone URI.
     * Also resets the previously selected days to an empty set.
     */
    override fun initAlarmState() {
        _alarmState.value = AlarmModel(
            time = LocalTime.now().withSecond(0).withNano(0),
            alarmSound = alarmRingtoneHelper.getDefaultRingtoneUri().toString(),
        )
        previousSelectedDays = emptySet()
    }


    /**
     * Sets a new [AlarmModel] to be edited.
     *
     * This is typically called when an existing alarm is being modified.
     *
     * @param alarm The alarm model to set as the current state.
     */
    override fun setAlarm(alarm: AlarmModel) {
        _alarmState.value = alarm
        previousSelectedDays = alarm.days
    }

    /**
     * Updates the alarm's label or title.
     *
     * @param label The new label string.
     */
    override fun updateLabel(label: String) {
        _alarmState.update { it.copy(label = label) }
    }

    /**
     * Updates the alarm time using a 12-hour clock format (AM/PM) and converts it to a 24-hour format.
     *
     * This function takes in the hour, minute, and AM/PM information from the user,
     * converts the time to a 24-hour format using the `convertTo24HourTime` method,
     * and updates the alarm state with the newly formatted time.
     *
     * @param hour The hour of the alarm, in 12-hour format (1–12).
     * @param minute The minute of the alarm, in the range (0–59).
     * @param amPm The period of the day: 0 for AM, 1 for PM.
     */
    override fun updateTime(hour: Int, minute: Int, amPm: Int) {
        val alarmTime = alarmTimeHelper.convertTo24HourTime(hour, minute, amPm)
        _alarmState.update { it.copy(time = alarmTime ) }
    }

    /**
     * Enables or disables the "daily" alarm mode.
     *
     * When "daily" mode is enabled, the alarm will repeat every day, and the days of the week
     * are set to include all days (Sunday to Saturday). When "daily" mode is disabled, it restores
     * the previously selected custom days that the user had set before enabling the "daily" mode.
     *
     * @param isDaily `true` to enable the daily alarm mode (alarm repeats every day), `false` to disable it
     *                and restore the previously selected custom days.
     */
    override fun updateIsDaily(isDaily: Boolean) {
        _alarmState.update { state ->
            if (isDaily) {
                state.copy(isDailyAlarm = true, days = DayOfWeek.entries.toSet())  // set all days
            } else {
                state.copy(isDailyAlarm = false, days = previousSelectedDays)  // restore custom days
            }
        }
    }

    /**
     * Toggles the selection of a specific day of the week.
     *
     * If all days are selected, the "daily" alarm mode is enabled automatically.
     * If not all days are selected, it restores the previously selected custom days.
     *
     * @param dayIndex Index from 0 (Sunday) to 6 (Saturday).
     */
    override fun toggleDay(dayIndex: Int) {
        val selectedDay = DayOfWeek.entries[dayIndex]

        _alarmState.update { alarm ->

            // Toggle the day in the set
            val updatedDays = alarm.days.toMutableSet().apply {
                if (contains(selectedDay)) remove(selectedDay) else add(selectedDay)
            }

            // Check if all days are selected and update accordingly
            val isNowDaily = updatedDays.size == DayOfWeek.entries.size

            // If not in daily mode, store the selected days
            if (!isNowDaily) previousSelectedDays = updatedDays.toSet()

            // Return updated state
            alarm.copy(
                days = updatedDays,
                isDailyAlarm = isNowDaily
            )
        }
    }


    /**
     * Updates the volume level for the alarm.
     *
     * @param volume An integer between 0 and 100.
     */
    override fun updateVolume(volume: Int) {
        _alarmState.update { it.copy(volume = volume) }
    }

    /**
     * Enables or disables vibration for the alarm.
     *
     * @param enabled True to enable vibration, false to disable.
     */
    override fun updateVibration(enabled: Boolean) {
        _alarmState.update { it.copy(isVibrateEnabled = enabled) }
    }

    /**
     * Updates the URI string of the selected alarm ringtone.
     *
     * @param uri The URI string representing the selected alarm sound.
     */
    override fun updateRingtone(uri: String) {
        _alarmState.update { it.copy(alarmSound = uri) }
    }

    /**
     * Updates the snooze configuration for the alarm.
     *
     * @param snoozeSettings The new snooze configuration object.
     */
    override fun updateSnooze(snoozeSettings: SnoozeSettings) {
        _alarmState.update { it.copy(snoozeSettings = snoozeSettings) }
    }

    /**
     * Adds or replaces a mission at the specified position.
     *
     * If the position is within the current list, the mission is updated.
     * Otherwise, it is appended to the list.
     *
     * @param position The index at which to insert or update the mission.
     * @param mission The [Mission] to add or update.
     */
    override fun updateMission(position: Int, mission: Mission) {
        _alarmState.update { state ->
            val updatedMissions = state.missions.toMutableList()

            if (position in updatedMissions.indices) {
                updatedMissions[position] = mission
            } else {
                updatedMissions.add(mission)
            }

            state.copy(missions = updatedMissions)
        }
    }

    /**
     * Removes the mission at the given index, if it exists.
     *
     * @param position The index of the mission to remove.
     */
    override fun removeMissionAt(position: Int) {
        _alarmState.update { state ->
            val updatedMissions = state.missions.toMutableList()
            if (position in updatedMissions.indices) {
                updatedMissions.removeAt(position)
            }
            state.copy(missions = updatedMissions)
        }
    }

}
