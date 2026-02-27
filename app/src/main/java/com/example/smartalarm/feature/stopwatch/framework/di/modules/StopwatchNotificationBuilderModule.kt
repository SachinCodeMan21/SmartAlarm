package com.example.smartalarm.feature.stopwatch.framework.di.modules

import com.example.smartalarm.core.framework.notification.builder.AppNotificationBuilder
import com.example.smartalarm.feature.stopwatch.framework.di.annotations.StopwatchNotificationBuilderMapKey
import com.example.smartalarm.feature.stopwatch.framework.notification.builder.ActiveStopwatchNotificationBuilder
import com.example.smartalarm.feature.stopwatch.framework.notification.enums.StopwatchNotificationBuilderTypeKey
import com.example.smartalarm.feature.stopwatch.framework.notification.factory.StopwatchNotificationBuilderFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

/**
 * Dagger Hilt module that provides and binds [AppNotificationBuilder] implementations
 * for different types of stopwatch notifications into a map.
 *
 * This module is installed in the [SingletonComponent], ensuring singleton scope
 * for all provided builders. Each builder is mapped to a specific
 * [StopwatchNotificationBuilderTypeKey] using multibinding, allowing easy retrieval
 * of the correct builder based on the notification type.
 */
@Module
@InstallIn(SingletonComponent::class)
object StopwatchNotificationBuilderModule {

    /**
     * Provides the [ActiveStopwatchNotificationBuilder] and binds it to the
     * [StopwatchNotificationBuilderTypeKey.ACTIVE_STOPWATCH] key.
     *
     * This allows the [StopwatchNotificationBuilderFactory] to retrieve the correct
     * builder when building notifications for active stopwatches.
     *
     * @param builder The builder instance to be provided.
     * @return The [AppNotificationBuilder] instance for active stopwatch notifications.
     */
    @Provides
    @IntoMap
    @StopwatchNotificationBuilderMapKey(StopwatchNotificationBuilderTypeKey.ACTIVE_STOPWATCH)
    fun provideActiveStopwatchBuilder(
        builder: ActiveStopwatchNotificationBuilder
    ): AppNotificationBuilder<*> = builder
}
