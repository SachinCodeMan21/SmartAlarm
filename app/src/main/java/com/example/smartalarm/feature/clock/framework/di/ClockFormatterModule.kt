package com.example.smartalarm.feature.clock.framework.di

import com.example.smartalarm.feature.clock.utility.ClockTimeFormatter
import com.example.smartalarm.feature.clock.utility.ClockTimeFormatterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ClockFormatterModule {

    @Binds
    abstract fun bindClockFormatter(
        impl: ClockTimeFormatterImpl
    ): ClockTimeFormatter
}