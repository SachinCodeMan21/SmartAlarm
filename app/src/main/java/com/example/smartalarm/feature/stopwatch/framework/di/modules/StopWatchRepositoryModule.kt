@file:Suppress("unused")

package com.example.smartalarm.feature.stopwatch.framework.di.modules


import com.example.smartalarm.core.startup.AppStartupTask
import com.example.smartalarm.feature.stopwatch.data.repository.StopWatchRepositoryImpl
import com.example.smartalarm.feature.stopwatch.data.startup.StopwatchStartup
import com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

/**
 * Hilt module to provide dependencies related to the stopwatch repository.
 * This module binds the [StopWatchRepository] interface to its implementation [StopWatchRepositoryImpl].
 */
@Module
@InstallIn(SingletonComponent::class)  // Install the module in the SingletonComponent for app-wide scope
abstract class StopWatchRepositoryModule {

    /**
     * Binds [StopWatchRepositoryImpl] to [StopWatchRepository].
     *
     * @param stopWatchRepositoryImpl The implementation of [StopWatchRepository].
     * @return An instance of [StopWatchRepository].
     */
    @Binds
    @Singleton
    abstract fun bindStopWatchRepository(
        stopWatchRepositoryImpl: StopWatchRepositoryImpl
    ): StopWatchRepository

}
