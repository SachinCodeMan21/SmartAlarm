package com.example.smartalarm.feature.alarm.framework.di.modules

import com.example.smartalarm.core.notification.builder.AppNotificationBuilder
import com.example.smartalarm.feature.alarm.framework.di.annotations.AlarmNotificationBuilderMapKey
import com.example.smartalarm.feature.alarm.framework.notification.builder.MissedAlarmNotificationBuilder
import com.example.smartalarm.feature.alarm.framework.notification.builder.ScheduledAlarmNotificationBuilder
import com.example.smartalarm.feature.alarm.framework.notification.builder.SnoozedAlarmNotificationBuilder
import com.example.smartalarm.feature.alarm.framework.notification.builder.UpcomingAlarmNotificationBuilder
import com.example.smartalarm.feature.alarm.framework.notification.enums.AlarmNotificationBuilderTypeKey
import com.example.smartalarm.feature.alarm.framework.notification.factory.AlarmNotificationBuilderFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides and binds various [AppNotificationBuilder] implementations
 * for different types of alarm notifications into a map.
 *
 * This module is installed in the [SingletonComponent], ensuring singleton scope for all provided builders.
 * Each builder is mapped to a specific [AlarmNotificationBuilderTypeKey] using multibinding,
 * allowing easy retrieval based on the notification type.
 *
 * These builders are later injected into [AlarmNotificationBuilderFactory], which selects
 * the appropriate builder based on the notification type key to construct
 * alarm-related notifications dynamically.
 */
@Module
@InstallIn(SingletonComponent::class)
object AlarmNotificationBuilderModule {

    /**
     * Provides the [UpcomingAlarmNotificationBuilder] bound to the [AlarmNotificationBuilderTypeKey.UPCOMING] key.
     *
     * @param builder The builder instance to be provided.
     * @return The [AppNotificationBuilder] instance for upcoming alarms.
     */
    @Provides
    @Singleton
    @IntoMap
    @AlarmNotificationBuilderMapKey(AlarmNotificationBuilderTypeKey.UPCOMING)
    fun provideUpcomingAlarmBuilder(
        builder: UpcomingAlarmNotificationBuilder
    ): AppNotificationBuilder<*> = builder

    /**
     * Provides the [SnoozedAlarmNotificationBuilder] bound to the [AlarmNotificationBuilderTypeKey.SNOOZED] key.
     *
     * @param builder The builder instance to be provided.
     * @return The [AppNotificationBuilder] instance for snoozed alarms.
     */
    @Provides
    @Singleton
    @IntoMap
    @AlarmNotificationBuilderMapKey(AlarmNotificationBuilderTypeKey.SNOOZED)
    fun provideSnoozedAlarmBuilder(
        builder: SnoozedAlarmNotificationBuilder
    ): AppNotificationBuilder<*> = builder

    /**
     * Provides the [MissedAlarmNotificationBuilder] bound to the [AlarmNotificationBuilderTypeKey.MISSED] key.
     *
     * @param builder The builder instance to be provided.
     * @return The [AppNotificationBuilder] instance for missed alarms.
     */
    @Provides
    @Singleton
    @IntoMap
    @AlarmNotificationBuilderMapKey(AlarmNotificationBuilderTypeKey.MISSED)
    fun provideMissedAlarmBuilder(
        builder: MissedAlarmNotificationBuilder
    ): AppNotificationBuilder<*> = builder

    /**
     * Provides the [ScheduledAlarmNotificationBuilder] bound to the [AlarmNotificationBuilderTypeKey.SCHEDULED] key.
     *
     * @param builder The builder instance to be provided.
     * @return The [AppNotificationBuilder] instance for scheduled alarms.
     */
    @Provides
    @Singleton
    @IntoMap
    @AlarmNotificationBuilderMapKey(AlarmNotificationBuilderTypeKey.SCHEDULED)
    fun provideScheduledAlarmBuilder(
        builder: ScheduledAlarmNotificationBuilder
    ): AppNotificationBuilder<*> = builder
}
