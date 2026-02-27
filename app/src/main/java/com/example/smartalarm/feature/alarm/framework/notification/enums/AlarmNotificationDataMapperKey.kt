package com.example.smartalarm.feature.alarm.framework.notification.enums

import com.example.smartalarm.core.framework.notification.marker.AppNotificationDataMapperKey
import com.example.smartalarm.core.framework.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationData
import com.example.smartalarm.core.framework.notification.factory.NotificationDataMapperFactory

/**
 * Enum representing keys for selecting specific [AppNotificationDataMapper] implementations
 * for alarm notifications.
 *
 * Each key identifies a mapper responsible for converting an [AlarmModel] into
 * [AlarmNotificationData] suitable for a particular alarm notification state:
 *
 * - [UPCOMING]: Maps data for alarms that are upcoming, before they ring.
 * - [SNOOZED]: Maps data for alarms that have been snoozed.
 * - [MISSED]: Maps data for alarms that were missed by the user.
 * - [SCHEDULED]: Maps data for alarms that are currently scheduled or actively ringing.
 *
 * These keys are used by [NotificationDataMapperFactory] to retrieve the correct
 * mapper for building alarm notifications dynamically at runtime.
 */
enum class AlarmNotificationDataMapperKey : AppNotificationDataMapperKey {
    UPCOMING,
    SNOOZED,
    MISSED,
    SCHEDULED
}
