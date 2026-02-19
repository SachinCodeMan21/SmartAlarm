package com.example.smartalarm.feature.timer.framework.di.modules

import com.example.smartalarm.feature.timer.domain.usecase.contract.DeleteTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.GetAllTimersUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.PauseTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.RestartTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.SaveTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.SnoozeTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.StartTimerUseCase
import com.example.smartalarm.feature.timer.domain.usecase.contract.TickTimerUsecase
import com.example.smartalarm.feature.timer.domain.usecase.impl.DeleteTimerUseCaseImpl
import com.example.smartalarm.feature.timer.domain.usecase.impl.GetAllTimersUseCaseImpl
import com.example.smartalarm.feature.timer.domain.usecase.impl.PauseTimerUseCaseImpl
import com.example.smartalarm.feature.timer.domain.usecase.impl.RestartTimerUseCaseImpl
import com.example.smartalarm.feature.timer.domain.usecase.impl.SaveTimerUseCaseImpl
import com.example.smartalarm.feature.timer.domain.usecase.impl.SnoozeTimerUseCaseImpl
import com.example.smartalarm.feature.timer.domain.usecase.impl.StartTimerUseCaseImpl
import com.example.smartalarm.feature.timer.domain.usecase.impl.TickTimerUsecaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimerUseCaseModule {

    @Binds
    @Singleton
    abstract fun bindStartTimerUseCase(
        impl: StartTimerUseCaseImpl
    ): StartTimerUseCase

    @Binds
    @Singleton
    abstract fun bindPauseTimerUseCase(
        impl: PauseTimerUseCaseImpl
    ): PauseTimerUseCase

    @Binds
    @Singleton
    abstract fun bindSnoozeTimerUseCase(
        impl: SnoozeTimerUseCaseImpl
    ): SnoozeTimerUseCase

    @Binds
    @Singleton
    abstract fun bindRestartTimerUseCase(
        impl: RestartTimerUseCaseImpl
    ): RestartTimerUseCase

    @Binds
    @Singleton
    abstract fun bindTickTimerUseCase(
        impl: TickTimerUsecaseImpl
    ): TickTimerUsecase


    @Binds
    @Singleton
    abstract fun bindSaveTimerUseCase(
        impl: SaveTimerUseCaseImpl
    ): SaveTimerUseCase

    @Binds
    @Singleton
    abstract fun bindDeleteTimerUseCase(
        impl: DeleteTimerUseCaseImpl
    ): DeleteTimerUseCase

    @Binds
    @Singleton
    abstract fun bindGetAllTimersUseCase(
        impl: GetAllTimersUseCaseImpl
    ): GetAllTimersUseCase
}
