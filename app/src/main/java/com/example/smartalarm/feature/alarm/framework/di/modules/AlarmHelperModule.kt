package com.example.smartalarm.feature.alarm.framework.di.modules

import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import com.example.smartalarm.feature.alarm.utility.helper.impl.AlarmTimeHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmHelperModule {

    @Binds
    @Singleton
    abstract fun bindAlarmHelper(impl: AlarmTimeHelperImpl): AlarmTimeHelper

}