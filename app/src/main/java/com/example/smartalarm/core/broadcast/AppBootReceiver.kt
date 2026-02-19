package com.example.smartalarm.core.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.domain.repository.StopWatchRepository
import com.example.smartalarm.feature.stopwatch.framework.broadcasts.constants.StopWatchBroadCastAction
import com.example.smartalarm.feature.stopwatch.framework.services.StopwatchService
import com.example.smartalarm.feature.stopwatch.domain.usecase.contract.GetCurrentStopwatchStateUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppBootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var stopWatchRepository: StopWatchRepository

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("TAG","AppBootReceiver onReceive Executed With Action : ${intent.action}")
        if (intent.action == Intent.ACTION_BOOT_COMPLETED){
            val pendingResult = goAsync()
            handleBootCompleted(context, pendingResult)
        }
    }
    private fun handleBootCompleted(context: Context, pendingResult: PendingResult) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                Log.d("TAG","AppBootReceiver handleBootCompleted Executed")
                resumeStopwatchServiceIfRunning(context)
            } finally {
                pendingResult.finish()
            }
        }
    }
    private suspend fun resumeStopwatchServiceIfRunning(context: Context) {

        val currentStopwatch = stopWatchRepository.getBootStopwatchState(1) ?: StopwatchModel()
        Log.d("TAG","AppBootReceiver resumeStopwatchServiceIfRunning Executed with stopwatch : $currentStopwatch")

        if (currentStopwatch.isRunning) {
            val intent = Intent(context, StopwatchService::class.java).apply {
                action = StopWatchBroadCastAction.BOOT_RESTORE
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }
}