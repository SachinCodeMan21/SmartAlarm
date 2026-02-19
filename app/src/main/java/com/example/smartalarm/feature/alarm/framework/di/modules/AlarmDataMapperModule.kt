package com.example.smartalarm.feature.alarm.framework.di.modules

import com.example.smartalarm.core.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.feature.alarm.framework.di.annotations.AlarmNotificationMapDataMapperKey
import com.example.smartalarm.feature.alarm.framework.notification.enums.AlarmNotificationDataMapperKey
import com.example.smartalarm.feature.alarm.framework.notification.mapper.MissedAlarmNotificationDataMapper
import com.example.smartalarm.feature.alarm.framework.notification.mapper.ScheduledAlarmNotificationDataMapper
import com.example.smartalarm.feature.alarm.framework.notification.mapper.SnoozedAlarmNotificationDataMapper
import com.example.smartalarm.feature.alarm.framework.notification.mapper.UpcomingAlarmNotificationDataMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton


/**
 * Dagger Hilt module that provides and binds various [AppNotificationDataMapper] implementations
 * for different types of alarm notification data into a map.
 *
 * This module is installed in the [SingletonComponent], and uses multibinding to map
 * each [AppNotificationDataMapper] to its corresponding [AlarmNotificationDataMapperKey].
 * This allows dynamic retrieval of data mappers based on the alarm notification type.
 */
@Module
@InstallIn(SingletonComponent::class)
object AlarmDataMapperModule {

    /**
     * Provides the [UpcomingAlarmNotificationDataMapper] bound to the [AlarmNotificationDataMapperKey.UPCOMING] key.
     *
     * @param mapper The data mapper instance to be provided.
     * @return The [AppNotificationDataMapper] instance for upcoming alarm notifications.
     */
    @Provides
    @IntoMap
    @Singleton
    @AlarmNotificationMapDataMapperKey(AlarmNotificationDataMapperKey.UPCOMING)
    fun provideUpcomingAlarmNotificationDataMapper(
        mapper: UpcomingAlarmNotificationDataMapper
    ): AppNotificationDataMapper<*, *> = mapper

    /**
     * Provides the [SnoozedAlarmNotificationDataMapper] bound to the [AlarmNotificationDataMapperKey.SNOOZED] key.
     *
     * @param mapper The data mapper instance to be provided.
     * @return The [AppNotificationDataMapper] instance for snoozed alarm notifications.
     */
    @Provides
    @IntoMap
    @Singleton
    @AlarmNotificationMapDataMapperKey(AlarmNotificationDataMapperKey.SNOOZED)
    fun provideSnoozedAlarmNotificationDataMapper(
        mapper: SnoozedAlarmNotificationDataMapper
    ): AppNotificationDataMapper<*, *> = mapper

    /**
     * Provides the [MissedAlarmNotificationDataMapper] bound to the [AlarmNotificationDataMapperKey.MISSED] key.
     *
     * @param mapper The data mapper instance to be provided.
     * @return The [AppNotificationDataMapper] instance for missed alarm notifications.
     */
    @Provides
    @IntoMap
    @Singleton
    @AlarmNotificationMapDataMapperKey(AlarmNotificationDataMapperKey.MISSED)
    fun provideMissedAlarmNotificationDataMapper(
        mapper: MissedAlarmNotificationDataMapper
    ): AppNotificationDataMapper<*, *> = mapper

    /**
     * Provides the [ScheduledAlarmNotificationDataMapper] bound to the [AlarmNotificationDataMapperKey.SCHEDULED] key.
     *
     * @param mapper The data mapper instance to be provided.
     * @return The [AppNotificationDataMapper] instance for scheduled alarm notifications.
     */
    @Provides
    @IntoMap
    @Singleton
    @AlarmNotificationMapDataMapperKey(AlarmNotificationDataMapperKey.SCHEDULED)
    fun provideScheduledAlarmNotificationDataMapper(
        mapper: ScheduledAlarmNotificationDataMapper
    ): AppNotificationDataMapper<*, *> = mapper
}
