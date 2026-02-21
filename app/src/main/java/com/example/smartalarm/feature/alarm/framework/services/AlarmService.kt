package com.example.smartalarm.feature.alarm.framework.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.smartalarm.core.model.Result
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
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel.RingingAlarmModel
import com.example.smartalarm.core.permission.PermissionManager
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.repository.AlarmRepository
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
        private const val RINGING_ALARM_NOTIFICATION_ID = 1001
    }

    // ---------------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------------

    @Inject lateinit var getAlarmByIdUseCase: GetAlarmByIdUseCase
    @Inject lateinit var ringAlarmUseCase: RingAlarmUseCase
    @Inject lateinit var snoozeAlarmUseCase: SnoozeAlarmUseCase
    @Inject lateinit var missedAlarmUseCase: MissedAlarmUseCase
    @Inject lateinit var stopAlarmUseCase: StopAlarmUseCase

    @Inject lateinit var alarmRepository: AlarmRepository
    @Inject lateinit var alarmNotificationManager: AlarmNotificationManager
    @Inject lateinit var alarmRingtoneManager: AlarmRingtoneManager
    @Inject lateinit var vibrationManager: VibrationManager
    @Inject lateinit var permissionManager: PermissionManager


    // ---------------------------------------------------------------------
    // Properties
    // ---------------------------------------------------------------------

    private var serviceScope: CoroutineScope? = null
    private var isObserverActive = false



    // Flag to track if we are currently active
    private var isCurrentlyRinging = false




    // ---------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------'

    override fun onCreate() {
        super.onCreate()
        serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        observeAlarmState()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val alarmId = it.getIntExtra(AlarmKeys.ALARM_ID, 0)
            val action = it.action ?: return START_NOT_STICKY

            when (action) {
                // Now most actions simply update the DB via UseCases
                AlarmBroadCastAction.ACTION_TRIGGER -> handleNewAlarmTrigger(alarmId)
                AlarmBroadCastAction.ACTION_SNOOZE -> handleSnooze(alarmId)
                AlarmBroadCastAction.ACTION_STOP -> handleStop(alarmId)

                // Pure Hardware actions
                AlarmBroadCastAction.ACTION_PAUSE -> pauseHardware()
                AlarmBroadCastAction.ACTION_RESUME -> resumeHardware(alarmId)
            }
        }

        return START_STICKY
    }



    // ---------------------------------------------------------------------
    // Core Logic: The Observer
    // ---------------------------------------------------------------------

    private fun observeAlarmState() {
        if (isObserverActive) return
        isObserverActive = true

        serviceScope?.launch {

            alarmRepository.getAlarms().collect { allAlarms ->

                val ringingAlarms = allAlarms.filter { it.alarmState == AlarmState.RINGING }

                if (ringingAlarms.isEmpty()) {
                    // CASE 1: The list is empty, but we WERE ringing.
                    // This means the user stopped/snoozed the last alarm.
                    if (isCurrentlyRinging) {
                        terminateService()
                    }
                    // CASE 2: The list is empty and we WEREN'T ringing.
                    // This happens during the millisecond gap when the service
                    // first starts but before handleInitialTrigger updates the DB.
                    // We do nothing and wait for the next emission.
                    return@collect
                }

                // If we reached here, we have at least one ringing alarm
                isCurrentlyRinging = true

                // Resolve Conflicts (Handover)
                val primaryAlarm = ringingAlarms.maxByOrNull { it.id } ?: return@collect

                // Update UI and Hardware
                updateServicePresence(primaryAlarm)

                ringingAlarms.filter { it.id != primaryAlarm.id }.forEach { olderAlarm ->
                    missedAlarmUseCase(olderAlarm)
                }


            }
        }
    }
    private suspend fun updateServicePresence(alarm: AlarmModel) {
        // Manage Hardware
        alarmRingtoneManager.playAlarmRingtone(alarm.alarmSound, alarm.volume)
        if (alarm.isVibrateEnabled) vibrationManager.startVibration()

        // Manage Notification
        val notification = alarmNotificationManager.getAlarmNotification(RingingAlarmModel(alarm))
        withContext(Dispatchers.Main) {
            // Use Dynamic ID: Alarm A (Missed) keeps ID 1, Alarm B (Ringing) takes ID 2
            startForeground(alarm.id + RINGING_ALARM_NOTIFICATION_ID, notification)
        }
    }


    // ---------------------------------------------------------------------
    // Action Handlers (Now DB focused)
    // ---------------------------------------------------------------------

    private fun handleNewAlarmTrigger(alarmId: Int) {
        serviceScope?.launch {
            if (permissionManager.isPostNotificationPermissionGranted()) {
                val alarm = getAlarm(alarmId)
                // UseCase handles setting DB state to RINGING
                alarm?.let { ringAlarmUseCase(it) }
            }
        }
    }
    private fun handleSnooze(alarmId: Int) {
        serviceScope?.launch {
            val alarm = getAlarm(alarmId)
            // UseCase updates DB -> Observer sees it -> Service stops if list is empty
            alarm?.let { if (it.snoozeSettings.snoozedCount > 0) snoozeAlarmUseCase(it) }
        }
    }
    private fun handleStop(alarmId: Int) {
        serviceScope?.launch {
            val alarm = getAlarm(alarmId)
            alarm?.let { stopAlarmUseCase(it) }
        }
    }
    private fun pauseHardware() {
        alarmRingtoneManager.stopAlarmRingtone()
        vibrationManager.stopVibration()
    }
    private fun resumeHardware(alarmId: Int) {
        serviceScope?.launch {
            getAlarm(alarmId)?.let { alarm ->
                alarmRingtoneManager.playAlarmRingtone(alarm.alarmSound, alarm.volume)
                if (alarm.isVibrateEnabled) vibrationManager.startVibration()
            }
        }
    }
    private fun stopHardware() {
        alarmRingtoneManager.stopAlarmRingtone()
        vibrationManager.stopVibration()
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private suspend fun getAlarm(alarmId: Int): AlarmModel? {
        return when (val result = getAlarmByIdUseCase(alarmId)) {
            is Result.Success -> result.data
            else -> null
        }
    }
    private fun terminateService() {
        isCurrentlyRinging = false
        stopHardware()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope?.cancel()
    }


}