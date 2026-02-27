package com.example.smartalarm.feature.timer.utility

import android.content.Context
import android.media.RingtoneManager
import com.example.smartalarm.core.framework.ringtone.BaseRingtonePlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerRingtonePlayer @Inject constructor(
    @param:ApplicationContext private val context: Context
) : BaseRingtonePlayer(context) {

    fun playDefaultTimer() {
        val timerUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        play(timerUri)
    }
}

