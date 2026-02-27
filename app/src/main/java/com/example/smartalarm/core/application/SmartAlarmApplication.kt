package com.example.smartalarm.core.application

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.smartalarm.core.framework.notification.channel.AppNotificationChannelManager
import com.example.smartalarm.core.application.startup.AppStartupRunner
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SmartAlarmApplication : Application(), Configuration.Provider {

    companion object {
        lateinit var instance: SmartAlarmApplication private set
    }

    @Inject
    lateinit var channelManager: AppNotificationChannelManager

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    @Inject
    lateinit var appStartupRunner: AppStartupRunner



    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG) // Helps debug WorkManager
            .build()


    override fun onCreate() {
        super.onCreate()
        instance = this
        channelManager.createAllNotificationChannels()
        WorkManager.initialize(this, workManagerConfiguration)  // 🛠️ Manually initializing workManager here
    }

}
