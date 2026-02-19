package com.example.smartalarm.feature.timer.framework.notification.enums

import com.example.smartalarm.core.notification.marker.AppNotificationBuilderTypeKey
import com.example.smartalarm.core.notification.builder.AppNotificationBuilder
import com.example.smartalarm.core.notification.factory.AppNotificationBuilderFactory

/**
 * Enum representing the distinct types of timer notifications.
 *
 * Each enum value acts as a key for selecting the appropriate
 * [AppNotificationBuilder] implementation via [AppNotificationBuilderFactory].
 *
 * - [ACTIVE]: Represents an ongoing timer notification.
 * - [COMPLETED]: Represents a timer that has finished.
 */
enum class TimerNotificationBuilderTypeKey : AppNotificationBuilderTypeKey {
    ACTIVE, COMPLETED,MISSED
}
