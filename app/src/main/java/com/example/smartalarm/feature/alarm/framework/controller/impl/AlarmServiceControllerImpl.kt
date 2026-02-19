package com.example.smartalarm.feature.alarm.framework.controller.impl

import android.content.Context
import android.content.Intent
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmBroadCastAction
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmKeys
import com.example.smartalarm.feature.alarm.framework.broadcasts.receivers.AlarmReceiver
import com.example.smartalarm.feature.alarm.framework.controller.contract.AlarmServiceController
import com.example.smartalarm.feature.alarm.framework.services.AlarmService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmServiceControllerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : AlarmServiceController {

    override fun startAlarm(alarmId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmBroadCastAction.ACTION_TRIGGER
            putExtra(AlarmKeys.ALARM_ID, alarmId)
        }
        context.sendBroadcast(intent)
    }

    override fun snoozeAlarm(alarmId : Int){
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmBroadCastAction.ACTION_SNOOZE
            putExtra(AlarmKeys.ALARM_ID, alarmId)
        }
        context.sendBroadcast(intent)
    }

    override fun pauseAlarm(alarmId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmBroadCastAction.ACTION_PAUSE
            putExtra(AlarmKeys.ALARM_ID, alarmId)
        }
        context.sendBroadcast(intent)
    }

    override fun resumeAlarm(alarmId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmBroadCastAction.ACTION_RESUME
            putExtra(AlarmKeys.ALARM_ID, alarmId)
        }
        context.sendBroadcast(intent)
    }

    override fun stopAlarm(alarmId: Int){
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmBroadCastAction.ACTION_STOP
            putExtra(AlarmKeys.ALARM_ID, alarmId)
        }
        context.sendBroadcast(intent)
    }

    override fun stopAlarmService() {
        val intent = Intent(context, AlarmService::class.java)
        context.stopService(intent)
    }
}
