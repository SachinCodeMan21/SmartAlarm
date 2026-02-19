package com.example.smartalarm.feature.alarm.framework.di.modules

import com.example.smartalarm.feature.alarm.presentation.job.MissionCountDownJobManager
import com.example.smartalarm.feature.alarm.presentation.job.MissionCountDownJobManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmJobManagerModule {

    @Binds
    @Singleton
    abstract fun bindAlarmCountdownJobManager(impl : MissionCountDownJobManagerImpl) : MissionCountDownJobManager
}