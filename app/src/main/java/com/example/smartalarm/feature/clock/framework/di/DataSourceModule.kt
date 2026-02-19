package com.example.smartalarm.feature.clock.framework.di

import com.example.smartalarm.feature.clock.data.datasource.contract.ClockLocalDataSource
import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceLocalDataSource
import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceRemoteDataSource
import com.example.smartalarm.feature.clock.data.datasource.impl.ClockLocalDataSourceImpl
import com.example.smartalarm.feature.clock.data.datasource.impl.PlaceLocalDataSourceImpl
import com.example.smartalarm.feature.clock.data.datasource.impl.PlaceRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    /**
     * Binds the implementation [PlaceRemoteDataSourceImpl] to the [PlaceRemoteDataSource] interface.
     *
     * @param impl The implementation instance provided by Hilt.
     * @return The interface type to be injected.
     */
    @Binds
    @Singleton
    abstract fun bindPlaceRemoteDataSource(
        impl: PlaceRemoteDataSourceImpl
    ): PlaceRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPlaceLocalDataSource(
        impl: PlaceLocalDataSourceImpl
    ): PlaceLocalDataSource

    @Binds
    @Singleton
    abstract fun bindClockLocalDataSource(
        impl: ClockLocalDataSourceImpl
    ): ClockLocalDataSource


}
