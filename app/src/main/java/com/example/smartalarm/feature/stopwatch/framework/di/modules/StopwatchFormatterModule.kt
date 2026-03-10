package com.example.smartalarm.feature.stopwatch.framework.di.modules

import com.example.smartalarm.feature.stopwatch.utility.StopwatchTimeFormatter
import com.example.smartalarm.feature.stopwatch.utility.StopwatchTimeFormatterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StopwatchFormatterModule {

    @Binds
    @Singleton
    abstract fun bindStopwatchTimeFormatter(
        impl: StopwatchTimeFormatterImpl
    ): StopwatchTimeFormatter
}