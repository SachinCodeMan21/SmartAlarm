package com.example.smartalarm.feature.timer.framework.notification.enums

import com.example.smartalarm.core.framework.notification.marker.AppNotificationDataMapperKey
import com.example.smartalarm.core.framework.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.core.framework.notification.factory.NotificationDataMapperFactory
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.framework.notification.model.TimerNotificationData

/**
 * Enum representing keys for selecting specific [AppNotificationDataMapper] implementations
 * for timer notifications.
 *
 * Each key identifies a mapper responsible for converting a [TimerModel] into
 * [TimerNotificationData] suitable for a particular timer notification state.
 *
 * - [ACTIVE]: Maps data for timers that are currently running.
 * - [COMPLETED]: Maps data for timers that have finished.
 *
 * These keys are used by [NotificationDataMapperFactory] to retrieve the correct
 * mapper for building timer notifications dynamically at runtime.
 */
enum class TimerNotificationDataMapperKey : AppNotificationDataMapperKey {
    ACTIVE,
    COMPLETED,
    MISSED
}
