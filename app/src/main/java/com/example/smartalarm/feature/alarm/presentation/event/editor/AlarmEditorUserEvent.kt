package com.example.smartalarm.feature.alarm.presentation.event.editor

import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.presentation.view.fragment.editor.SnoozeAlarmFragment
import com.example.smartalarm.feature.alarm.presentation.viewmodel.editor.AlarmEditorViewModel

/**
 * Sealed class representing various user events within the alarm editor screen.
 * These events capture and handle user interactions related to configuring, editing, and managing alarms.
 * The events encompass changes in alarm settings (such as label, time, sound, and recurrence), as well as interactions with alarm missions (including adding, selecting, previewing, and updating missions).
 * Additionally, the events manage settings for vibration, volume, ringtone, snooze duration, and day-of-week toggling.
 *
 * The events are used to trigger actions and updates within the alarm editor UI, and are typically consumed by a ViewModel
 * or a similar controller to update the alarm's state or navigate to other parts of the app (e.g., navigating to the snooze screen, alarm sound picker, etc.).
 *
 * Event categories:
 *
 * 1. **Alarm Settings**: Handling user actions for setting the alarm label, time, recurrence, and days of the week.
 * 2. **Mission Settings**: Handling interactions with alarm missions, such as adding, selecting, previewing, and updating missions.
 * 3. **Sound Settings**: Managing volume, vibration, and ringtone settings.
 * 4. **Snooze Settings**: Managing snooze-related settings, such as modifying the snooze duration.
 * 5. **Saving/Updating Alarm**: Saving or updating the alarm after modifications have been made.
 *
 * Each event includes necessary parameters for making updates to the alarm or navigating between views based on user interaction.
 */
sealed class AlarmEditorUserEvent {


    // Represents events related to editing the alarm label, time, daily recurrence, and toggling days of the week.

    /**
     * Triggered when the user changes the alarm's label.
     * This event carries the new label text that will be applied to the alarm.
     *
     * @param label The new label to be set for the alarm.
     */
    data class LabelChanged(val label: String) : AlarmEditorUserEvent()

    /**
     * Triggered when the user changes the alarm's time.
     * This event carries the new time information, including the hour, minute, and AM/PM setting.
     *
     * @param hour The hour part of the new alarm time (in 12-hour format).
     * @param minute The minute part of the new alarm time.
     * @param amPm The AM/PM value (1 for AM, 2 for PM).
     */
    data class TimeChanged(val hour: Int, val minute: Int, val amPm: Int) : AlarmEditorUserEvent()

    /**
     * Triggered when the user toggles the daily recurrence setting for the alarm.
     * This event carries a boolean indicating whether the alarm is set to repeat daily.
     *
     * @param isDaily A boolean indicating if the alarm should repeat daily (true) or not (false).
     */
    data class IsDailyChanged(val isDaily: Boolean) : AlarmEditorUserEvent()

    /**
     * Triggered when the user toggles the selection of a specific day for the alarm.
     * This event carries the index of the day that was toggled (e.g., 0 for Sunday, 1 for Monday, etc.).
     *
     * @param toggleDayIndex The index of the day that was toggled (0 to 6, where 0 is Sunday).
     */
    data class DayToggled(val toggleDayIndex: Int) : AlarmEditorUserEvent()




    // Represents events related to interacting with alarm missions (e.g., handling clicks on mission items).

    /**
     * Triggered when the user clicks on the mission placeholder (when no mission items are present).
     * This event carries the position of the placeholder in the RecyclerView, which will be used to
     * determine where to add or display a new mission item.
     *
     * @param position The position of the mission placeholder in the list.
     */
    data class HandleMissionItemPlaceHolderClick(val position: Int) : AlarmEditorUserEvent()

    /**
     * Triggered when the user clicks on an existing mission item in the list.
     * This event carries the position of the clicked mission item and the mission object, allowing
     * the editor to display or modify details for the selected mission.
     *
     * @param position The position of the clicked mission item in the list.
     * @param existingMission The mission object that was clicked, containing relevant data about the mission.
     */
    data class HandleMissionItemClick(val position: Int, val existingMission: Mission) : AlarmEditorUserEvent()

    /**
     * Triggered when the user clicks to remove an existing mission item.
     * This event carries the position of the mission item to be removed, allowing the editor to
     * update the mission list accordingly.
     *
     * @param position The position of the mission item to be removed in the list.
     */
    data class HandleRemoveMissionClick(val position: Int) : AlarmEditorUserEvent()



    // Represents events for [ showing dummy mission preview ,  selecting or updating an alarm mission ].

    /**
     * Triggered when the user clicks the "preview" button to see how a selected mission will look like in the alarm flow.
     * This event carries the selected mission object, and it navigates to the `AlarmActivity` to show a dummy preview
     * of how the mission will appear and flow, allowing the user to review it before selection.
     *
     * @param previewMission The mission to be previewed by the user, allowing them to see a mockup of the mission flow.
     */
    data class StartAlarmMissionPreview(val previewMission : Mission) : AlarmEditorUserEvent()

    /**
     * Triggered when the user selects a specific mission for the alarm from the picker.
     * This event carries the position of the selected mission item and the corresponding mission object,
     * allowing the alarm editor to update the alarm with the selected mission details.
     *
     * @param position The position of the selected mission in the list of available missions.
     * @param selectedMission The mission object that was selected by the user from the picker.
     */
    data class AlarmMissionSelected(val position: Int, val selectedMission: Mission) : AlarmEditorUserEvent()

    /**
     * Triggered when the user updates the mission details from the selected mission's bottom sheet.
     * This event carries the position of the mission to be updated and the new mission details,
     * allowing the alarm editor to update the mission in the alarm settings.
     *
     * @param position The position of the mission item in the list that is being updated.
     * @param mission The updated mission object that will replace the previous mission details in the alarm.
     */
    data class UpdateAlarmMission(val position: Int, val mission: Mission) : AlarmEditorUserEvent()




    // Represents events related to alarm sound settings (volume and vibration), and selecting ringtones.

    /**
     * Triggered when the user changes the alarm volume.
     * This event carries the new volume value, which will be applied to the alarm's sound settings.
     *
     * @param volume The new volume level (typically on a scale from 0 to 100).
     */
    data class VolumeChanged(val volume: Int) : AlarmEditorUserEvent()

    /**
     * Triggered when the user toggles the vibration setting for the alarm.
     * This event carries a boolean indicating whether vibration is enabled or disabled.
     *
     * @param isEnabled A boolean indicating if vibration is enabled (true) or disabled (false).
     */
    data class VibrationToggled(val isEnabled: Boolean) : AlarmEditorUserEvent()

    /**
     * Triggered when the user selects to launch the alarm sound picker.
     * This event signals that the user wants to choose a new sound for the alarm.
     */
    object LaunchAlarmSoundPicker : AlarmEditorUserEvent()

    /**
     * Triggered when the user selects a specific ringtone.
     * This event carries the URI of the selected ringtone, which will be set as the alarm sound.
     *
     * @param uri The URI pointing to the selected ringtone file.
     */
    data class RingtoneSelected(val uri: String) : AlarmEditorUserEvent()




    // Represents navigation to snooze screen ,  saving / updating  the alarm.

    /**
     * Triggered when the user clicks to edit the snooze time for an alarm.
     * This event navigates the user to the `EditSnoozeFragment`, where they can
     * modify the snooze duration and related details for the alarm.
     */
    object EditSnoozeClick : AlarmEditorUserEvent()

    /**
     * Triggered when the user clicks to save or update the current alarm.
     * This event is used to save any changes made to the alarm (such as time, label, or snooze)
     * or update the alarm if it already exists.
     */
    object SaveOrUpdateAlarmClick : AlarmEditorUserEvent()

    /**
     * Event triggered when the system back button is pressed.
     *
     * This is typically handled by Android's hardware back button or the back gesture in gesture navigation.
     * The event is captured and forwarded to the [AlarmEditorViewModel] for appropriate action.
     */
    object OnSystemBackPressed : AlarmEditorUserEvent()

    /**
     * Event triggered when is in [SnoozeAlarmFragment] and the user clicks the back button in the toolbar (app bar).
     *
     * The toolbar's "Up" button is typically used for navigation within the app.
     * Pressing this button will send the event to the [AlarmEditorViewModel] to handle back navigation logic.
     */
    object OnToolbarBackPressed : AlarmEditorUserEvent()

}
