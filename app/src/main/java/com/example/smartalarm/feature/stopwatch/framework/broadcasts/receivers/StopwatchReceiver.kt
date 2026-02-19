package com.example.smartalarm.feature.stopwatch.framework.broadcasts.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.smartalarm.feature.stopwatch.framework.services.StopwatchService
import com.example.smartalarm.feature.stopwatch.framework.broadcasts.constants.StopWatchBroadCastAction
import com.example.smartalarm.feature.stopwatch.framework.broadcasts.constants.StopWatchKey

/**
 * BroadcastReceiver to handle stopwatch-related broadcast intents.
 *
 * 1. Listens for stopwatch control actions sent as broadcast intents.
 * 2. On receiving an intent, delegates the action handling to [handleAction].
 * 3. Forwards the intent to [StopwatchService], starting it either as a foreground or normal service
 *    depending on the action type.
 */
class StopwatchReceiver : BroadcastReceiver() {

    /**
     * Called when the BroadcastReceiver receives an Intent broadcast.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received, which contains the stopwatch action.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { ctx ->
            intent?.let { intent ->
                handleAction(ctx, intent)
            }
        }
    }

    /**
     * Handles the stopwatch action by forwarding the intent to [StopwatchService].
     *
     * 1. Creates a new intent targeting [StopwatchService] with the same action and stopwatch ID from the input intent.
     * 2. If the action is START, starts the service as a foreground service using [ContextCompat.startForegroundService].
     * 3. For all other actions, starts the service normally with [Context.startService].
     *
     * @param context The context used to start the service.
     * @param intent The intent containing the action and stopwatch ID.
     */
    private fun handleAction(context: Context, intent: Intent) {

        val serviceIntent = Intent(context, StopwatchService::class.java).apply {
            this.action = intent.action
            putExtra(StopWatchKey.ID,intent.getIntExtra(StopWatchKey.ID,0))
        }

        if (intent.action == StopWatchBroadCastAction.START_FOREGROUND) {
            ContextCompat.startForegroundService(context, serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

}