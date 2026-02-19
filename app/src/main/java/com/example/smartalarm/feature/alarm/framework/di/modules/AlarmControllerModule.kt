package com.example.smartalarm.feature.alarm.framework.di.modules

import com.example.smartalarm.feature.alarm.framework.controller.contract.AlarmServiceController
import com.example.smartalarm.feature.alarm.framework.controller.impl.AlarmServiceControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmControllerModule {

    @Binds
    @Singleton
    abstract fun bindAlarmServiceController(
        impl: AlarmServiceControllerImpl
    ): AlarmServiceController
}