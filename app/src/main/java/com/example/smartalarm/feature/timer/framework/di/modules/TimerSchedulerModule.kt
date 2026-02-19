package com.example.smartalarm.feature.timer.framework.di.modules

import com.example.smartalarm.feature.timer.framework.scheduler.TimerScheduler
import com.example.smartalarm.feature.timer.framework.scheduler.TimerSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimerSchedulerModule {

    @Binds
    @Singleton
    abstract fun bindTimerScheduler(timerSchedulerImpl: TimerSchedulerImpl): TimerScheduler

}