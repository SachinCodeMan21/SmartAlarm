package com.example.smartalarm.feature.timer.framework.di.modules

import com.example.smartalarm.feature.timer.utility.formatter.TimerTimeFormatter
import com.example.smartalarm.feature.timer.utility.formatter.TimerTimeFormatterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TimerFormatterModule {

    @Binds
    abstract fun bindTimerFormatter(
        impl: TimerTimeFormatterImpl
    ): TimerTimeFormatter
}