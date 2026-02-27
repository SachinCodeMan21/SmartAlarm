package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.usecase.contract.PostSaveOrUpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import javax.inject.Inject


/**
 * Implementation of the [PostSaveOrUpdateAlarmUseCase] for saving or updating an alarm and
 * performing necessary actions such as scheduling the alarm and posting notifications.
 *
 * This class interacts with the [AlarmScheduler] to schedule the alarm, the [AlarmNotificationManager]
 * to post notifications, and the [AlarmTimeHelper] to calculate the time until the next alarm trigger.
 * It returns a [MyResult] containing a string that represents how long until the alarm triggers
 * or an empty string if the alarm is disabled.
 *
 * @param alarmScheduler Responsible for scheduling the alarm at the appropriate time.
 * @param alarmNotificationManager Manages the notifications for the alarm.
 * @param alarmTimeHelper Provides helper methods for calculating and formatting the alarm time.
 */
class PostSaveOrUpdateAlarmUseCaseImpl @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val alarmNotificationManager: AlarmNotificationManager,
    private val alarmTimeHelper: AlarmTimeHelper,
) : PostSaveOrUpdateAlarmUseCase {

    /**
     * Saves or updates the alarm, schedules it, and posts a notification for the user.
     *
     * This method performs the following actions:
     * - If the alarm is enabled, it calculates the time remaining until the next alarm trigger.
     * - Schedules the alarm and posts a notification to alert the user.
     * - Returns a formatted string representing how long until the alarm triggers.
     * - If the alarm is disabled, it returns an empty string.
     *
     * @param alarm The [AlarmModel] to be saved or updated.
     * @return A [MyResult] containing a string representing the time until the alarm triggers,
     *         or an empty string if the alarm is disabled.
     */
    override fun invoke(alarm: AlarmModel): MyResult<String, DataError> {

        return if (alarm.isEnabled) {

            // Calculate the remaining time in milliseconds for the next alarm trigger
            val alarmTimeMillis = alarmTimeHelper.calculateNextAlarmTriggerMillis(alarm.time, alarm.days)

            // Schedule the alarm with the calculated time
            alarmScheduler.scheduleSmartAlarm(alarm.id, alarmTimeMillis)

            // Post a notification to alert the user about the upcoming alarm
            alarmNotificationManager.postAlarmNotification(
                alarm.id,
                AlarmNotificationModel.UpcomingAlarmModel(alarm, alarmTimeMillis)
            )

            // Get the formatted string representing how long until the alarm triggers
            val alarmTriggerTimeTextMessage = alarmTimeHelper.getFormattedTimeUntilNextAlarm(alarmTimeMillis)

            // Return a success result with the formatted string
            MyResult.Success(alarmTriggerTimeTextMessage)
        } else {
            // Return a success result with an empty string if the alarm is disabled
            MyResult.Success("")
        }

    }
}
