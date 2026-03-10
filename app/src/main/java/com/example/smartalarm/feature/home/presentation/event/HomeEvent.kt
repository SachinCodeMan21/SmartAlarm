package com.example.smartalarm.feature.home.presentation.event

import com.example.smartalarm.core.framework.notification.model.NotificationIntentData

/**
 * Defines all user and system events that can occur on the Home screen.
 *
 * HomeEvent acts as the single source of truth for interactions originating
 * from HomeActivity. These events are processed by HomeViewModel to drive
 * navigation and other UI behavior.
 *
 * This pattern helps:
 * - Centralize event handling
 * - Keep UI logic out of the Activity
 * - Maintain a predictable event-driven flow
 */
sealed class HomeEvent {

    /** Restores the last opened destination when the app starts or resumes. */
    object RestoreLastOpenedDestination : HomeEvent()

    /**
     * Triggered when the app is opened via a notification.
     *
     * @param notificationIntentData Data extracted from the notification intent.
     */
    data class NavigateFromNotification(
        val notificationIntentData: NotificationIntentData
    ) : HomeEvent()

    /**
     * Requests navigation to a specific child fragment in the Home graph.
     *
     * @param destinationId Navigation destination resource ID.
     */
    data class NavigateToChildFragment(
        val destinationId: Int
    ) : HomeEvent()

    /**
     * Triggered when a navigation menu item is selected.
     *
     * @param selectedDestinationId Destination associated with the selected menu item.
     */
    data class NavMenuItemSelected(
        val selectedDestinationId: Int
    ) : HomeEvent()

    /** Triggered when the system back button is pressed. */
    object SystemBackPressed : HomeEvent()
}