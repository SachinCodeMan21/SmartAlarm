package com.example.smartalarm.core.framework.ringtone

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.smartalarm.core.utility.extension.isSdk33AndAbove

/**
 * Abstract base class responsible for handling ringtone playback using [MediaPlayer].
 *
 * Ensures thread-safe play and stop operations with synchronized blocks.
 * Designed to be extended by concrete ringtone player implementations.
 */
abstract class BaseRingtonePlayer(
    private val context: Context
)
{

    /**
     * The internal [MediaPlayer] instance used for playback.
     * Null when not initialized or after release.
     */
    protected var mediaPlayer: MediaPlayer? = null

    /**
     * Lock object for synchronizing media player operations.
     */
    private val lock = Any()

    /**
     * Audio attributes configured for alarm usage and music content type.
     */
    private val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    /**
     * Plays the ringtone from the given [uri]. If already playing, it does nothing.
     *
     * The ringtone is played in a loop using [MediaPlayer]. If another playback is in progress,
     * it is first stopped and the player is reinitialized.
     *
     * @param context The context used to access the media.
     * @param uri The URI of the ringtone to be played.
     */
    open fun play(uri: Uri): Unit = synchronized(lock) {


        // If already playing, just return — don't restart
        if (mediaPlayer?.isPlaying == true)  return


        try {

            stop() // Stop any existing playback if any

            // Create attributed context for AppOps compliance (Android 12+)
            val playbackContext = if (context.isSdk33AndAbove) { context.createAttributionContext("audioPlayback") } else { context }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(playbackContext, uri)
                setAudioAttributes(audioAttributes)
                isLooping = true
                prepare()
                start()
            }

            Log.d("TAG","Alarm Ringtone is Playing")

        }
        catch (e: Exception) {}

    }


    /**
     * Stops the current ringtone playback and releases the [MediaPlayer] resources.
     *
     * If the media player is playing, it is stopped and released.
     */
    open fun stop() = synchronized(lock) {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

}
