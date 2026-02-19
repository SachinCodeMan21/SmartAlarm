package com.example.smartalarm.feature.alarm.framework.manager.contract

import android.net.Uri
import android.media.MediaPlayer

/**
 * Interface defining the contract for managing alarm ringtone playback and selection.
 *
 * This interface provides methods to play and stop alarm sounds, retrieve the system's default alarm
 * ringtone, get the title of a ringtone.
 *
 * Implementations of this interface should provide the underlying logic for managing alarm sound
 * playback and interacting with the system's ringtone settings.
 */
interface AlarmRingtoneManager {

    /**
     * Plays the specified alarm ringtone from the given [alarmSound] URI.
     * If a ringtone is already playing, it does nothing to avoid restarting playback.
     *
     * This method allows for alarm sound playback. It is designed to be called with the URI of the
     * alarm sound that needs to be played. If a sound is already playing, the call is ignored.
     *
     * @param alarmSound A [String] representing the URI of the alarm ringtone to play.
     *                   This should be a valid URI that points to an audio resource.
     */
    fun playAlarmRingtone(alarmSound: String, alarmVolume:Int)

    /**
     * Stops the current ringtone playback and releases any associated resources.
     *
     * If a ringtone is currently playing, it is stopped, and the resources held by the [MediaPlayer]
     * are released. This ensures that there are no lingering resources or sounds playing.
     */
    fun stopAlarmRingtone()

    /**
     * Retrieves the default alarm ringtone URI from the system's [android.media.RingtoneManager].
     *
     * This method allows you to obtain the default alarm sound URI, which is typically pre-set in the
     * system or user settings. The URI returned can be used for alarm notifications or when no custom
     * alarm ringtone is selected by the user.
     *
     * @return A [Uri] pointing to the system's default alarm ringtone.
     */
    fun getDefaultRingtoneUri(): Uri

    /**
     * Retrieves the title of the ringtone associated with the provided [ringtoneUri].
     *
     * This method queries the system's ringtone manager for the title of the ringtone identified by the
     * given URI. If the URI is valid, it returns the name of the ringtone. If the URI is `null` or invalid,
     * it falls back to returning the title of the default alarm ringtone.
     *
     * @param ringtoneUri The [Uri] pointing to the ringtone whose title is to be retrieved.
     *            If `null`, the title of the default ringtone is returned.
     * @return The title of the ringtone as a [String], or the default ringtone's title if the URI is invalid or null.
     */
    fun getRingtoneTitle(ringtoneUri: String?): String

}
