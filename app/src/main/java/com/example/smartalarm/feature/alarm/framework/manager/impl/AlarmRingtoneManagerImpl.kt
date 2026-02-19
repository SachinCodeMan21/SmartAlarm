package com.example.smartalarm.feature.alarm.framework.manager.impl

import android.content.Context
import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.media.MediaPlayer
import android.util.Log
import androidx.core.net.toUri
import com.example.smartalarm.core.ringtone.BaseRingtonePlayer
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import com.example.smartalarm.feature.setting.manager.LanguageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

/**
 * Concrete implementation of [AlarmRingtoneManager] responsible for handling alarm ringtone playback.
 * Extends [BaseRingtonePlayer] to provide functionality for playing alarm sounds and managing ringtone settings.
 *
 * This class utilizes the [MediaPlayer] for alarm sound playback, providing methods to play a specific alarm sound,
 * retrieve the default ringtone, and fetch the title of the selected ringtone. It also allows the user to select a custom
 * alarm ringtone through the system's ringtone picker activity.
 *
 * @param context The [Context] used to access system resources and perform media operations.
 *                It is injected using Dagger's [ApplicationContext] annotation to ensure it has the
 *                correct application context.
 */
class AlarmRingtoneManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AlarmRingtoneManager{

    private var mediaPlayer: MediaPlayer? = null

    private val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private val lock = Any()


    /**
     * Plays the specified alarm sound.
     *
     * This method takes an [alarmSound] URI string, converts it into a [Uri] object,
     * and then calls the inherited [playAlarmRingtone] method from [BaseRingtonePlayer] to handle playback.
     *
     * @param alarmSound The URI string of the alarm sound to be played. This string is converted
     *                   into a [Uri] before being passed to the [play] method for playback.
     */
    override fun playAlarmRingtone(alarmSound: String, alarmVolume: Int) {
        synchronized(lock) {

            if (mediaPlayer?.isPlaying == true) return // Do nothing if already playing

            try {

                stopAlarmRingtone() // Stop any existing playback

                val volume = alarmVolume / 100f // Convert volume to float between 0 and 1

                mediaPlayer = MediaPlayer().apply {
                    val uri = alarmSound.toUri()
                    setDataSource(context, uri)
                    setAudioAttributes(audioAttributes)
                    isLooping = true
                    prepare()
                    start()

                    setVolume(volume, volume) // Set custom volume for alarm
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun stopAlarmRingtone() {
        synchronized(lock) {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
            mediaPlayer = null
        }
    }

    /**
     * Retrieves the default alarm ringtone URI.
     *
     * This method uses [RingtoneManager] to fetch the default alarm ringtone URI set on the system.
     * The URI is returned for use in cases where no custom ringtone has been selected.
     *
     * @return The [Uri] of the default alarm ringtone.
     */
    override fun getDefaultRingtoneUri(): Uri {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    }

    /**
     * Gets the title of the ringtone corresponding to the provided [ringtoneUri].
     *
     * This method checks if the provided [ringtoneUri] is non-null and retrieves the title of the ringtone
     * associated with that URI. If the URI is null, the default alarm ringtone title is returned.
     *
     * @param ringtoneUri The [Uri] of the ringtone whose title is to be fetched. If null, the default ringtone title
     *            will be returned.
     * @return The title of the ringtone associated with the provided [ringtoneUri].
     *         If the [uri] is null, it falls back to the default alarm ringtone title.
     */
    override fun getRingtoneTitle(ringtoneUri: String?): String {

        val ringtone = if (ringtoneUri != null)
            RingtoneManager.getRingtone(context, ringtoneUri.toUri())
        else
            null
        return ringtone?.getTitle(context) ?: RingtoneManager.getRingtone(context, getDefaultRingtoneUri()).getTitle(context)
    }

}
