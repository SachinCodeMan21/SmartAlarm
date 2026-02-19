package com.example.smartalarm.feature.timer.framework.di.modules

import com.example.smartalarm.feature.timer.data.repository.TimerRepositoryImpl
import com.example.smartalarm.feature.timer.domain.repository.TimerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimerRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTimerRepository(timerRepositoryImpl: TimerRepositoryImpl) : TimerRepository

}