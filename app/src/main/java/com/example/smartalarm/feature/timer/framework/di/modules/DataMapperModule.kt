package com.example.smartalarm.feature.timer.framework.di.modules

import com.example.smartalarm.core.notification.mapper.AppNotificationDataMapper
import com.example.smartalarm.feature.timer.framework.di.annotations.TimerNotificationMapDataMapperKey
import com.example.smartalarm.feature.timer.framework.notification.enums.TimerNotificationDataMapperKey
import com.example.smartalarm.feature.timer.framework.notification.mapper.ActiveTimerNotificationDataMapper
import com.example.smartalarm.feature.timer.framework.notification.mapper.CompletedNotificationTimerDataMapper
import com.example.smartalarm.feature.timer.framework.notification.mapper.MissedTimerNotificationDataMapper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap


@Module
@InstallIn(SingletonComponent::class)
abstract class DataMapperModule {

    @Binds
    @IntoMap
    @TimerNotificationMapDataMapperKey(TimerNotificationDataMapperKey.ACTIVE)
    abstract fun bindActiveTimerMapper(
        mapper: ActiveTimerNotificationDataMapper
    ): AppNotificationDataMapper<*, *>

    @Binds
    @IntoMap
    @TimerNotificationMapDataMapperKey(TimerNotificationDataMapperKey.COMPLETED)
    abstract fun bindCompletedTimerMapper(
        mapper: CompletedNotificationTimerDataMapper
    ): AppNotificationDataMapper<*, *>


    @Binds
    @IntoMap
    @TimerNotificationMapDataMapperKey(TimerNotificationDataMapperKey.MISSED)
    abstract fun bindMissedTimerMapper(
        mapper: MissedTimerNotificationDataMapper
    ): AppNotificationDataMapper<*, *>

}