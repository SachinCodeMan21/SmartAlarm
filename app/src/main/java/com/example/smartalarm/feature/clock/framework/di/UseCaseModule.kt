package com.example.smartalarm.feature.clock.framework.di

import com.example.smartalarm.feature.clock.domain.usecase.contract.ClockUseCases
import com.example.smartalarm.feature.clock.domain.usecase.contract.PlaceSearchUseCases
import com.example.smartalarm.feature.clock.domain.usecase.contract.UpdateClockUseCase
import com.example.smartalarm.feature.clock.domain.usecase.impl.ClockUseCasesImpl
import com.example.smartalarm.feature.clock.domain.usecase.impl.PlaceSearchUseCasesImpl
import com.example.smartalarm.feature.clock.domain.usecase.impl.UpdateClockUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding use case implementations to their interfaces.
 *
 * This module ensures that the presentation and domain layers
 * depend on abstractions ([ClockUseCases], [PlaceSearchUseCases])
 * rather than concrete implementations, promoting testability
 * and adherence to clean architecture principles.
 *
 * Scope:
 * - All bindings are singletons to provide a single consistent instance
 *   of each use case throughout the application lifecycle.
 *
 * Responsibilities:
 * 1. Bind [ClockUseCasesImpl] to [ClockUseCases].
 * 2. Bind [PlaceSearchUseCasesImpl] to [PlaceSearchUseCases].
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    /**
     * Binds the implementation of [ClockUseCases].
     *
     * @param imp The concrete [ClockUseCasesImpl] provided by Hilt.
     * @return The [ClockUseCases] interface for injection.
     */
    @Binds
    @Singleton
    abstract fun bindClockUseCaseModule(
        imp: ClockUseCasesImpl
    ): ClockUseCases

    /**
     * Binds the implementation of [PlaceSearchUseCases].
     *
     * @param imp The concrete [PlaceSearchUseCasesImpl] provided by Hilt.
     * @return The [PlaceSearchUseCases] interface for injection.
     */
    @Binds
    @Singleton
    abstract fun bindPlaceUseCaseModule(
        imp: PlaceSearchUseCasesImpl
    ): PlaceSearchUseCases

    /**
     * Binds the implementation of [UpdateClockUseCase].
     *
     * @param impl The concrete [UpdateClockUseCaseImpl] provided by Hilt.
     * @return The [UpdateClockUseCase] interface for injection.
     */
    @Binds
    @Singleton
    abstract fun bindUpdateClockUseCase(
        impl: UpdateClockUseCaseImpl
    ): UpdateClockUseCase
}