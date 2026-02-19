package com.example.smartalarm.feature.clock.framework.di

import com.example.smartalarm.feature.clock.framework.jobmanager.contract.ClockUpdaterJob
import com.example.smartalarm.feature.clock.framework.jobmanager.impl.ClockUpdaterJobImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class JobModule {

    @Binds
    @Singleton
    abstract fun bindClockJobUpdater(impl: ClockUpdaterJobImpl) : ClockUpdaterJob

}