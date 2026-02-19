package com.example.smartalarm.core.schedular

import android.content.BroadcastReceiver
import com.example.smartalarm.core.model.TaskDestination

data class CancelTask<out T : BroadcastReceiver>(
    val id: Int,
    val receiverClass: Class<out T>,
    val destination: TaskDestination,
    val action: String? = null
)