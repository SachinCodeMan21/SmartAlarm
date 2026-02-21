package com.example.smartalarm.feature.alarm.presentation.event.home

/**
 * Represents all possible user and system events related to alarms.
 *
 * These events are sent from the UI[com.example.smartalarm.feature.alarm.presentation.view.fragment.home.AlarmFragment] to the ViewModel[com.example.smartalarm.feature.alarm.presentation.viewmodel.home.AlarmViewModel] to trigger
 * state updates or side effects in the alarm feature.
 */


//sealed class AlarmEvent {
//
//
//    /**
//     * Event to request retrieval of all alarms.
//     */
//    object GetAllAlarms : AlarmEvent()
//
//    /**
//     * Event to initiate the creation of a new alarm.
//     */
//    object AddNewAlarm : AlarmEvent()
//
//    /**
//     * Event triggered when an alarm's enabled/disabled switch is toggled.
//     *
//     * @param toggledAlarmId The ID of the alarm being toggled.
//     * @param isEnabled The new enabled state of the alarm.
//     */
//    data class ToggleAlarm(val toggledAlarmId: Int, val isEnabled: Boolean) : AlarmEvent()
//
//    /**
//     * Event triggered when an alarm item is clicked.
//     *
//     * @param selectedAlarmId The ID of the selected alarm.
//     */
//    data class AlarmItemClicked(val selectedAlarmId: Int) : AlarmEvent()
//
//    /**
//     * Event triggered when an alarm item is swiped (typically for deletion).
//     *
//     * @param deletedAlarmId The ID of the alarm to delete.
//     */
//    data class AlarmItemSwiped(val deletedAlarmId: Int) : AlarmEvent()
//
//    /**
//     * Event to undo the last deleted alarm.
//     */
//    object UndoDeletedAlarm : AlarmEvent()
//
//    data class OnPermissionResult(val result: PermissionResult) : AlarmEvent()
//
//}


sealed class AlarmEvent {

    /**
     * Event to initiate the creation of a new alarm.
     */
    object AddNewAlarm : AlarmEvent()

    /**
     * Event triggered when an alarm's enabled/disabled switch is toggled.
     *
     * @param toggledAlarmId The ID of the alarm being toggled.
     * @param isEnabled The new enabled state of the alarm.
     */
    data class ToggleAlarm(val toggledAlarmId: Int, val isEnabled: Boolean) : AlarmEvent()

    /**
     * Event triggered when an alarm item is clicked.
     *
     * @param selectedAlarmId The ID of the selected alarm.
     */
    data class AlarmItemClicked(val selectedAlarmId: Int) : AlarmEvent()

    /**
     * Event triggered when an alarm item is swiped (typically for deletion).
     *
     * @param deletedAlarmId The ID of the alarm to delete.
     */
    data class AlarmItemSwiped(val deletedAlarmId: Int) : AlarmEvent()

    /**
     * Event to undo the last deleted alarm.
     */
    object UndoDeletedAlarm : AlarmEvent()

}