package com.example.smartalarm.feature.stopwatch.framework.di.modules

import com.example.smartalarm.core.database.MyDatabase
import com.example.smartalarm.core.startup.AppStartupTask
import com.example.smartalarm.feature.stopwatch.data.local.dao.StopwatchDao
import com.example.smartalarm.feature.stopwatch.data.startup.StopwatchStartup
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * Hilt module that provides database-related dependencies for the stopwatch feature.
 *
 * This module is installed in the SingletonComponent, ensuring the provided
 * instances live as long as the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object StopwatchDatabaseModule {

    /**
     * Provides a [StopwatchDao] instance from the given [MyDatabase].
     *
     * @param db The database instance from which to obtain the DAO.
     * @return The [StopwatchDao] used for accessing stopwatch-related data.
     */
    @Provides
    fun provideStopWatchDao(db: MyDatabase): StopwatchDao = db.stopwatchDao()

    @Provides
    @IntoSet
    fun provideFirstTask(task: StopwatchStartup): AppStartupTask = task
}
