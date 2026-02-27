package com.example.smartalarm.feature.home.presentation.event

import com.example.smartalarm.core.framework.notification.model.NotificationIntentData

/**
 * Represents various events triggered by user actions or system interactions in HomeActivity.
 * These events manage navigation, system back presses, and handling data passed through notifications.
 *
 * The purpose of this sealed class is to centralize all navigation and system interaction events for HomeActivity,
 * providing a clean separation of concerns and enabling the ViewModel to respond appropriately to UI changes.
 *
 * Key Responsibilities:
 * - **RestoreLastOpenedDestination**: Ensures the app restores the last opened screen when resumed.
 * - **NavigateFromNotification**: Directs the user to the relevant screen based on notification data.
 * - **NavigateToChildFragment**: Initiates navigation to a specific fragment within the app.
 * - **NavMenuItemSelected**: Triggers navigation when a menu item is selected.
 * - **SystemBackPressed**: Intercepts the back press event to properly manage activity termination.
 */
sealed class HomeEvent {

    /**
     * Restores the last opened destination when the app is resumed.
     * Used to ensure the user returns to their previous screen.
     */
    object RestoreLastOpenedDestination : HomeEvent()


    /**
     * Navigates based on notification data, such as a timer or alarm.
     * Ensures the user is directed to the appropriate screen when interacting with a notification.
     *
     * @param notificationIntentData Data from the notification used for navigation.
     */
    data class NavigateFromNotification(val notificationIntentData: NotificationIntentData) : HomeEvent()


    /**
     * Represents a request to navigate to a specific child fragment.
     *
     * This event is used to trigger navigation to the appropriate fragment, ensuring a decoupled navigation flow
     * that is independent of the UI state. It allows navigation logic to remain clean and maintainable.
     *
     * @param destinationId The ID of the fragment to navigate to.
     */
    data class NavigateToChildFragment(val destinationId: Int) : HomeEvent()


    /**
     * Handles the selection of a navigation menu item and triggers the corresponding navigation action.
     *
     * @param selectedDestinationId ID of the selected menu item’s destination.
     */
    data class NavMenuItemSelected(val selectedDestinationId: Int) : HomeEvent()


    /**
     * Manages the system back press event to intercept the normal top level fragment navigate up to close the activity instead.
     */
    object SystemBackPressed : HomeEvent()
}
