package com.example.smartalarm.feature.alarm.framework.di.modules

import com.example.smartalarm.core.data.database.MyDatabase
import com.example.smartalarm.feature.alarm.data.local.dao.AlarmDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides dependencies related to the Alarm DAO.
 *
 * This module is installed in the [SingletonComponent], ensuring the provided
 * dependencies live as long as the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object AlarmDaoModule {

    /**
     * Provides a singleton instance of [AlarmDao] from the given [MyDatabase].
     *
     * @param myDatabase The database instance to retrieve the DAO from.
     * @return A singleton [AlarmDao] instance for accessing alarm-related data operations.
     */
    @Provides
    @Singleton
    fun provideAlarmDao(myDatabase: MyDatabase): AlarmDao = myDatabase.alarmsDao()
}
