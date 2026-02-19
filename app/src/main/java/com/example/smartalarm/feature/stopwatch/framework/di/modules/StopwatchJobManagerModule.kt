@file:Suppress("unused")

package com.example.smartalarm.feature.stopwatch.framework.di.modules

import com.example.smartalarm.feature.stopwatch.framework.jobmanager.contract.StopwatchTickerJobManager
import com.example.smartalarm.feature.stopwatch.framework.jobmanager.contract.BlinkEffectJobManager
import com.example.smartalarm.feature.stopwatch.framework.jobmanager.impl.StopwatchTimerJobManagerImpl
import com.example.smartalarm.feature.stopwatch.framework.jobmanager.impl.BlinkEffectJobManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Hilt module to provide dependencies for stopwatch job managers.
 *
 * This module binds the interfaces [StopwatchTickerJobManager] and [BlinkEffectJobManager]
 * to their respective implementations [StopwatchTimerJobManagerImpl] and [BlinkEffectJobManagerImpl].
 */
@Module
@InstallIn(SingletonComponent::class)  // Install the module in the SingletonComponent (app-wide scope)
abstract class StopwatchJobManagerModule {

    /**
     * Binds [StopwatchTimerJobManagerImpl] to [StopwatchTickerJobManager].
     */
    @Binds
    abstract fun bindStopwatchTickerJobManager(
        stopwatchTimerJobManagerImpl: StopwatchTimerJobManagerImpl
    ): StopwatchTickerJobManager

    /**
     * Binds [BlinkEffectJobManagerImpl] to [BlinkEffectJobManager].
     *
     * @return An instance of [BlinkEffectJobManager].
     */
    @Binds
    @Singleton
    abstract fun bindBlinkEffectJobManager(
        blinkEffectJobManagerImpl: BlinkEffectJobManagerImpl
    ): BlinkEffectJobManager

}
