package com.example.smartalarm.feature.clock.framework.di

import com.example.smartalarm.core.database.MyDatabase
import com.example.smartalarm.feature.clock.data.local.dao.ClockDao
import com.example.smartalarm.feature.clock.data.local.dao.PlaceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent :: class)
class DaoModule {


    @Provides
    @Singleton
    fun provideClockDao(myDatabase: MyDatabase) : ClockDao = myDatabase.clockDao()

    @Provides
    @Singleton
    fun providePlaceDao(myDatabase: MyDatabase): PlaceDao = myDatabase.placeDao()

}