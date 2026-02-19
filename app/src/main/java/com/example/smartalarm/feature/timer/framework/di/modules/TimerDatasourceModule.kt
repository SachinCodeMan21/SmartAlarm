package com.example.smartalarm.feature.timer.framework.di.modules

import com.example.smartalarm.feature.timer.data.datasource.contract.TimerLocalDataSource
import com.example.smartalarm.feature.timer.data.datasource.impl.TimerLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimerDatasourceModule {

    @Binds
    @Singleton
    abstract fun bindTimerDataSource(impl: TimerLocalDataSourceImpl) : TimerLocalDataSource
}