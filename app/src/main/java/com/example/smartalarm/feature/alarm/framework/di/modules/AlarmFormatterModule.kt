package com.example.smartalarm.feature.alarm.framework.di.modules

import com.example.smartalarm.feature.alarm.utility.formatter.AlarmTimeFormatter
import com.example.smartalarm.feature.alarm.utility.formatter.AlarmTimeFormatterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmFormatterModule {

    @Binds
    abstract fun bindAlarmFormatter(
        impl: AlarmTimeFormatterImpl
    ): AlarmTimeFormatter

}