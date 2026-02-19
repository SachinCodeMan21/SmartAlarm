package com.example.smartalarm.feature.timer.framework.scheduler

import android.os.Bundle
import android.util.Log
import com.example.smartalarm.core.model.TaskDestination
import com.example.smartalarm.core.schedular.AppAlarmScheduler
import com.example.smartalarm.core.schedular.CancelTask
import com.example.smartalarm.core.schedular.ScheduledTask
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerBroadCastAction
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerKeys
import com.example.smartalarm.feature.timer.framework.broadcast.receiver.TimerReceiver
import javax.inject.Inject
import kotlin.jvm.java


class TimerSchedulerImpl @Inject constructor(
    private val appAlarmScheduler: AppAlarmScheduler
) : TimerScheduler
{

    //---------------------------------------------------------------
    // 1] Scheduling & Canceling Timer
    //---------------------------------------------------------------

    override fun scheduleTimer(timerId: Int, triggerAtMillis: Long) {
        val task = ScheduledTask(
            id = timerId,
            timeInMillis = triggerAtMillis,
            receiverClass = TimerReceiver::class.java,
            destination = TaskDestination.SHOW_TIMER_DISPLAY,
            extras = timerExtras(timerId),
            action = TimerBroadCastAction.ACTION_START
        )
        appAlarmScheduler.schedule(task)
    }

    override fun cancelScheduledTimer(timerId: Int) {
        val cancelTask = CancelTask(
            id = timerId,
            receiverClass = TimerReceiver::class.java,
            destination = TaskDestination.SHOW_TIMER_DISPLAY,
            action = TimerBroadCastAction.ACTION_START
        )
        appAlarmScheduler.cancel(cancelTask)
    }

    //---------------------------------------------------------------
    // 2] Scheduling & Canceling Timer Timeout
    //---------------------------------------------------------------

    override fun scheduleTimerTimeout(timerId: Int, triggerTimeInMillis: Long) {


        val timeoutTime = triggerTimeInMillis + 10000L

        Log.d("TAG","scheduleTimerTimeout executed with timeoutTime = $timeoutTime")


        val task = ScheduledTask(
            id = timerId,
            timeInMillis = timeoutTime,
            receiverClass = TimerReceiver::class.java,
            destination = TaskDestination.SHOW_TIMER_DISPLAY,
            extras = timerExtras(timerId),
            action = TimerBroadCastAction.ACTION_TIMER_TIMEOUT
        )
        appAlarmScheduler.schedule(task)
    }

    override fun cancelTimerTimeout(timerId: Int) {
        val cancelTask = CancelTask(
            id = timerId,
            receiverClass = TimerReceiver::class.java,
            destination = TaskDestination.SHOW_TIMER_DISPLAY,
            action = TimerBroadCastAction.ACTION_TIMER_TIMEOUT
        )
        appAlarmScheduler.cancel(cancelTask)
    }

    //---------------------------------------------------------------
    // 3] Scheduling & Canceling Snooze Timer
    //---------------------------------------------------------------

    override fun scheduleSnoozeTimer(timerId: Int, snoozeIntervalMillis: Long) {
        val task = ScheduledTask(
            id = timerId,
            timeInMillis = System.currentTimeMillis() + snoozeIntervalMillis,
            receiverClass = TimerReceiver::class.java,
            destination = TaskDestination.SHOW_TIMER_DISPLAY,
            extras = timerExtras(timerId),
            action = TimerBroadCastAction.ACTION_SNOOZE
        )
        appAlarmScheduler.schedule(task)
    }

    override fun cancelSnoozeTimer(timerId: Int) {
        val cancelTask = CancelTask(
            id = timerId,
            receiverClass = TimerReceiver::class.java,
            destination = TaskDestination.SHOW_TIMER_DISPLAY,
            action = TimerBroadCastAction.ACTION_SNOOZE
        )
        appAlarmScheduler.cancel(cancelTask)
    }

    //---------------------------------------------------------------
    // 4] Cancel All
    //---------------------------------------------------------------

    override fun cancelAllScheduledTimers(timerId: Int) {
        cancelScheduledTimer(timerId)
        cancelSnoozeTimer(timerId)
        cancelTimerTimeout(timerId)
    }

    //---------------------------------------------------------------
    // Helpers
    //---------------------------------------------------------------

    private fun timerExtras(timerId: Int) =
        Bundle().apply { putInt(TimerKeys.TIMER_ID, timerId) }

}
