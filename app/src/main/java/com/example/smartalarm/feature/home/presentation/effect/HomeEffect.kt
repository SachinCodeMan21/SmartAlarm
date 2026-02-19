package com.example.smartalarm.feature.home.presentation.effect

import com.example.smartalarm.core.notification.model.NotificationIntentData
import com.example.smartalarm.feature.home.presentation.viewmodel.HomeViewModel

/**
 * Represents all possible UI effects emitted by the Home Activity feature.
 *
 * These effects are triggered by the [HomeViewModel] and observed by the HomeActivity to manage UI
 * transitions, actions, and other one-time effects such as navigation and icon animations.
 */
sealed class HomeEffect {

    /**
     * Triggers navigation to a specific child fragment within the Home Activity.
     *
     * This effect is emitted when the user navigates to a new destination, such as when a menu item
     * is selected or a notification is clicked. The destination ID is used to determine which fragment
     * to navigate to.
     *
     * @param destinationId The ID of the destination to navigate to.
     */
    data class NavigateToChildFragment(val destinationId: Int) : HomeEffect()

    /**
     * Handles navigation triggered by a notification click.
     *
     * This effect provides the necessary data (destination ID, notification action, etc.)
     * to navigate based on the notification's content, such as opening a timer or switching fragments.
     *
     * @param notificationIntentData Contains details like destination ID, action, and additional data.
     */
    data class HandleNotificationNavigation(val notificationIntentData: NotificationIntentData) : HomeEffect()

    /**
     * Animates the rotation of the selected bottom navigation item icon.
     *
     * This effect is triggered when a user interacts with a navigation item, providing a visual cue.
     *
     * @param bottomNavItemId The ID of the bottom navigation item to animate.
     */
    data class RotateSelectedNavItemIcon(val bottomNavItemId: Int) : HomeEffect()

    /**
     * Represents the effect to finish the current activity.
     *
     * This effect is typically triggered when the user presses the back button or when the activity
     * needs to be closed programmatically.
     */
    object FinishActivity : HomeEffect()

}

