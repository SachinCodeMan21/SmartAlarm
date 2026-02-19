package com.example.smartalarm.feature.alarm.framework.di.modules

import com.example.smartalarm.feature.alarm.data.datasource.contract.AlarmLocalDataSource
import com.example.smartalarm.feature.alarm.data.datasource.impl.AlarmLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that binds the implementation of [AlarmLocalDataSource].
 *
 * This module is installed in the [SingletonComponent], ensuring the binding
 * lives as long as the application.
 *
 * Uses [@Binds] to associate the [AlarmLocalDataSourceImpl] concrete class
 * with its [AlarmLocalDataSource] interface.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmDataSourceModule {

    /**
     * Binds [AlarmLocalDataSourceImpl] as the singleton implementation for [AlarmLocalDataSource].
     *
     * @param impl The concrete implementation of [AlarmLocalDataSource].
     * @return The bound [AlarmLocalDataSource] interface.
     */
    @Binds
    @Singleton
    abstract fun bindLocalAlarmDataSource(impl: AlarmLocalDataSourceImpl): AlarmLocalDataSource
}
