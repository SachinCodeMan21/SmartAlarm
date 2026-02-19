package com.example.smartalarm.feature.alarm.framework.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.extension.logDebug
import com.example.smartalarm.core.utility.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.usecase.contract.GetAlarmByIdUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SnoozeAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.StopAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.MissedAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.RingAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmBroadCastAction
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmKeys
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel.ScheduledAlarmModel
import com.example.smartalarm.core.permission.PermissionManager
import com.example.smartalarm.core.utility.Constants.PACKAGE
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class AlarmService : Service() {

    companion object {

        /**
         * Notification ID for the ringing alarm to manage updates or clearing.
         */
        const val RINGING_ALARM_NOTIFICATION_ID = 1001

        const val ACTION_FINISH_ALARM_ACTIVITY = "$PACKAGE.ACTION_FINISH_ALARM_ACTIVITY"


    }


    // ---------------------------------------------------------------------
    // Dependencies (Injected via Hilt)
    // ---------------------------------------------------------------------

    // Injected dependencies for alarm-related use cases
    @Inject
    lateinit var getAlarmByIdUseCase: GetAlarmByIdUseCase

    @Inject
    lateinit var ringAlarmUseCase: RingAlarmUseCase

    @Inject
    lateinit var snoozeAlarmUseCase: SnoozeAlarmUseCase

    @Inject
    lateinit var missedAlarmUseCase: MissedAlarmUseCase

    @Inject
    lateinit var stopAlarmUseCase: StopAlarmUseCase


    // Injected dependencies for various helper classes
    @Inject
    lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var alarmNotificationManager: AlarmNotificationManager

    @Inject
    lateinit var alarmRingtoneManager : AlarmRingtoneManager

    @Inject
    lateinit var vibrationManager: VibrationManager

    @Inject
    lateinit var sharedPrefsHelper: SharedPrefsHelper


    // ---------------------------------------------------------------------
    // Properties
    // ---------------------------------------------------------------------

    /**
     * Coroutine scope for running background tasks safely within the service.
     */
    private var serviceScope: CoroutineScope? = null



    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes the service scope with an IO dispatcher and a supervisor job.
     * This scope is used for launching background coroutines in the service.
     */
    override fun onCreate() {
        super.onCreate()
        serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    }

    /**
     * Handles incoming commands to control the alarm service based on the action specified in the intent.
     *
     * This method processes different alarm actions such as triggering, pausing, resuming, snoozing,
     * timing out, or stopping an alarm. Each action is handled by invoking the corresponding method
     * to perform the required operation on the alarm.
     *
     * @param intent The intent containing the action and alarm ID for the operation.
     * @param flags Any flags that were set for the service start.
     * @param startId The ID that uniquely identifies this specific start request.
     * @return The service's ongoing status after handling the command.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {

            val alarmId = it.getIntExtra(AlarmKeys.ALARM_ID, 0)
            val action = it.action ?: return START_NOT_STICKY

            when (action) {
                AlarmBroadCastAction.ACTION_TRIGGER -> handleAlarmStart(alarmId)
                AlarmBroadCastAction.ACTION_PAUSE -> handleAlarmPause()
                AlarmBroadCastAction.ACTION_RESUME -> handleAlarmResume(alarmId)
                AlarmBroadCastAction.ACTION_SNOOZE -> handleAlarmSnooze(alarmId)
                AlarmBroadCastAction.ACTION_TIMEOUT -> handleAlarmTimeout(alarmId)
                AlarmBroadCastAction.ACTION_STOP -> handleAlarmStop(alarmId)
            }

        }

        return START_STICKY
    }


    /**
     * This service does not support binding, so this method always returns null.
     *
     * @param intent The binding intent (ignored).
     * @return Always null since binding is not allowed.
     */
    override fun onBind(intent: Intent?): IBinder? = null


    /**
     * Cleans up resources when the service is destroyed.
     *
     * Stops any currently playing alarm ringtone and cancels the service coroutine scope
     * to prevent memory leaks or ongoing background work.
     */
    override fun onDestroy() {
        super.onDestroy()
        serviceScope?.cancel()
    }



    // ---------------------------------------------------------------------
    // Alarm Action Handlers
    // ---------------------------------------------------------------------

    /**
     * Handles the action of starting an alarm.
     * This method checks if the last active alarm needs to be marked as "missed" before starting the new alarm.
     * If there is an active missed alarm, it updates its state, and then it triggers the ringing of the new alarm.
     *
     * @param alarmId The ID of the alarm to start.
     */
    private fun handleAlarmStart(alarmId: Int) {
        // Handle alarm action with permission check
        handleAlarmActionWithPermissions(alarmId) { alarm ->

            val lastActiveAlarmNotificationId = sharedPrefsHelper.lastActiveAlarmNotificationPref


            // Check if we need to update the last active alarm to missed
            if (lastActiveAlarmNotificationId > 0) {

                val lastActiveAlarm = getAlarm(lastActiveAlarmNotificationId)
                if (lastActiveAlarm == null) {
                    stopSelf()
                    return@handleAlarmActionWithPermissions
                }

                // Handle missed alarm update
                when (missedAlarmUseCase(lastActiveAlarm)) {
                    is Result.Success -> {
                        // No action required if missed alarm is successfully handled
                    }
                    is Result.Error -> {
                        stopSelf()
                        return@handleAlarmActionWithPermissions
                    }
                }
            }

            // Now, handle the ringing alarm
            when (val ringingResult = ringAlarmUseCase(alarm)) {
                is Result.Success -> {
                    showForegroundAlarmNotification(ringingResult.data)
                }
                is Result.Error -> {
                    stopSelf()
                }
            }
        }
    }


    /**
     * Handles the action of snoozing an alarm.
     * It triggers the snooze functionality for the given alarm, and then stops the foreground service.
     *
     * @param alarmId The ID of the alarm to snooze.
     */
    private fun handleAlarmSnooze(alarmId: Int) {
        handleAlarmActionWithPermissions(alarmId) { alarm ->
            when (snoozeAlarmUseCase(alarm)) {
                is Result.Success,
                is Result.Error -> {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }
    }


    /**
     * Pauses the currently active alarm by stopping its ringtone and vibration.
     * This method stops both the sound and vibration of the alarm.
     */
    private fun handleAlarmPause() {
        alarmRingtoneManager.stopAlarmRingtone()
        vibrationManager.stopVibration()
    }

    /**
     * Resumes a paused alarm by playing its ringtone and starting vibration (if enabled).
     * This method will resume the alarm's sound and vibration based on the provided alarm details.
     *
     * @param alarmId The ID of the alarm to resume.
     */
    private fun handleAlarmResume(alarmId: Int) {
        handleAlarmActionWithPermissions(alarmId) {
            val alarm = getAlarm(alarmId)
            alarm?.let {
                alarmRingtoneManager.playAlarmRingtone(alarm.alarmSound, alarm.volume)
                if (it.isVibrateEnabled) vibrationManager.startVibration()
            }
        }
    }


    /**
     * Handles the timeout action for an alarm.
     * If the alarm's timeout has been reached, it marks the alarm as missed and stops the foreground service.
     *
     * @param alarmId The ID of the alarm that has timed out.
     */
    private fun handleAlarmTimeout(alarmId: Int) {
        handleAlarmActionWithPermissions(alarmId) { alarm ->

            when (missedAlarmUseCase(alarm)) {
                is Result.Success -> {
                    alarmRingtoneManager.stopAlarmRingtone()
                    vibrationManager.stopVibration()
                    sendFinishBroadcast()
                }
                is Result.Error -> {}
            }

            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }


    /**
     * Stops the currently active alarm by marking it as stopped and canceling its associated actions.
     * It triggers the stop alarm use case, then stops the foreground service and removes any related notifications.
     *
     * @param alarmId The ID of the alarm to stop.
     */
    private fun handleAlarmStop(alarmId: Int) {
        handleAlarmActionWithPermissions(alarmId) { alarm ->
            when (stopAlarmUseCase(alarm)) {
                is Result.Success -> {
                    sendFinishBroadcast()
                }
                is Result.Error -> {}
            }
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }



    // ---------------------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------------------

    /**
     * Displays the foreground notification for an active alarm.
     *
     * This method creates a notification based on the provided alarm and displays it as a foreground service
     * notification. It ensures the alarm notification is visible while the alarm is active.
     *
     * @param alarm The alarm model containing the details for the notification.
     */
    private suspend fun showForegroundAlarmNotification(alarm: AlarmModel) {
        val scheduledAlarmNotification = alarmNotificationManager.getAlarmNotification(ScheduledAlarmModel(alarm))
        withContext(Dispatchers.Main) {
            startForeground(RINGING_ALARM_NOTIFICATION_ID, scheduledAlarmNotification)
        }
    }


    /**
     * Executes an alarm action if the required permissions are granted.
     *
     * This method checks if both post-notification and exact alarm permissions are granted. If permissions
     * are granted, it retrieves the alarm by its ID and performs the specified action. If permissions are denied,
     * it logs a debug message and skips the action.
     *
     * @param alarmId The ID of the alarm to perform the action on.
     * @param action A suspending function that defines the action to be executed on the alarm.
     */
    private fun handleAlarmActionWithPermissions(alarmId : Int, action: suspend (AlarmModel) -> Unit) {
        serviceScope?.launch {
            // Check if both notification and exact alarm permissions are granted
            if (permissionManager.isPostNotificationPermissionGranted() && permissionManager.isScheduleExactAlarmPermissionGranted()) {
                val alarmId = getAlarm(alarmId)
                alarmId?.let { action(it) }

            } else {
                // Handle permission denial (if needed)
                logDebug("Permissions not granted. Action skipped.")
            }
        }
    }


    /**
     * Retrieves an alarm by its ID.
     *
     * This method interacts with the `getAlarmByIdUseCase` to fetch the alarm data based on the provided
     * `alarmId`. If the retrieval is successful, it returns the alarm model. In case of an error, it logs
     * the error message and returns `null`, ensuring safe handling of failure scenarios.
     *
     * @param alarmId The ID of the alarm to retrieve.
     * @return The [AlarmModel] if found, or `null` if an error occurs during retrieval.
     */
    private suspend fun getAlarm(alarmId : Int) : AlarmModel? {
        return when(val result = getAlarmByIdUseCase(alarmId)){
            is Result.Success -> result.data
            is Result.Error -> { null }
        }
    }

    private fun sendFinishBroadcast() {
        val intent = Intent(ACTION_FINISH_ALARM_ACTIVITY).apply {
            // This is mandatory for NOT_EXPORTED receivers on modern Android
            `package` = packageName
        }
        sendBroadcast(intent)
    }

}
