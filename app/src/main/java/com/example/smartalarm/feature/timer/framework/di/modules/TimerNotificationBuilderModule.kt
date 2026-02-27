package com.example.smartalarm.feature.timer.framework.di.modules

import com.example.smartalarm.core.framework.notification.builder.AppNotificationBuilder
import com.example.smartalarm.feature.timer.framework.di.annotations.TimerNotificationBuilderMapKey
import com.example.smartalarm.feature.timer.framework.notification.builder.ActiveTimerNotificationBuilder
import com.example.smartalarm.feature.timer.framework.notification.builder.CompletedTimerNotificationBuilder
import com.example.smartalarm.feature.timer.framework.notification.builder.MissedTimerNotificationBuilder
import com.example.smartalarm.feature.timer.framework.notification.enums.TimerNotificationBuilderTypeKey
import com.example.smartalarm.feature.timer.framework.notification.factory.TimerNotificationBuilderFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

/**
 * Dagger Hilt module that provides and binds [AppNotificationBuilder] implementations
 * for different types of timer notifications into a map.
 *
 * This module is installed in the [SingletonComponent], ensuring singleton scope
 * for all provided builders. Each builder is mapped to a specific
 * [TimerNotificationBuilderTypeKey] using multibinding, allowing the corresponding
 * [TimerNotificationBuilderFactory] to retrieve the correct builder dynamically
 * based on the notification type.
 */
@Module
@InstallIn(SingletonComponent::class)
object TimerNotificationBuilderModule {

    /**
     * Provides the [ActiveTimerNotificationBuilder] and binds it to the
     * [TimerNotificationBuilderTypeKey.ACTIVE] key.
     *
     * This allows the [TimerNotificationBuilderFactory] to retrieve the correct
     * builder when constructing notifications for active timers.
     *
     * @param builder The builder instance to be provided.
     * @return The [AppNotificationBuilder] instance for active timer notifications.
     */
    @Provides
    @IntoMap
    @TimerNotificationBuilderMapKey(TimerNotificationBuilderTypeKey.ACTIVE)
    fun provideActiveTimerBuilder(
        builder: ActiveTimerNotificationBuilder
    ): AppNotificationBuilder<*> = builder

    /**
     * Provides the [CompletedTimerNotificationBuilder] and binds it to the
     * [TimerNotificationBuilderTypeKey.COMPLETED] key.
     *
     * This allows the [TimerNotificationBuilderFactory] to retrieve the correct
     * builder when constructing notifications for completed timers.
     *
     * @param builder The builder instance to be provided.
     * @return The [AppNotificationBuilder] instance for completed timer notifications.
     */
    @Provides
    @IntoMap
    @TimerNotificationBuilderMapKey(TimerNotificationBuilderTypeKey.COMPLETED)
    fun provideCompletedTimerBuilder(
        builder: CompletedTimerNotificationBuilder
    ): AppNotificationBuilder<*> = builder

    @Provides
    @IntoMap
    @TimerNotificationBuilderMapKey(TimerNotificationBuilderTypeKey.MISSED)
    fun provideMissedTimerBuilder(
        builder: MissedTimerNotificationBuilder
    ): AppNotificationBuilder<*> = builder
}
