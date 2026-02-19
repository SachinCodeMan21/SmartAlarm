package com.example.smartalarm.feature.timer.framework.di.modules

import com.example.smartalarm.core.database.MyDatabase
import com.example.smartalarm.core.startup.AppStartupTask
import com.example.smartalarm.feature.stopwatch.data.startup.StopwatchStartup
import com.example.smartalarm.feature.timer.data.local.dao.TimerDao
import com.example.smartalarm.feature.timer.data.startup.ShowTimerStartup
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TimerDaoModule {

    @Provides
    @Singleton
    fun provideTimerDao(myDatabase: MyDatabase) : TimerDao = myDatabase.timerDao()

    @Provides
    @IntoSet
    fun provideSecondTask(task: ShowTimerStartup): AppStartupTask = task
}