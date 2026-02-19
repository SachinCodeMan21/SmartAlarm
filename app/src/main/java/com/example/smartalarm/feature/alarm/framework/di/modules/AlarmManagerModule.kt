package com.example.smartalarm.feature.alarm.framework.di.modules

import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import com.example.smartalarm.feature.alarm.framework.manager.impl.AlarmRingtoneManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmManagerModule {

    /**
     * Provides an implementation of [AlarmRingtoneManager].
     * This will be injected wherever [AlarmRingtoneManager] is needed.
     */
    @Binds
    @Singleton  // Makes this a singleton for the application
    abstract fun bindAlarmRingtonePlayer(impl: AlarmRingtoneManagerImpl): AlarmRingtoneManager
}