package com.example.smartalarm.feature.alarm.framework.di.modules

import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.feature.alarm.framework.scheduler.impl.AlarmSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for binding the [AlarmScheduler] interface to its implementation.
 *
 * This module is installed in the [SingletonComponent], ensuring a singleton scope for the binding.
 *
 * Binds [AlarmSchedulerImpl] as the singleton implementation of [AlarmScheduler].
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmSchedularModule {

    /**
     * Binds [AlarmSchedulerImpl] as the singleton implementation for [AlarmScheduler].
     *
     * @param impl The concrete implementation of [AlarmScheduler].
     * @return The bound [AlarmScheduler] interface.
     */
    @Binds
    @Singleton
    abstract fun bindAlarmSchedular(impl: AlarmSchedulerImpl): AlarmScheduler
}