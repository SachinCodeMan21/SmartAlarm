@file:Suppress("unused")

package com.example.smartalarm.feature.stopwatch.framework.di.modules

import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.DeleteStopwatchUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.GetCurrentStopwatchStateUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.GetStopwatchStateUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.LapStopwatchUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.PauseStopwatchUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.StartStopwatchUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.UpdateStopwatchTickerStateUseCase
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.DeleteStopwatchUseCaseImpl
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.GetCurrentStopwatchStateUseCaseImpl
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.GetStopwatchStateUseCaseImpl
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.LapStopwatchUseCaseImpl
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.PauseStopwatchUseCaseImpl
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.StartStopwatchUseCaseImpl
import com.example.smartalarm.feature.stopwatch.domain.usecase.impl.UpdateStopwatchTickerStateUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Dagger module providing dependencies for the stopwatch feature.
 *
 * This module binds the use case interfaces to their respective implementations, ensuring that
 * all instances are scoped as singletons and shared across the app.
 *
 * @see StartStopwatchUseCaseImpl
 * @see PauseStopwatchUseCaseImpl
 * @see LapStopwatchUseCaseImpl
 * @see UpdateStopwatchTickerStateUseCaseImpl
 * @see GetStopwatchStateUseCaseImpl
 * @see GetCurrentStopwatchStateUseCaseImpl
 * @see DeleteStopwatchUseCaseImpl
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class StopwatchUseCasesModule {

    /**
     * Binds the [StartStopwatchUseCase] implementation.
     *
     * @param impl The [StartStopwatchUseCaseImpl] implementation.
     * @return The bound [StartStopwatchUseCase].
     */
    @Binds
    @Singleton
    abstract fun bindStartStopwatchUseCase(impl: StartStopwatchUseCaseImpl): StartStopwatchUseCase

    /**
     * Binds the [PauseStopwatchUseCase] implementation.
     *
     * @param impl The [PauseStopwatchUseCaseImpl] implementation.
     * @return The bound [PauseStopwatchUseCase].
     */
    @Binds
    @Singleton
    abstract fun bindPauseStopwatchUseCase(impl: PauseStopwatchUseCaseImpl): PauseStopwatchUseCase

    /**
     * Binds the [LapStopwatchUseCase] implementation.
     *
     * @param impl The [LapStopwatchUseCaseImpl] implementation.
     * @return The bound [LapStopwatchUseCase].
     */
    @Binds
    @Singleton
    abstract fun bindLapStopwatchUseCase(impl: LapStopwatchUseCaseImpl): LapStopwatchUseCase

    /**
     * Binds the [UpdateStopwatchTickerStateUseCase] implementation.
     *
     * @param impl The [UpdateStopwatchTickerStateUseCaseImpl] implementation.
     * @return The bound [UpdateStopwatchTickerStateUseCase].
     */
    @Binds
    @Singleton
    abstract fun bindUpdateTickerTimesUseCase(impl: UpdateStopwatchTickerStateUseCaseImpl): UpdateStopwatchTickerStateUseCase

    /**
     * Binds the [GetStopwatchStateUseCase] implementation.
     *
     * @param impl The [GetStopwatchStateUseCaseImpl] implementation.
     * @return The bound [GetStopwatchStateUseCase].
     */
    @Binds
    @Singleton
    abstract fun bindGetStopwatchStateUseCase(impl: GetStopwatchStateUseCaseImpl): GetStopwatchStateUseCase

    /**
     * Binds the [GetCurrentStopwatchStateUseCase] implementation.
     *
     * @param impl The [GetCurrentStopwatchStateUseCaseImpl] implementation.
     * @return The bound [GetCurrentStopwatchStateUseCase].
     */
    @Binds
    @Singleton
    abstract fun bindGetCurrentStopwatchStateUseCase(impl: GetCurrentStopwatchStateUseCaseImpl): GetCurrentStopwatchStateUseCase

    /**
     * Binds the [DeleteStopwatchUseCase] implementation.
     *
     * @param impl The [DeleteStopwatchUseCaseImpl] implementation.
     * @return The bound [DeleteStopwatchUseCase].
     */
    @Binds
    @Singleton
    abstract fun bindDeleteStopwatchUseCase(impl: DeleteStopwatchUseCaseImpl): DeleteStopwatchUseCase
}
