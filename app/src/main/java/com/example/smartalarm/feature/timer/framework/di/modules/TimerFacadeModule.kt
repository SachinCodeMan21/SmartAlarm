package com.example.smartalarm.feature.timer.framework.di.modules

import com.example.smartalarm.feature.timer.domain.facade.contract.TimerUseCasesFacade
import com.example.smartalarm.feature.timer.domain.facade.impl.TimerUseCasesFacadeImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimerFacadeModule {

    @Binds
    @Singleton
    abstract fun bindTimerUseCasesFacade(impl: TimerUseCasesFacadeImpl) : TimerUseCasesFacade
}