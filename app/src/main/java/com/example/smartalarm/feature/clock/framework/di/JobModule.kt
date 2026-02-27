package com.example.smartalarm.feature.clock.framework.di

import com.example.smartalarm.feature.clock.framework.jobmanager.contract.ClockUpdaterJob
import com.example.smartalarm.feature.clock.framework.jobmanager.impl.ClockUpdaterJobImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding job-related implementations to their interfaces.
 *
 * This module provides dependency injection mappings for all clock related background
 * jobs or schedulers, ensuring that consumers depend on abstractions
 * rather than concrete implementations.
 *
 * Scope:
 * - All bindings are singletons to guarantee a single instance of the job
 *   is used throughout the app lifecycle.
 *
 * Responsibilities:
 * 1. Bind [ClockUpdaterJobImpl] to the [ClockUpdaterJob] interface.
 *
 * This promotes testability and adheres to clean architecture principles
 * by allowing consumers to depend on interfaces instead of concrete classes.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class JobModule {

    /**
     * Binds the implementation of [ClockUpdaterJob].
     *
     * @param impl The concrete [ClockUpdaterJobImpl] provided by Hilt.
     * @return The [ClockUpdaterJob] interface for injection.
     */
    @Binds
    @Singleton
    abstract fun bindClockJobUpdater(
        impl: ClockUpdaterJobImpl
    ): ClockUpdaterJob
}