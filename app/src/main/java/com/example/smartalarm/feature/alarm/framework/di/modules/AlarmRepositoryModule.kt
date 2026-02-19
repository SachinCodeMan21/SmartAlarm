package com.example.smartalarm.feature.alarm.framework.di.modules

import com.example.smartalarm.feature.alarm.data.repository.AlarmRepositoryImpl
import com.example.smartalarm.feature.alarm.domain.repository.AlarmRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for binding the [AlarmRepository] interface to its implementation.
 *
 * This module is installed in the [SingletonComponent], ensuring that the provided
 * [AlarmRepository] instance is a singleton within the application scope.
 *
 * Binds [AlarmRepositoryImpl] as the singleton implementation of [AlarmRepository].
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmRepositoryModule {

    /**
     * Binds [AlarmRepositoryImpl] as the singleton implementation for [AlarmRepository].
     *
     * @param impl The concrete implementation of [AlarmRepository].
     * @return The bound [AlarmRepository] interface.
     */
    @Binds
    @Singleton
    abstract fun bindAlarmRepository(impl: AlarmRepositoryImpl): AlarmRepository
}
