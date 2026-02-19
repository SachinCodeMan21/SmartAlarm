package com.example.smartalarm.core.notification.channel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


/**
 * Manages creation and registration of all app-specific notification channels
 * with the Android system's [NotificationManager].
 *
 * This ensures that all required notification channels are available on devices
 * running Android 8.0 (API level 26) and above.
 *
 * @property context The application context, used to access resources and system services.
 */
class AppNotificationChannelManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    /**
     * Creates and registers all defined notification channels with the system.
     *
     * This should be called during app startup or initialization to ensure that
     * notifications are routed through properly configured channels.
     */
    fun createAllNotificationChannels() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        AppNotificationChannel.entries.forEach { channelEnum ->
            val channel = channelEnum.toNotificationChannel(context)
            notificationManager.createNotificationChannel(channel)
        }
    }


    /**
     * Converts an [AppNotificationChannel] enum entry into a [NotificationChannel] instance.
     *
     * @param context The application context used to fetch localized strings.
     * @return A configured [NotificationChannel] ready for registration.
     */
    private fun AppNotificationChannel.toNotificationChannel(context: Context): NotificationChannel {
        return NotificationChannel(
            channelId,
            context.getString(channelNameResId),
            channelImportance
        ).apply {
            description = context.getString(channelDescResId)
        }
    }

}
