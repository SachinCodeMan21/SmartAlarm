package com.example.smartalarm.core.di.modules

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationManagerCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

/**
 * Hilt module that provides core application-level dependencies.
 *
 * All dependencies are scoped to the SingletonComponent, meaning they will live
 * as long as the application does.
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    /**
     * Provides a singleton instance of [AlarmManager] from the application context.
     *
     * Used to schedule alarms and notifications.
     *
     * @param context The application context provided by Hilt.
     * @return An instance of [AlarmManager].
     */
    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    /**
     * Provides a singleton instance of [NotificationManager] from the application context.
     *
     * Used to post, cancel and manage notifications.
     *
     * @param context The application context provided by Hilt.
     * @return An instance of [NotificationManager].
     */
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * Provides a singleton instance of [NotificationManagerCompat] for managing notifications.
     *
     * @param context The application context provided by Hilt.
     * @return An instance of [NotificationManagerCompat].
     */
    @Provides
    @Singleton
    fun provideNotificationManagerCompat(@ApplicationContext context: Context): NotificationManagerCompat {
        return NotificationManagerCompat.from(context)
    }


    /**
     * Provides a singleton instance of [SharedPreferences] with the name "MyPrefs".
     *
     * Used for lightweight key-value storage.
     *
     * @param context The application context provided by Hilt.
     * @return An instance of [SharedPreferences].
     */
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    }


    @Provides
    fun provideServiceScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

}
