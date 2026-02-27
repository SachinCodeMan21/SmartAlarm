package com.example.smartalarm.feature.clock.framework.di

import com.example.smartalarm.feature.clock.data.datasource.contract.ClockLocalDataSource
import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceLocalDataSource
import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceRemoteDataSource
import com.example.smartalarm.feature.clock.data.datasource.impl.ClockLocalDataSourceImpl
import com.example.smartalarm.feature.clock.data.datasource.impl.PlaceLocalDataSourceImpl
import com.example.smartalarm.feature.clock.data.datasource.impl.PlaceRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding data source implementations to their interfaces.
 *
 * This module establishes dependency injection mappings for both
 * local and remote data sources. By using `@Binds`, Hilt will
 * provide the correct implementation whenever the interface is requested.
 *
 * Scope:
 * - All bindings are singletons to ensure a single instance of each
 *   data source is used throughout the application.
 *
 * Responsibilities:
 * 1. Bind [PlaceRemoteDataSourceImpl] to [PlaceRemoteDataSource].
 * 2. Bind [PlaceLocalDataSourceImpl] to [PlaceLocalDataSource].
 * 3. Bind [ClockLocalDataSourceImpl] to [ClockLocalDataSource].
 *
 * This allows the domain and repository layers to depend on interfaces
 * rather than concrete implementations, promoting testability and
 * clean architecture principles.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    /**
     * Binds the implementation of [PlaceRemoteDataSource].
     *
     * @param impl The concrete [PlaceRemoteDataSourceImpl] provided by Hilt.
     * @return The [PlaceRemoteDataSource] interface for injection.
     */
    @Binds
    @Singleton
    abstract fun bindPlaceRemoteDataSource(
        impl: PlaceRemoteDataSourceImpl
    ): PlaceRemoteDataSource

    /**
     * Binds the implementation of [PlaceLocalDataSource].
     *
     * @param impl The concrete [PlaceLocalDataSourceImpl] provided by Hilt.
     * @return The [PlaceLocalDataSource] interface for injection.
     */
    @Binds
    @Singleton
    abstract fun bindPlaceLocalDataSource(
        impl: PlaceLocalDataSourceImpl
    ): PlaceLocalDataSource

    /**
     * Binds the implementation of [ClockLocalDataSource].
     *
     * @param impl The concrete [ClockLocalDataSourceImpl] provided by Hilt.
     * @return The [ClockLocalDataSource] interface for injection.
     */
    @Binds
    @Singleton
    abstract fun bindClockLocalDataSource(
        impl: ClockLocalDataSourceImpl
    ): ClockLocalDataSource

}