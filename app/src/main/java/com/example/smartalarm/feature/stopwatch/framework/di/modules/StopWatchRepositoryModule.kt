@file:Suppress("unused")

package com.example.smartalarm.feature.stopwatch.framework.di.modules

import com.example.smartalarm.feature.stopwatch.data.repository.StopwatchRepositoryImpl
import com.example.smartalarm.feature.stopwatch.domain.repository.StopwatchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to provide dependencies related to the stopwatch repository.
 * This module binds the [StopwatchRepository] interface to its implementation [StopWatchRepositoryImpl].
 */
@Module
@InstallIn(SingletonComponent::class)  // Install the module in the SingletonComponent for app-wide scope
abstract class StopWatchRepositoryModule {

    /**
     * Binds [StopwatchRepositoryImpl] to [StopwatchRepository].
     *
     * @param stopwatchRepositoryImpl The implementation of [StopwatchRepository].
     * @return An instance of [StopwatchRepository].
     */
    @Binds
    @Singleton
    abstract fun bindStopWatchRepository(
        stopwatchRepositoryImpl: StopwatchRepositoryImpl
    ): StopwatchRepository

}
