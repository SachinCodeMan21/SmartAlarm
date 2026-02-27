package com.example.smartalarm.feature.clock.framework.di

import com.example.smartalarm.feature.clock.data.repository.ClockRepositoryImpl
import com.example.smartalarm.feature.clock.data.repository.PlaceRepositoryImpl
import com.example.smartalarm.feature.clock.domain.repository.ClockRepository
import com.example.smartalarm.feature.clock.domain.repository.PlaceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding repository implementations to their interfaces.
 *
 * This module establishes dependency injection mappings for repositories,
 * ensuring that consumers depend on abstractions rather than concrete classes.
 *
 * Scope:
 * - All bindings are singletons to provide a single consistent instance
 *   of each repository across the application.
 *
 * Responsibilities:
 * 1. Bind [ClockRepositoryImpl] to [ClockRepository].
 * 2. Bind [PlaceRepositoryImpl] to [PlaceRepository].
 *
 * This design supports clean architecture principles by allowing
 * the domain and use case layers to depend on interfaces, enhancing
 * testability and modularity.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds the implementation of [ClockRepository].
     *
     * @param impl The concrete [ClockRepositoryImpl] provided by Hilt.
     * @return The [ClockRepository] interface for injection.
     */
    @Binds
    @Singleton
    abstract fun bindClockRepository(
        impl: ClockRepositoryImpl
    ): ClockRepository

    /**
     * Binds the implementation of [PlaceRepository].
     *
     * @param impl The concrete [PlaceRepositoryImpl] provided by Hilt.
     * @return The [PlaceRepository] interface for injection.
     */
    @Binds
    @Singleton
    abstract fun bindPlaceRepository(
        impl: PlaceRepositoryImpl
    ): PlaceRepository
}