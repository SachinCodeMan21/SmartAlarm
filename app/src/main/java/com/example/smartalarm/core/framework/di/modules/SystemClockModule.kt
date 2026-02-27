package com.example.smartalarm.core.framework.di.modules

import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.core.utility.systemClock.impl.SystemClockHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SystemClockModule {
    @Binds
    @Singleton
    abstract fun bindSystemClockProvider(
        impl: SystemClockHelperImpl
    ): SystemClockHelper
}