package com.example.smartalarm.core.notification.marker

import com.example.smartalarm.core.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.core.notification.factory.NotificationDataMapperFactory
import com.example.smartalarm.core.notification.model.AppNotificationModel

/**
 * Marker interface representing a key for selecting a specific [AppNotificationDataMapper].
 *
 * Implementations of this interface (commonly enums or singleton objects) uniquely identify
 * the type of notification data to be mapped. These keys are used by
 * [NotificationDataMapperFactory] to retrieve the correct mapper for a given
 * [AppNotificationModel].
 *
 * This design enables a type-safe, flexible system for mapping domain-level models
 * to platform-specific notification data across different notification types.
 */
interface AppNotificationDataMapperKey
