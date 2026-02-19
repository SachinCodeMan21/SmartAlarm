package com.example.smartalarm.feature.clock.framework.di

import com.example.smartalarm.feature.clock.data.repository.ClockRepositoryImpl
import com.example.smartalarm.feature.clock.data.repository.PlaceRepositoryImpl
import com.example.smartalarm.feature.clock.domain.repository.ClockRepository
import com.example.smartalarm.feature.clock.domain.repository.PlaceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindClockRepository(impl: ClockRepositoryImpl) : ClockRepository

    @Binds
    @Singleton
    abstract fun bindPlaceRepository(impl: PlaceRepositoryImpl) : PlaceRepository
}