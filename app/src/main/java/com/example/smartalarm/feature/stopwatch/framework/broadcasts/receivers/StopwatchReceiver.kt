package com.example.smartalarm.feature.stopwatch.framework.broadcasts.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.smartalarm.feature.stopwatch.framework.services.StopwatchService
import com.example.smartalarm.feature.stopwatch.framework.broadcasts.constants.StopWatchBroadCastAction
import com.example.smartalarm.feature.stopwatch.framework.broadcasts.constants.StopWatchBroadCastAction.START_FOREGROUND


/**
 * Command dispatcher responsible for intercepting stopwatch-related broadcast intents.
 * * This receiver acts as a bridge between external components (e.g., Notification actions,
 * Widgets) and the [StopwatchService]. It ensures that control signals are correctly
 * routed and that the service lifecycle is managed according to Android background
 * execution requirements.
 */
class StopwatchReceiver : BroadcastReceiver() {

    /**
     * Entry point for incoming broadcast signals.
     * Validates the context and intent before delegating to the internal action handler.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        handleAction(context, intent)
    }

    /**
     * Translates broadcast intents into Service commands.
     * * Implements logic to satisfy Android's 'Background Service Start' restrictions by
     * promoting the intent to a Foreground Service start when the [START_FOREGROUND]
     * action is detected.
     *
     * @param context The operational context.
     * @param intent The intercepted broadcast containing the [StopWatchBroadCastAction].
     */
    private fun handleAction(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, StopwatchService::class.java).apply {
            action = intent.action
        }

        // Logic check for Foreground promotion to prevent BackgroundServiceStartNotAllowedException
        if (intent.action == START_FOREGROUND) {
            ContextCompat.startForegroundService(context, serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}