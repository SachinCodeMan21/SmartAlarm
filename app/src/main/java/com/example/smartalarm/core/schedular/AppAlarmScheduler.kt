package com.example.smartalarm.core.schedular

import android.content.BroadcastReceiver
import com.example.smartalarm.core.model.TaskDestination

interface AppAlarmScheduler {
    fun <T : BroadcastReceiver> schedule(task: ScheduledTask<T>)
    fun <T : BroadcastReceiver> cancel(cancelTask: CancelTask<T>)
}