package com.example.smartalarm.feature.alarm.framework.manager.impl

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.media.MediaPlayer
import androidx.core.net.toUri
import com.example.smartalarm.core.ringtone.BaseRingtonePlayer
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.roundToInt

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
//class AlarmRingtoneManagerImpl @Inject constructor(
//    @param:ApplicationContext private val context: Context
//) : AlarmRingtoneManager{
//
//    private var mediaPlayer: MediaPlayer? = null
//
//    private val audioAttributes: AudioAttributes = AudioAttributes.Builder()
//        .setUsage(AudioAttributes.USAGE_ALARM)
//        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//        .build()
//
//    private val lock = Any()
//
//
//    /**
//     * Plays the specified alarm sound.
//     *
//     * This method takes an [alarmSound] URI string, converts it into a [Uri] object,
//     * and then calls the inherited [playAlarmRingtone] method from [BaseRingtonePlayer] to handle playback.
//     *
//     * @param alarmSound The URI string of the alarm sound to be played. This string is converted
//     *                   into a [Uri] before being passed to the [play] method for playback.
//     */
//    override fun playAlarmRingtone(alarmSound: String, alarmVolume: Int) {
//
//        synchronized(lock) {
//
//            if (mediaPlayer?.isPlaying == true) return // Do nothing if already playing
//
//            try {
//
//                stopAlarmRingtone() // Stop any existing playback
//
//                val volume = alarmVolume / 100f // Convert volume to float between 0 and 1
//
//                mediaPlayer = MediaPlayer().apply {
//                    val uri = alarmSound.toUri()
//                    setDataSource(context, uri)
//                    setAudioAttributes(audioAttributes)
//                    isLooping = true
//                    prepare()
//                    start()
//
//                    setVolume(volume, volume) // Set custom volume for alarm
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//
//    }
//
//
//    override fun stopAlarmRingtone() {
//        synchronized(lock) {
//            mediaPlayer?.let {
//                if (it.isPlaying) it.stop()
//                it.release()
//            }
//            mediaPlayer = null
//        }
//    }
//
//    /**
//     * Retrieves the default alarm ringtone URI.
//     *
//     * This method uses [RingtoneManager] to fetch the default alarm ringtone URI set on the system.
//     * The URI is returned for use in cases where no custom ringtone has been selected.
//     *
//     * @return The [Uri] of the default alarm ringtone.
//     */
//    override fun getDefaultRingtoneUri(): Uri {
//        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
//    }
//
//    /**
//     * Gets the title of the ringtone corresponding to the provided [ringtoneUri].
//     *
//     * This method checks if the provided [ringtoneUri] is non-null and retrieves the title of the ringtone
//     * associated with that URI. If the URI is null, the default alarm ringtone title is returned.
//     *
//     * @param ringtoneUri The [Uri] of the ringtone whose title is to be fetched. If null, the default ringtone title
//     *            will be returned.
//     * @return The title of the ringtone associated with the provided [ringtoneUri].
//     *         If the [uri] is null, it falls back to the default alarm ringtone title.
//     */
//    override fun getRingtoneTitle(ringtoneUri: String?): String {
//
//        val ringtone = if (ringtoneUri != null)
//            RingtoneManager.getRingtone(context, ringtoneUri.toUri())
//        else
//            null
//        return ringtone?.getTitle(context) ?: RingtoneManager.getRingtone(context, getDefaultRingtoneUri()).getTitle(context)
//    }
//
//}

/**
 * Concrete implementation of [AlarmRingtoneManager] responsible for handling alarm ringtone playback.
 * Extends [BaseRingtonePlayer] to provide functionality for playing alarm sounds and managing ringtone settings.
 *
 * This class utilizes the [MediaPlayer] for alarm sound playback. To ensure the alarm plays at the user-specified
 * volume even if the system alarm volume is 0 or low, it temporarily adjusts the system STREAM_ALARM volume during
 * playback and restores it afterward.
 *
 * @param context The [Context] used to access system resources and perform media operations.
 *                It is injected using Dagger's [ApplicationContext] annotation.
 */
class AlarmRingtoneManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AlarmRingtoneManager {

    private var mediaPlayer: MediaPlayer? = null
    private var originalAlarmVolume: Int = -1  // -1 means not set / no restore needed

    private val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)  // Better for alerts/alarms than MUSIC
        .build()

    private val audioManager: AudioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private val lock = Any()

    /**
     * Plays the specified alarm sound at the given volume level (0-100).
     *
     * Temporarily sets the system alarm volume to match the desired level (scaled to device's max),
     * plays the sound using USAGE_ALARM (so it respects DND/alarms-allowed), then restores original volume
     * when stopped or released.
     *
     * @param alarmSound The URI string of the alarm sound.
     * @param alarmVolume Desired volume percentage (0–100). 0 = silent, 100 = max possible.
     */
    override fun playAlarmRingtone(alarmSound: String, alarmVolume: Int) {
        synchronized(lock) {
            if (mediaPlayer?.isPlaying == true) return  // Already playing → skip

            try {
                stopAlarmRingtone()  // Clean up any previous player

                // Remember current system alarm volume for later restore
                originalAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)

                val maxAlarmVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                // Scale 0-100 → 0-maxAlarmVolume (most devices: 7 steps)
                val targetSystemVolume = (alarmVolume * maxAlarmVolume / 100f).roundToInt()
                    .coerceIn(0, maxAlarmVolume)

                // Temporarily set system alarm volume (no flags → no UI toast)
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, targetSystemVolume, 0)

                val uri = alarmSound.toUri()  // Assuming extension function or Uri.parse(alarmSound)

                mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, uri)
                    setAudioAttributes(audioAttributes)
                    isLooping = true  // Typical for alarms until dismissed
                    setVolume(1f, 1f)  // Full volume multiplier — now system vol controls loudness
                    prepareAsync()

                    setOnPreparedListener {
                        it.start()
                    }

                    setOnErrorListener { mp, what, extra ->
                        mp.release()
                        mediaPlayer = null
                        restoreAlarmVolume()
                        true
                    }

                    setOnCompletionListener {
                        restoreAlarmVolume()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                restoreAlarmVolume()
            }
        }
    }

    /**
     * Stops the current alarm playback and releases resources.
     * Also restores the original system alarm volume.
     */
    override fun stopAlarmRingtone() {
        synchronized(lock) {
            mediaPlayer?.let { player ->
                try {
                    if (player.isPlaying) player.stop()
                } catch (_: Exception) {
                }
                player.release()
            }
            mediaPlayer = null
            restoreAlarmVolume()
        }
    }

    private fun restoreAlarmVolume() {
        if (originalAlarmVolume != -1) {
            try {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalAlarmVolume, 0)
            } catch (_: Exception) {
            }
            originalAlarmVolume = -1
        }
    }

    override fun getDefaultRingtoneUri(): Uri {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    }

    override fun getRingtoneTitle(ringtoneUri: String?): String {
        val uri = ringtoneUri?.toUri() ?: getDefaultRingtoneUri()
        return try {
            RingtoneManager.getRingtone(context, uri)?.getTitle(context)
                ?: "Default alarm"
        } catch (e: Exception) {
            "Unknown ringtone"
        }
    }
}