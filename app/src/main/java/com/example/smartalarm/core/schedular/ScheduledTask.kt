package com.example.smartalarm.core.schedular

import android.app.Activity
import android.content.BroadcastReceiver
import android.os.Bundle
import com.example.smartalarm.core.model.TaskDestination

/**
 * A generic blueprint for any action that needs to be triggered at a specific time.
 * Used by Alarms, Timers, and Stopwatch features.
 */
data class ScheduledTask<out T : BroadcastReceiver>(
    val id: Int,
    val timeInMillis: Long = 0L,
    val receiverClass: Class<out T>,
    val destination: TaskDestination,
    val extras: Bundle = Bundle(),
    val action: String? = null // Optional receiver action
)
