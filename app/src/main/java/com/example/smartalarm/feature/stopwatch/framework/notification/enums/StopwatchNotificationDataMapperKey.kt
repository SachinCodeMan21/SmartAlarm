package com.example.smartalarm.feature.stopwatch.framework.notification.enums

import com.example.smartalarm.core.framework.notification.marker.AppNotificationDataMapperKey
import com.example.smartalarm.core.framework.notification.factory.NotificationDataMapperFactory
import com.example.smartalarm.core.framework.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.framework.notification.model.StopwatchNotificationData

/**
 * Enum representing keys for selecting specific [AppNotificationDataMapper] implementations
 * for stopwatch notifications.
 *
 * Each key identifies a mapper responsible for converting a [StopwatchModel] into
 * [StopwatchNotificationData] suitable for a particular stopwatch notification state.
 *
 * - [ACTIVE]: Maps data for an actively running stopwatch.
 *
 * These keys are used by [NotificationDataMapperFactory] to retrieve the correct
 * mapper for building stopwatch notifications dynamically at runtime.
 */
enum class StopwatchNotificationDataMapperKey : AppNotificationDataMapperKey {
    ACTIVE
}
