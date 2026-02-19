package com.example.smartalarm.feature.timer.framework.di.modules

import com.example.smartalarm.feature.timer.framework.service.handler.TimerNotificationHandler
import com.example.smartalarm.feature.timer.framework.service.handler.TimerNotificationHandlerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimerNotificationModule {


    @Binds
    @Singleton
    abstract fun bindTimerNotificationHandler(impl : TimerNotificationHandlerImpl) : TimerNotificationHandler
}