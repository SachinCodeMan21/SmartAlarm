package com.example.smartalarm.feature.alarm.presentation.effect.editor

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings

/**
 * Represents one-time UI side effects (navigation, dialogs, bottom sheets, etc.)
 * triggered by user actions or internal events in the Alarm Editor screen.
 *
 * These effects should not be part of the persistent UI state, and are typically
 * handled once (e.g., using a `SingleLiveEvent`, `SharedFlow`, or `Channel`).
 */
sealed class AlarmEditorEffect {


    // Navigation Effects
    /**
     * Effect triggered to finish the alarm editor activity and return to the previous screen (typically the home screen).
     * This happens after saving, updating, or cancelling the alarm.
     */
    object FinishEditorActivity : AlarmEditorEffect()


    /**
     * Effect to navigate to the `AlarmActivity` and start the alarm mission preview.
     * This allows the user to review how the mission flow will look before confirming.
     *
     * @param previewAlarmModel The `AlarmModel` containing alarm data for the mission preview.
     */
    data class NavigateToAlarmActivityForMissionPreview(val previewAlarmModel : AlarmModel) : AlarmEditorEffect()


    /**
     * Effect to navigate from the alarm editor to the snooze settings fragment.
     * Passes the current snooze settings to allow editing.
     *
     * @param snoozeSettings The current snooze settings to be edited.
     */
    data class NavigateToSnoozeAlarmFragment(val snoozeSettings: SnoozeSettings) : AlarmEditorEffect()


    /**
     * Effect to launch the alarm sound (ringtone) picker.
     *
     * This effect opens the system's ringtone picker, allowing the user to select or change the alarm sound.
     * If an existing sound URI is provided, it will be pre-selected in the picker.
     *
     * @param existingAlarmSound The URI of the current alarm sound, if any. If not available, this can be an empty string or `null`.
     */
    data class LaunchAlarmSoundPicker(val existingAlarmSound: String) : AlarmEditorEffect()




    // Show Mission Picker, SelectedMission BottomSheet

    /**
     * Effect to show the mission picker bottom sheet for selecting or editing a mission.
     *
     * @param position The position of the mission in the list.
     * @param existingMission The current mission at that position (or null for new ones).
     * @param usedMissions List of missions already used to avoid duplicates.
     */
    data class ShowMissionPickerBottomSheet(val position: Int, val existingMission: Mission?, val usedMissions: List<Mission>) : AlarmEditorEffect()

    /**
     * Effect to display the selected mission details in a bottom sheet.
     *
     * @param position The position in the mission list this mission is associated with.
     * @param selectedMission The mission selected by the user.
     */
    data class ShowSelectedMissionBottomSheet(val position: Int, val selectedMission: Mission) : AlarmEditorEffect()


    // Show Loader, Toast

    /**
     * Effect to show or hide a loading indicator during the save/update process.
     *
     * @param isLoading True to show the loading indicator, false to hide it.
     */
    data class ShowSaveUpdateLoadingIndicator(val isLoading: Boolean) : AlarmEditorEffect()


    /**
     * Effect to show a toast message to the user.
     * Typically used for status updates or error messages.
     *
     * @param toastMessage The message to be displayed in the toast.
     */
    data class ShowToastMessage(val toastMessage: String) : AlarmEditorEffect()

    /**
     * Effect to display an error message to the user (e.g., via a SnackBar or Toast).
     *
     * @param error The human-readable error message to be displayed.
     */
    data class ShowError(val error: DataError) : AlarmEditorEffect()

}