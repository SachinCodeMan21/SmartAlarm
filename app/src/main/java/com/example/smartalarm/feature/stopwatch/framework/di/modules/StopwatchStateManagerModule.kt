package com.example.smartalarm.feature.stopwatch.framework.di.modules

import com.example.smartalarm.feature.stopwatch.data.manager.StopwatchInMemoryStateManager
import com.example.smartalarm.feature.stopwatch.data.manager.StopwatchInMemoryStateManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StopwatchStateManagerModule {
    @Binds
    @Singleton
    abstract fun bindStopwatchInMemoryStateManager(impl: StopwatchInMemoryStateManagerImpl): StopwatchInMemoryStateManager
}
