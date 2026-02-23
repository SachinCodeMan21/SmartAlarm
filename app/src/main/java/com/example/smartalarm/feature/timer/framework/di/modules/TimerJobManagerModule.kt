package com.example.smartalarm.feature.timer.framework.di.modules

import com.example.smartalarm.feature.timer.framework.jobmanager.contract.ShowTimerJobManager
import com.example.smartalarm.feature.timer.framework.jobmanager.contract.TimerServiceJobManager
import com.example.smartalarm.feature.timer.framework.jobmanager.impl.ShowTimerJobManagerImpl
import com.example.smartalarm.feature.timer.framework.jobmanager.impl.TimerServiceJobManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimerJobManagerModule {

    @Binds
    @Singleton
    abstract fun bindTimerJobManager(impl: ShowTimerJobManagerImpl) : ShowTimerJobManager

    @Binds
    @Singleton
    abstract fun bindTimerServiceJobManager(impl: TimerServiceJobManagerImpl) : TimerServiceJobManager

}