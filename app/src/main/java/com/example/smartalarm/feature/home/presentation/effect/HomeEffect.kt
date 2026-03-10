package com.example.smartalarm.feature.home.presentation.effect

import com.example.smartalarm.core.framework.notification.model.NotificationIntentData

/**
 * Represents one-time UI effects emitted by HomeViewModel.
 *
 * Effects are consumed by HomeActivity to perform UI actions such as
 * navigation, animations, or finishing the activity. Unlike state,
 * effects are not persisted and should only occur once.
 */
sealed class HomeEffect {

    /**
     * Requests navigation to a specific fragment destination.
     *
     * @param destinationId Navigation destination resource ID.
     */
    data class NavigateToChildFragment(
        val destinationId: Int
    ) : HomeEffect()

    /**
     * Handles navigation triggered by a notification interaction.
     *
     * @param notificationIntentData Data extracted from the notification intent.
     */
    data class HandleNotificationNavigation(
        val notificationIntentData: NotificationIntentData
    ) : HomeEffect()

    /**
     * Triggers a rotation animation for the selected navigation item icon.
     *
     * @param bottomNavItemId ID of the navigation item to animate.
     */
    data class RotateSelectedNavItemIcon(
        val bottomNavItemId: Int
    ) : HomeEffect()

    /** Requests the activity to finish. */
    object FinishActivity : HomeEffect()
}

