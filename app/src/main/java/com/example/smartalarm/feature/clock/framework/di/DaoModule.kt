package com.example.smartalarm.feature.clock.framework.di

import com.example.smartalarm.core.data.database.MyDatabase
import com.example.smartalarm.feature.clock.data.local.dao.ClockDao
import com.example.smartalarm.feature.clock.data.local.dao.PlaceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing Room DAO instances.
 *
 * This module defines how to inject `ClockDao` and `PlaceDao`
 * from the `MyDatabase` instance into the dependency graph.
 *
 * Scope:
 * - All DAOs are provided as singletons to ensure a single
 *   consistent instance is used across the app.
 *
 * Usage:
 * - Inject DAOs wherever needed in repositories or use cases:
 *   ```kotlin
 *   @Inject lateinit var clockDao: ClockDao
 *   ```
 */
@Module
@InstallIn(SingletonComponent::class)
class DaoModule {

    /**
     * Provides the [ClockDao] instance from the Room database.
     *
     * @param myDatabase The Room database instance.
     * @return Singleton instance of [ClockDao].
     */
    @Provides
    @Singleton
    fun provideClockDao(myDatabase: MyDatabase): ClockDao = myDatabase.clockDao()

    /**
     * Provides the [PlaceDao] instance from the Room database.
     *
     * @param myDatabase The Room database instance.
     * @return Singleton instance of [PlaceDao].
     */
    @Provides
    @Singleton
    fun providePlaceDao(myDatabase: MyDatabase): PlaceDao = myDatabase.placeDao()

}