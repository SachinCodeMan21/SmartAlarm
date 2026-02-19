package com.example.smartalarm.feature.alarm.framework.di.modules

import com.example.smartalarm.feature.alarm.presentation.view.statemanager.contract.AlarmEditorHomeStateManager
import com.example.smartalarm.feature.alarm.presentation.view.statemanager.impl.AlarmEditorHomeStateManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for binding the implementation of [AlarmEditorHomeStateManager].
 *
 * This module is installed in the [SingletonComponent], ensuring the bound instance
 * lives throughout the application's lifecycle.
 *
 * Uses [@Binds] to associate the concrete implementation [AlarmEditorHomeStateManagerImpl]
 * with its interface [AlarmEditorHomeStateManager], providing a singleton-scoped binding.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmStateManagerModule {

    /**
     * Binds [AlarmEditorHomeStateManagerImpl] as the singleton implementation for [AlarmEditorHomeStateManager].
     *
     * @param impl The concrete implementation of [AlarmEditorHomeStateManager].
     * @return The bound [AlarmEditorHomeStateManager] interface.
     */
    @Binds
    @Singleton
    abstract fun bindAlarmEditorHomeStateManager(impl: AlarmEditorHomeStateManagerImpl): AlarmEditorHomeStateManager
}
