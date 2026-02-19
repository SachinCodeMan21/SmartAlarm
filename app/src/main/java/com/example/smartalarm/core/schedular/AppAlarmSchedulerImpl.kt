package com.example.smartalarm.core.schedular

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.smartalarm.core.model.TaskDestination
import com.example.smartalarm.feature.alarm.presentation.view.activity.AlarmActivity
import com.example.smartalarm.feature.home.presentation.view.HomeActivity
import com.example.smartalarm.feature.timer.presentation.view.activity.ShowTimerActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class AppAlarmSchedulerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AppAlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun <T : BroadcastReceiver> schedule(task: ScheduledTask<T>) {

        // 1. Resolve the destination enum to a real Activity Class
        val activityClass = getTargetActivity(task.destination)

        // 2. Intent for the "Next Alarm" system info (Activity)
        val contentIntent = Intent(context, activityClass).apply {
            // Essential flags to prevent multiple app instances
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            // Pass the extras here too so the Activity knows which item to show
            putExtras(task.extras)
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            task.id,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Intent for when the timer actually expires (Receiver)
        val receiverIntent = Intent(context, task.receiverClass).apply {
            task.action?.let { action = it }
            putExtras(task.extras)
        }

        val receiverPendingIntent = PendingIntent.getBroadcast(
            context,
            task.id,
            receiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 4. Final AlarmClockInfo setup
        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            task.timeInMillis,
            contentPendingIntent
        )

        try {
            alarmManager.setAlarmClock(alarmClockInfo, receiverPendingIntent)
        } catch (_: SecurityException) {
            // Fallback if exact alarm permission isn't granted
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.timeInMillis, receiverPendingIntent)
        }
    }

    override fun <T : BroadcastReceiver> cancel(cancelTask: CancelTask<T>) {

        // 1️⃣ Cancel the broadcast PendingIntent (the actual alarm trigger)
        val receiverIntent = Intent(context, cancelTask.receiverClass).apply {
            cancelTask.action?.let { setAction(it) } // Apply action if provided
        }

        val receiverPendingIntent = PendingIntent.getBroadcast(
            context,
            cancelTask.id,
            receiverIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        receiverPendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }

        // 2️⃣ Cancel the content PendingIntent (used for AlarmClockInfo / status bar)
        val contentIntent = Intent(context, getTargetActivity(cancelTask.destination))
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            cancelTask.id,
            contentIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        contentPendingIntent?.cancel()
    }

    /**
     * This is the "Bridge". It maps the clean Enum to the actual Activity.
     * If you rename an Activity later, you only change it here.
     */
    private fun getTargetActivity(destination: TaskDestination): Class<out Activity> {
        return when (destination) {
            TaskDestination.HOME_SCREEN -> HomeActivity::class.java
            TaskDestination.ALARM_DISPLAY -> AlarmActivity::class.java
            TaskDestination.SHOW_TIMER_DISPLAY -> ShowTimerActivity::class.java
        }
    }


}