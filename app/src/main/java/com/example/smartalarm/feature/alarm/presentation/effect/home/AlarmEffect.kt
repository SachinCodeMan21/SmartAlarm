package com.example.smartalarm.feature.alarm.presentation.effect.home

/**
 * Represents one-time side effects or UI actions within the alarm feature.
 *
 * These effects are emitted by [com.example.smartalarm.feature.alarm.presentation.viewmodel.home.AlarmViewModel] and observed by the UI layer (e.g., [com.example.smartalarm.feature.alarm.presentation.view.fragment.home.AlarmFragment])
 * to trigger actions such as permission requests, navigation, service control, or displaying messages.
 *
 * Each effect corresponds to a discrete UI event or operation that should be handled exactly once.
 *
 * Typical uses include:
 * - Requesting notification or exact alarm permissions based on user actions.
 * - Navigating to alarm creation or editing screens.
 * - Stopping the alarm service.
 * - Showing transient UI feedback like SnackBar,ShowToast messages.
 */
sealed class AlarmEffect {


    /**
     * Effect to navigate to the screen for creating a new alarm.
     */
    object NavigateToCreateAlarmScreen : AlarmEffect()

    /**
     * Effect to navigate to the screen for editing an existing alarm.
     *
     * @param alarmId The ID of the alarm to edit.
     */
    data class NavigateToEditAlarmScreen(val alarmId: Int) : AlarmEffect()


    /**
     * Effect to stop the alarm service.
     */
    object  StopAlarmService : AlarmEffect()

    /**
     * Effect to show a SnackBar message, typically after an alarm item is swiped.
     *
     * @param swipedAlarmId The ID of the alarm related to the SnackBar message.
     */
    data class ShowSnackBarMessage(val swipedAlarmId: Int) : AlarmEffect()

    /**
     *  Represents an effect to show a toast message with the given message.
     */
    data class ShowToastMessage(val toastMessage: String) : AlarmEffect()

}