package com.example.smartalarm.feature.alarm.framework.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smartalarm.core.utility.extension.logDebug
import com.example.smartalarm.feature.alarm.domain.usecase.contract.DismissAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmKeys
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


/**
 * A worker that manages the dismissal of alarms in the background.
 *
 * This worker is responsible for handling alarm dismissal requests by invoking the [DismissAlarmUseCase].
 * It is triggered when an alarm needs to be dismissed based on the ID passed in the input data.
 *
 * The worker performs the following operations:
 * 1. Retrieves the alarm ID from the input data.
 * 2. Calls the [DismissAlarmUseCase] to dismiss the alarm:
 *    - If the alarm is found, it updates the alarm state accordingly (marking it as expired or rescheduling it).
 *    - If the alarm is not found or cannot be updated, it logs a failure.
 * 3. Returns a [Result.success] if the alarm was successfully dismissed or updated, or [Result.failure] if the alarm could not be found or updated.
 *
 * This worker is typically used for background tasks involving alarm dismissal, such as when the alarm
 * needs to be stopped or rescheduled after being triggered.
 *
 * @param context The context used by the worker.
 * @param workerParams The worker parameters, containing input data like the alarm ID.
 * @param dismissAlarmUseCase The use case for dismissing alarms.
 */
@HiltWorker
class AlarmWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dismissAlarmUseCase: DismissAlarmUseCase
) : CoroutineWorker(context, workerParams) {

    /**
     * Executes the background task to dismiss the alarm.
     *
     * This method performs the following steps:
     * 1. Retrieves the alarm ID from the input data.
     * 2. Uses [DismissAlarmUseCase] to dismiss the alarm by updating its state (either marking it as expired
     *    or rescheduling it if it’s a repeating alarm).
     * 3. Returns [Result.success] if the alarm was successfully dismissed, or [Result.failure] if the alarm could not be found or updated.
     *
     * @return A [Result] indicating the success or failure of the task.
     */
    override suspend fun doWork(): Result {

        val alarmId = inputData.getInt(AlarmKeys.ALARM_ID, 0)

        if (alarmId == 0) {
            logDebug("Invalid alarm ID = 0")
            return Result.success()
        }

        // Use the DismissAlarmUseCase to handle the alarm dismissal
        val updatedAlarm = dismissAlarmUseCase(alarmId)

        // If no alarm was found or updated, return failure
        if (updatedAlarm == null) {
            logDebug("Alarm not found for ID = $alarmId")
            return Result.failure()
        }

        return Result.success()
    }

}
