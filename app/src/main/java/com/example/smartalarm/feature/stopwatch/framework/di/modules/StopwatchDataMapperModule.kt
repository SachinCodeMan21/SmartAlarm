package com.example.smartalarm.feature.stopwatch.framework.di.modules

import com.example.smartalarm.core.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.feature.stopwatch.framework.di.annotations.StopwatchNotificationMapDataMapperKey
import com.example.smartalarm.feature.stopwatch.framework.notification.enums.StopwatchNotificationDataMapperKey
import com.example.smartalarm.feature.stopwatch.framework.notification.mapper.ActiveStopwatchNotificationDataMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

/**
 * Dagger Hilt module providing stopwatch notification data mappers.
 *
 * This module uses multi-binding to map specific [StopwatchNotificationDataMapperKey]
 * values to their corresponding [AppNotificationDataMapper] implementations.
 *
 * This allows the app to retrieve the correct data mapper dynamically based on
 * the key, supporting multiple stopwatch notification types in a scalable way.
 */
@Module
@InstallIn(SingletonComponent::class)
object StopwatchDataMapperModule {

    /**
     * Provides the [ActiveStopwatchNotificationDataMapper] into the Dagger map.
     *
     * The mapper is bound to the [StopwatchNotificationDataMapperKey.ACTIVE] key,
     * allowing injection of the correct mapper when this key is requested.
     *
     * @param mapper The [ActiveStopwatchNotificationDataMapper] instance to bind.
     * @return The mapper as [AppNotificationDataMapper<*, *>] for multi-binding.
     */
    @Provides
    @IntoMap
    @StopwatchNotificationMapDataMapperKey(StopwatchNotificationDataMapperKey.ACTIVE)
    fun provideStopwatchActiveDataMapper(
        mapper: ActiveStopwatchNotificationDataMapper
    ): AppNotificationDataMapper<*, *> = mapper
}
