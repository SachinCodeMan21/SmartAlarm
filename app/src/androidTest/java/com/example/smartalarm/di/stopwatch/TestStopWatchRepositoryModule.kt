package com.example.smartalarm.di.stopwatch

import com.example.smartalarm.feature.stopwatch.data.repository.StopWatchRepositoryImpl
import com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository
import com.example.smartalarm.feature.stopwatch.framework.di.modules.StopWatchRepositoryModule
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [StopWatchRepositoryModule::class]
)
abstract class TestStopWatchRepositoryModule {
    /**
     * Binds [com.example.smartalarm.feature.stopwatch.data.repository.StopWatchRepositoryImpl] to [com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository].
     *
     * @param stopWatchRepositoryImpl The implementation of [com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository].
     * @return An instance of [com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository].
     */
    @Binds
    @Singleton
    abstract fun bindStopWatchRepository(
        stopWatchRepositoryImpl: StopWatchRepositoryImpl
    ): StopWatchRepository
}