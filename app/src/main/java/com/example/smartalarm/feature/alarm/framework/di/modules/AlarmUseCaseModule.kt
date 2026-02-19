package com.example.smartalarm.feature.alarm.framework.di.modules

import com.example.smartalarm.feature.alarm.domain.usecase.contract.DeleteAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.DismissAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.GetAlarmByIdUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.GetAllAlarmsUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SnoozeAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.StopAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.MissedAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.PostSaveOrUpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.RingAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SaveAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SwipedAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.ToggleAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UndoAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.impl.DeleteAlarmUseCaseImpl
import com.example.smartalarm.feature.alarm.domain.usecase.impl.DismissAlarmUseCaseImpl
import com.example.smartalarm.feature.alarm.domain.usecase.impl.GetAlarmByIdUseCaseImpl
import com.example.smartalarm.feature.alarm.domain.usecase.impl.GetAllAlarmsUseCaseImpl
import com.example.smartalarm.feature.alarm.domain.usecase.impl.SnoozeAlarmUseCaseImpl
import com.example.smartalarm.feature.alarm.domain.usecase.impl.StopAlarmUseCaseImpl
import com.example.smartalarm.feature.alarm.domain.usecase.impl.MissedAlarmUseCaseImpl
import com.example.smartalarm.feature.alarm.domain.usecase.impl.PostSaveOrUpdateAlarmUseCaseImpl
import com.example.smartalarm.feature.alarm.domain.usecase.impl.RingAlarmUseCaseImpl
import com.example.smartalarm.feature.alarm.domain.usecase.impl.SaveAlarmUseCaseImpl
import com.example.smartalarm.feature.alarm.domain.usecase.impl.SwipedAlarmUseCaseImpl
import com.example.smartalarm.feature.alarm.domain.usecase.impl.ToggleAlarmUseCaseImpl
import com.example.smartalarm.feature.alarm.domain.usecase.impl.UndoAlarmUseCaseImpl
import com.example.smartalarm.feature.alarm.domain.usecase.impl.UpdateAlarmUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that binds use case implementations related to alarm management.
 *
 * This module is installed in the [SingletonComponent], ensuring all provided use cases
 * are singleton-scoped and live throughout the application's lifecycle.
 *
 * Each binding associates a concrete implementation with its corresponding use case interface.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmUseCaseModule {

    /**
     * Binds [GetAllAlarmsUseCaseImpl] as the singleton implementation for [GetAllAlarmsUseCase].
     *
     * @param impl The concrete implementation of [GetAllAlarmsUseCase].
     * @return The bound [GetAllAlarmsUseCase] interface.
     */
    @Binds
    @Singleton
    abstract fun bindGetAllAlarmsUseCase(impl: GetAllAlarmsUseCaseImpl): GetAllAlarmsUseCase

    /**
     * Binds [GetAlarmByIdUseCaseImpl] as the singleton implementation for [GetAlarmByIdUseCase].
     *
     * @param impl The concrete implementation of [GetAlarmByIdUseCase].
     * @return The bound [GetAlarmByIdUseCase] interface.
     */
    @Binds
    @Singleton
    abstract fun bindGetAlarmByIdUseCase(impl: GetAlarmByIdUseCaseImpl): GetAlarmByIdUseCase

    /**
     * Binds [SaveAlarmUseCaseImpl] as the singleton implementation for [SaveAlarmUseCase].
     *
     * @param impl The concrete implementation of [SaveAlarmUseCase].
     * @return The bound [SaveAlarmUseCase] interface.
     */
    @Binds
    @Singleton
    abstract fun bindSaveAlarmUseCase(impl: SaveAlarmUseCaseImpl): SaveAlarmUseCase

    /**
     * Binds [UpdateAlarmUseCaseImpl] as the singleton implementation for [UpdateAlarmUseCase].
     *
     * @param impl The concrete implementation of [UpdateAlarmUseCase].
     * @return The bound [UpdateAlarmUseCase] interface.
     */
    @Binds
    @Singleton
    abstract fun bindUpdateAlarmUseCase(impl: UpdateAlarmUseCaseImpl): UpdateAlarmUseCase

    /**
     * Binds [DeleteAlarmUseCaseImpl] as the singleton implementation for [DeleteAlarmUseCase].
     *
     * @param impl The concrete implementation of [DeleteAlarmUseCase].
     * @return The bound [DeleteAlarmUseCase] interface.
     */
    @Binds
    @Singleton
    abstract fun deleteAlarmByIdUseCase(impl: DeleteAlarmUseCaseImpl): DeleteAlarmUseCase


    @Binds
    @Singleton
    abstract fun bindRingAlarmUseCaseAlarm(impl: RingAlarmUseCaseImpl): RingAlarmUseCase

    @Binds
    @Singleton
    abstract fun bindMissedAlarmUseCase(impl: MissedAlarmUseCaseImpl): MissedAlarmUseCase

    @Binds
    @Singleton
    abstract fun bindSnoozedAlarmUseCase(impl: SnoozeAlarmUseCaseImpl): SnoozeAlarmUseCase

    @Binds
    @Singleton
    abstract fun bindStopAlarmUseCase(impl: StopAlarmUseCaseImpl): StopAlarmUseCase

    @Binds
    @Singleton
    abstract fun bindDismissAlarmUseCase(impl: DismissAlarmUseCaseImpl): DismissAlarmUseCase

    @Binds
    @Singleton
    abstract fun bindToggleAlarmUseCase(impl: ToggleAlarmUseCaseImpl): ToggleAlarmUseCase

    @Binds
    @Singleton
    abstract fun bindUndoAlarmUseCase(impl: UndoAlarmUseCaseImpl): UndoAlarmUseCase

    @Binds
    @Singleton
    abstract fun bindSwipedAlarmUseCase(impl: SwipedAlarmUseCaseImpl): SwipedAlarmUseCase

    @Binds
    @Singleton
    abstract fun bindPostSaveOrUpdateAlarmUseCase(impl: PostSaveOrUpdateAlarmUseCaseImpl): PostSaveOrUpdateAlarmUseCase


}
