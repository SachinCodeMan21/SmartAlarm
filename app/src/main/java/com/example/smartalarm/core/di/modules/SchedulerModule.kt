package com.example.smartalarm.core.di.modules

import com.example.smartalarm.core.schedular.AppAlarmScheduler
import com.example.smartalarm.core.schedular.AppAlarmSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SchedulerModule {

    @Binds
    @Singleton
    abstract fun bindAppAlarmScheduler(appAlarmSchedulerImpl: AppAlarmSchedulerImpl): AppAlarmScheduler
}