package com.example.smartalarm.core.framework.notification.mapper

import android.content.Context
import com.example.smartalarm.core.framework.notification.model.AppNotificationData
import com.example.smartalarm.core.framework.notification.model.AppNotificationModel

/**
 * Generic mapper interface for converting a domain-level notification model
 * into platform-specific notification data ([AppNotificationData]).
 *
 * This interface defines a contract for mapping any domain model that implements
 * [AppNotificationModel] into a concrete notification data object suitable for
 * building a notification in the Android system.
 *
 * @param DomainModel The type of the source domain model, must extend [AppNotificationModel].
 * @param NotificationData The type of notification data returned, must implement [AppNotificationData].
 *
 * Implementations of this interface encapsulate the logic required to extract and
 * format information from domain models (e.g., AlarmModel, TimerModel, StopwatchModel)
 * into notification-ready data, such as titles, content text, actions, and intents.
 */
interface AppNotificationDataMapper<in DomainModel : AppNotificationModel<*, NotificationData, *>, NotificationData : AppNotificationData> {

    /**
     * Converts the given domain model into notification data.
     *
     * @param context Android [Context] for accessing resources, formatting strings,
     * or any other system services required during mapping.
     * @param model The domain model instance to be mapped to notification data.
     * @return A [NotificationData] object encapsulating all UI-relevant information
     *         needed to display the notification.
     */
    fun map(context: Context, model: DomainModel): NotificationData
}
