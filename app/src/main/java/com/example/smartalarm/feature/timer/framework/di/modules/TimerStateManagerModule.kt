package com.example.smartalarm.feature.timer.framework.di.modules

import com.example.smartalarm.feature.stopwatch.data.manager.StopwatchInMemoryStateManager
import com.example.smartalarm.feature.stopwatch.data.manager.StopwatchInMemoryStateManagerImpl
import com.example.smartalarm.feature.timer.domain.manager.ShowTimerStateManager
import com.example.smartalarm.feature.timer.presentation.view.statemanager.contract.TimerInputStateManager
import com.example.smartalarm.feature.timer.data.manager.ShowTimerStateManagerImpl
import com.example.smartalarm.feature.timer.data.manager.TimerInMemoryStateManager
import com.example.smartalarm.feature.timer.data.manager.TimerInMemoryStateManagerImpl
import com.example.smartalarm.feature.timer.presentation.view.statemanager.impl.TimerInputStateManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimerStateManagerModule {

    @Binds
    @Singleton
    abstract fun bindTimerInputStateManager(impl: TimerInputStateManagerImpl): TimerInputStateManager

    @Binds
    @Singleton
    abstract fun bindShowTimerStateManager(impl: ShowTimerStateManagerImpl) : ShowTimerStateManager

    @Binds
    @Singleton
    abstract fun bindShowTimerInMemoryStateManager(impl: TimerInMemoryStateManagerImpl) : TimerInMemoryStateManager
}