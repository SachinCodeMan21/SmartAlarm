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
 * Hilt module providing dependencies for the Stopwatch feature’s local data source.
 *
 * This module binds the [StopwatchLocalDataSource] interface to its concrete
 * implementation [StopwatchLocalDataSourceImpl] and ensures a single, app-wide
 * instance is used wherever it is injected.
 *
 *  ### Scope Rationale
 *  The datasource is scoped to the **SingletonComponent** (app-wide) because:
 *  1. It is used by the **app’s sync manager** on startup to synchronize the
 *     latest stopwatch state with the repository.
 *  2. Background services or notifications can query it to display a **running
 *     stopwatch** even when the app is in background.
 *  3. Fragments observing the stopwatch state can safely subscribe to a
 *     centralized source of truth without needing multiple instances.
 *
 * ### Injection
 * Use Hilt to inject [StopwatchLocalDataSource] wherever needed; Hilt will
 * provide the singleton [StopwatchLocalDataSourceImpl] automatically.
 */
@Module
@InstallIn(SingletonComponent::class)
object StopwatchLocalDataSourceModule {

    /**
     * Provides a singleton instance of [StopwatchLocalDataSourceImpl].
     *
     * @param dao The [StopwatchDao] instance, automatically provided by Hilt.
     * @return A singleton [StopwatchLocalDataSource] instance to ensure consistent
     *         state management across the app.
     */
    @Provides
    @Singleton
    fun provideStopwatchLocalDataSource(dao: StopwatchDao): StopwatchLocalDataSource {
        return StopwatchLocalDataSourceImpl(dao)
    }
}