package com.example.smartalarm.feature.alarm.presentation.mapper

import com.example.smartalarm.R
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.core.utility.formatter.time.TimeFormatter
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.domain.model.MissionItem
import com.example.smartalarm.feature.alarm.domain.model.MissionType
import com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings
import com.example.smartalarm.feature.alarm.presentation.model.editor.AlarmEditorHomeUiModel
import com.example.smartalarm.feature.alarm.presentation.model.home.AlarmUiModel
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import javax.inject.Inject


/**
 * Mapper class for converting [AlarmModel] objects to UI-friendly models.
 *
 * This class is responsible for mapping alarm data to the UI representations required
 * by the application. It handles conversions like formatting the alarm time,
 * preparing mission-related data, and formatting snooze settings for display.
 *
 * @param timeFormatter Formatter used to convert time into the desired format.
 * @param numberFormatter Formatter used to format numbers for UI.
 * @param alarmRingtonePlayer Manages alarm ringtone settings and titles.
 * @param resourceProvider Provides localized strings for UI elements.
 */
class AlarmUiMapper @Inject constructor(
    private val timeFormatter: TimeFormatter,
    private val numberFormatter: NumberFormatter,
    private val alarmRingtonePlayer: AlarmRingtoneManager,
    private val resourceProvider: ResourceProvider
) {

    /**
     * Converts an [AlarmModel] to a UI model ([AlarmUiModel]) for display in the alarm list.
     *
     * This method maps the alarm data to an appropriate UI format, including:
     * - Formatting the alarm time
     * - Getting the icon for the first mission
     * - Including alarm settings like enabled status and selected days
     *
     * @param alarmModel The [AlarmModel] object to be mapped.
     * @return A UI-friendly [AlarmUiModel] with formatted alarm data.
     */
    fun toUiModel(alarmModel: AlarmModel): AlarmUiModel {
        val formattedAlarmTime = timeFormatter.formatToAlarmTime(alarmModel.time.hour, alarmModel.time.minute)
        val missionIconResId = alarmModel.missions.firstOrNull()?.type?.getIconRes()
        return AlarmUiModel(
            id = alarmModel.id,
            formattedAlarmTime = formattedAlarmTime,
            selectedDays = alarmModel.days,
            missionIconResId = missionIconResId,
            alarmMissions = alarmModel.missions,
            isEnabled = alarmModel.isEnabled
        )
    }

    /**
     * Converts an [AlarmModel] to a UI model ([AlarmEditorHomeUiModel]) for use in the alarm editor.
     *
     * This method formats alarm-related data like time, days, missions, and settings (volume, vibration, etc.)
     * for displaying on the alarm editor screen.
     *
     * @param alarmModel The [AlarmModel] object to be mapped.
     * @return A UI model for the alarm editor screen ([AlarmEditorHomeUiModel]).
     */
    fun toEditorHomeUiModel(alarmModel: AlarmModel): AlarmEditorHomeUiModel {
        with(alarmModel) {
            return AlarmEditorHomeUiModel(
                label = label,
                hour = getConverted12HourTime(alarmModel.time.hour),
                minute = time.minute,
                amPm = getAmPmValue(alarmModel.time.hour),
                isDailyAlarm = isDailyAlarm,
                selectedDays = days,
                missionItemList = getMissionItemList(missions),
                formattedMissionSlotText = getFormattedMissionSlotText(missions),
                volume = volume,
                isVibrateEnabled = isVibrateEnabled,
                alarmSoundTitle = alarmRingtonePlayer.getRingtoneTitle(alarmSound),
                formattedSnoozedText = getFormattedAlarmSnoozedText(snoozeSettings)
            )
        }
    }


    // Helper Methods

    /**
     * Determines whether the given hour is AM (0) or PM (1).
     *
     * @param hour The hour in 24-hour format.
     * @return 0 for AM and 1 for PM.
     */
    fun getAmPmValue(hour: Int): Int {
        return if (hour >= 12) 1 else 0
    }

    /**
     * Converts a 24-hour format hour to a 12-hour format.
     *
     * @param hour The hour in 24-hour format.
     * @return The equivalent hour in 12-hour format.
     */
    fun getConverted12HourTime(hour: Int): Int {
        val amPm = getAmPmValue(hour)
        return when (amPm) {
            0 -> if (hour == 0) 12 else hour        // 0 → 12 AM, 1–11 stay same
            1 -> if (hour == 12) 12 else hour - 12  // 12 → 12 PM, 13–23 → 1–11 PM
            else -> hour
        }
    }

    /**
     * Converts a list of [Mission] objects to a list of [MissionItem] UI components,
     * filling any remaining slots with placeholders.
     *
     * @param alarmMission The list of missions associated with the alarm.
     * @return A list of mission items, including placeholders if there are fewer missions than available slots.
     */
    fun getMissionItemList(alarmMission: List<Mission>): List<MissionItem> {
        val totalMissionSlots = MissionType.getAvailableMissionCount()
        return List(totalMissionSlots) { index ->
            alarmMission.getOrNull(index)?.let {
                MissionItem.MissionData(it)
            } ?: MissionItem.Placeholder(index)
        }
    }

    /**
     * Formats a string showing the remaining available mission slots.
     *
     * @param selectedMissions The list of selected missions.
     * @return A formatted string showing the number of selected and available mission slots.
     */
    fun getFormattedMissionSlotText(selectedMissions: List<Mission>): String {
        val selectedMissionSlots = numberFormatter.formatLocalizedNumber(selectedMissions.size.toLong(), false)
        val totalMissionSlots = numberFormatter.formatLocalizedNumber(MissionType.getAvailableMissionCount().toLong(), false)
        return resourceProvider.getString(R.string.remaining_mission_slot_count, selectedMissionSlots, totalMissionSlots)
    }

    /**
     * Formats a string showing the snooze interval and limit.
     *
     * @param snoozeSettings The [SnoozeSettings] object containing the snooze interval and limit.
     * @return A formatted string showing the snooze settings (interval in minutes and limit).
     */
    fun getFormattedAlarmSnoozedText(snoozeSettings: SnoozeSettings): String {
        return "${numberFormatter.formatLocalizedNumber(snoozeSettings.snoozeIntervalMinutes.toLong(), false)} ${resourceProvider.getString(R.string.min)}," +
                " ${numberFormatter.formatLocalizedNumber(snoozeSettings.snoozeLimit.toLong(), false)} ${resourceProvider.getString(R.string.times)} "
    }
}



