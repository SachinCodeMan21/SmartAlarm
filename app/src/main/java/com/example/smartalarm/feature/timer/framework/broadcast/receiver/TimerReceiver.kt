package com.example.smartalarm.feature.timer.framework.broadcast.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerBroadCastAction
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerKeys
import com.example.smartalarm.feature.timer.framework.service.ShowTimerService


class TimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { ctx ->
            intent?.let { intent ->
                Log.d("TAG","TimerReceiver Executed With Action = ${intent.action.toString()}")
                handleAction(ctx,intent)
            }
        }
    }

    private fun handleAction(context: Context, intent: Intent) {

        val serviceIntent = Intent(context, ShowTimerService::class.java).apply {
            action = intent.action
            putExtra(TimerKeys.TIMER_ID,intent.getIntExtra(TimerKeys.TIMER_ID,0))
        }

        if (intent.action == TimerBroadCastAction.ACTION_START) {
            ContextCompat.startForegroundService(context, serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

    }

}
