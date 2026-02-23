@file:Suppress("unused")

package com.example.smartalarm.feature.stopwatch.framework.di.modules

import com.example.smartalarm.feature.stopwatch.data.datasource.contract.StopwatchLocalDataSource
import com.example.smartalarm.feature.stopwatch.data.datasource.impl.StopwatchLocalDataSourceImpl
import com.example.smartalarm.feature.stopwatch.data.local.dao.StopwatchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to provide dependencies related to the stopwatch local data source.
 * This module binds the [StopwatchLocalDataSource] interface to its implementation [StopwatchLocalDataSourceImpl].
 */
@Module
@InstallIn(SingletonComponent::class)  // Install the module in the SingletonComponent
object StopwatchLocalDataSourceModule {

    /**
     * Provides a singleton instance of [StopwatchLocalDataSourceImpl].
     * This method will be used by Hilt to inject dependencies wherever [StopwatchLocalDataSource] is required.
     *
     * @param dao The [StopwatchDao] instance, automatically provided by Hilt.
     * @return A singleton instance of [StopwatchLocalDataSource].
     */
    @Provides
    @Singleton
    fun provideStopwatchLocalDataSource(dao: StopwatchDao): StopwatchLocalDataSource {
        return StopwatchLocalDataSourceImpl(dao)
    }
}
