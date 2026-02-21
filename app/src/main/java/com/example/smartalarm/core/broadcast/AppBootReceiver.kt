package com.example.smartalarm.core.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.usecase.contract.GetAllAlarmsUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.MissedAlarmUseCase
import com.example.smartalarm.feature.alarm.domain.usecase.contract.PostSaveOrUpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import com.example.smartalarm.feature.timer.domain.usecase.contract.GetAllTimersUseCase
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerBroadCastAction
import com.example.smartalarm.feature.timer.framework.broadcast.receiver.TimerReceiver
import com.example.smartalarm.feature.timer.framework.scheduler.TimerScheduler
import com.example.smartalarm.feature.timer.framework.service.ShowTimerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class AppBootReceiver : BroadcastReceiver() {

    @Inject lateinit var getAllAlarmsUseCase: GetAllAlarmsUseCase
    @Inject lateinit var postSaveOrUpdateAlarmUseCase: PostSaveOrUpdateAlarmUseCase
    @Inject lateinit var missedAlarmUseCase: MissedAlarmUseCase

    @Inject lateinit var alarmScheduler: AlarmScheduler

    @Inject lateinit var alarmTimeHelper: AlarmTimeHelper

    @Inject lateinit var getAllTimersUseCase: GetAllTimersUseCase
    @Inject lateinit var timerScheduler: TimerScheduler


    override fun onReceive(context: Context, intent: Intent) {
        Log.d("TAG","AppBootReceiver onReceive Executed With Action : ${intent.action}")
        if (intent.action == Intent.ACTION_BOOT_COMPLETED){
            val pendingResult = goAsync()
            handleBootCompleted(pendingResult)
        }
    }
    private fun handleBootCompleted(pendingResult: PendingResult) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                Log.d("TAG","AppBootReceiver handleBootCompleted Executed")
                reschedulePendingAlarms()
                startTimersIfRunning()
            } finally {
                pendingResult.finish()
            }
        }
    }
    private suspend fun reschedulePendingAlarms() {

        // 1. Fetch all alarms that are either active or in an interrupted state
        val allAlarms = getAllAlarmsUseCase().first()

        val alarmsToReschedule = allAlarms.filter {
            it.isEnabled &&  (it.alarmState == AlarmState.UPCOMING || it.alarmState == AlarmState.RINGING || it.alarmState == AlarmState.SNOOZED)
        }

        val currentTime = System.currentTimeMillis()

        alarmsToReschedule.forEach { alarm ->

            when (alarm.alarmState) {

                AlarmState.RINGING -> { missedAlarmUseCase(alarm) }

                // Case B: Interrupted while Snoozed
                AlarmState.SNOOZED -> {

                    val snoozeTarget = localTimeToMillis(alarm.time) + TimeUnit.MINUTES.toMillis(alarm.snoozeSettings.snoozeIntervalMinutes.toLong())

                    if (snoozeTarget > currentTime) {
                        // The snooze hasn't happened yet—reschedule it!
                        postSaveOrUpdateAlarmUseCase(alarm)
                        //alarmScheduler.scheduleSmartAlarm(alarm.id, snoozeTarget)
                    } else {
                        // The snooze passed while phone was off—skip to next full occurrence
                        missedAlarmUseCase(alarm)
                    }
                }

                // Case C: Standard "Upcoming" (isEnabled but IDLE state)
                AlarmState.UPCOMING -> {
                    val upcomingTargetTime = localTimeToMillis(alarm.time)
                    if (upcomingTargetTime > currentTime) {
                        postSaveOrUpdateAlarmUseCase(alarm)
                        //alarmScheduler.scheduleSmartAlarm(alarm.id, upcomingTargetTime)
                    } else {
                        missedAlarmUseCase(alarm)
                    }
                }

                else -> {}
            }
        }
    }
    private suspend fun startTimersIfRunning(){
        val hasRunningTimers = getAllTimersUseCase().first().any { it.isTimerRunning }
        if (hasRunningTimers){ timerScheduler.scheduleServiceStart() }
    }

    fun localTimeToMillis(localTime: LocalTime): Long {
        // Get current date to combine with LocalTime
        val currentDate = LocalDateTime.now().toLocalDate()

        // Combine current date with the LocalTime to create a LocalDateTime
        val localDateTime = LocalDateTime.of(currentDate, localTime)

        // Convert to Instant using UTC offset, then get time in milliseconds
        return localDateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

}