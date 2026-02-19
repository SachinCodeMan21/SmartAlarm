package com.example.smartalarm.feature.clock.framework.di

import com.example.smartalarm.feature.clock.domain.usecase.contract.ClockUseCases
import com.example.smartalarm.feature.clock.domain.usecase.contract.PlaceSearchUseCases
import com.example.smartalarm.feature.clock.domain.usecase.impl.ClockUseCasesImpl
import com.example.smartalarm.feature.clock.domain.usecase.impl.PlaceSearchUseCasesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun bindClockUseCaseModule(imp : ClockUseCasesImpl) : ClockUseCases

    @Binds
    @Singleton
    abstract fun bindPlaceUseCaseModule(imp : PlaceSearchUseCasesImpl) : PlaceSearchUseCases
}