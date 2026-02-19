package com.example.smartalarm.feature.alarm.framework.broadcasts.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmBroadCastAction
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmKeys
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.services.AlarmService
import com.example.smartalarm.feature.alarm.framework.worker.AlarmWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * BroadcastReceiver responsible for handling alarm-related actions such as triggering, snoozing,
 * stopping, pausing, or dismissing an alarm. It receives broadcast intents and delegates the actual
 * work to the [AlarmService] or [AlarmWorker] accordingly.
 */
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmNotificationManager: AlarmNotificationManager

    /**
     * Called when the receiver receives a broadcast intent. Determines the action and
     * delegates handling to the appropriate method.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent?.action ?: return
        context?.let {
            when (action) {
                AlarmBroadCastAction.ACTION_TRIGGER,
                AlarmBroadCastAction.ACTION_PAUSE,
                AlarmBroadCastAction.ACTION_RESUME,
                AlarmBroadCastAction.ACTION_SNOOZE,
                AlarmBroadCastAction.ACTION_RETRIGGER,
                AlarmBroadCastAction.ACTION_TIMEOUT,
                AlarmBroadCastAction.ACTION_STOP -> handleAlarmAction(
                    context,
                    intent
                )

                AlarmBroadCastAction.ACTION_DISMISS -> handleAlarmNotificationDismiss(
                    context,
                    intent
                )
            }
        }

    }

    /**
     * Forwards alarm-related broadcast actions to the [AlarmService] for processing.
     *
     * - This method extracts the alarm ID and action from the received intent, then starts
     * the corresponding service to handle the action.
     *
     * - If the action represents triggering
     * an alarm (i.e., [AlarmBroadCastAction.ACTION_TRIGGER]), the service is started
     * as a foreground service to ensure proper execution even when the app is not in the foreground.
     *
     * - For other actions (such as snooze, stop, or pause), it starts the service normally in the background.
     *
     * @param context The application context used to start the [AlarmService].
     * @param intent The incoming broadcast intent containing the alarm action and ID.
     */

    private fun handleAlarmAction(context: Context, intent: Intent) {

        val alarmId = intent.getIntExtra(AlarmKeys.ALARM_ID, 0)

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            this.action = intent.action
            putExtra(AlarmKeys.ALARM_ID, alarmId)
        }

        if (intent.action == AlarmBroadCastAction.ACTION_TRIGGER) {
            ContextCompat.startForegroundService(context, serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

    }


    /**
     * Handles the dismissal of an alarm notification by scheduling a background task.
     *
     * This method enqueues a OneTimeWorkRequest using [AlarmWorker] to perform alarm dismissed
     * operations after the user dismisses the alarm from notification
     *
     * @param context The application context used to enqueue the work request.
     * @param intent The intent containing the alarm ID to be passed to the worker.
     */
    private fun handleAlarmNotificationDismiss(context: Context, intent: Intent) {

        val alarmId = intent.getIntExtra(AlarmKeys.ALARM_ID, 0)

        val workRequest = OneTimeWorkRequestBuilder<AlarmWorker>()
            .setInputData(
                workDataOf(
                    AlarmKeys.ALARM_ID to alarmId,
                )
            )
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)

    }

}