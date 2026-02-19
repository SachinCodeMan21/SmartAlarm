package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import org.junit.Before
import org.junit.Test
import io.mockk.impl.annotations.MockK
import java.time.LocalTime
import kotlin.test.assertEquals
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue

@ExperimentalCoroutinesApi
class ToggleAlarmUseCaseImplTest {

    @MockK
    private lateinit var updateAlarmUseCase: UpdateAlarmUseCase

    @MockK
    private lateinit var alarmScheduler: AlarmScheduler

    @MockK
    private lateinit var alarmNotificationManager: AlarmNotificationManager

    @MockK
    private lateinit var alarmTimeHelper: AlarmTimeHelper

    @InjectMockKs
    private lateinit var toggleAlarmUseCase: ToggleAlarmUseCaseImpl

    private val alarmId = 1
    private val alarmModel = AlarmModel(
        id = alarmId,
        label = "Test Alarm",
        time = LocalTime.of(7, 0),
        days = setOf(DayOfWeek.MON),
        isEnabled = false,
        alarmState = AlarmState.UPCOMING,
        snoozeSettings = SnoozeSettings(isSnoozeEnabled = true, snoozeLimit = 3, snoozedCount = 2, snoozeIntervalMinutes = 5)
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Test that toggling the alarm on schedules it and posts a notification.
     */
    @Test
    fun `invoke - enable alarm - schedules and posts notification`() = runTest {

        // Arrange
        val updatedAlarm = alarmModel.copy(
            isEnabled = true,
            snoozeSettings = alarmModel.snoozeSettings.copy(isAlarmSnoozed = false, snoozedCount = alarmModel.snoozeSettings.snoozeLimit),
            alarmState =AlarmState.UPCOMING
        )
        coEvery { updateAlarmUseCase(updatedAlarm) } returns Result.Success(Unit)
        every { alarmTimeHelper.calculateNextAlarmTriggerMillis(any(), any()) } returns 123456789L
        every { alarmTimeHelper.getFormattedTimeUntilNextAlarm(any()) } returns "7:00 AM"
        every { alarmScheduler.scheduleSmartAlarm(any(), any()) } just Runs
        every { alarmNotificationManager.postAlarmNotification(any(), any()) } just Runs

        // Act
        val result = toggleAlarmUseCase.invoke(alarmModel, isEnabled = true)
        advanceUntilIdle()

        // Assert
        assertTrue(result is Result.Success)
        assertEquals("7:00 AM", (result as Result.Success).data)
        coVerify(exactly = 1) { updateAlarmUseCase(updatedAlarm) }
        verify(exactly = 1) { alarmScheduler.scheduleSmartAlarm(alarmId, 123456789L) }
        verify(exactly = 1) { alarmNotificationManager.postAlarmNotification(alarmId, any()) }
    }

    /**
     * Test that toggling the alarm off cancels it and removes the notification.
     */
    @Test
    fun `invoke - disable alarm - cancels and removes notification`() = runTest {
        // Arrange
        val updatedAlarm = alarmModel.copy(
            isEnabled = false,
            snoozeSettings = alarmModel.snoozeSettings.copy(isAlarmSnoozed = false, snoozedCount = alarmModel.snoozeSettings.snoozeLimit),
            alarmState =AlarmState.EXPIRED
        )
        coEvery { updateAlarmUseCase(updatedAlarm) } returns Result.Success(Unit)
        every { alarmScheduler.cancelAllScheduledAlarms(any()) } just Runs
        every { alarmNotificationManager.cancelAlarmNotification(any()) } just Runs

        // Act
        val result = toggleAlarmUseCase.invoke(alarmModel, isEnabled = false)
        advanceUntilIdle()

        // Assert
        assertTrue(result is Result.Success)
        assertEquals("", (result as Result.Success).data)
        coVerify(exactly = 1) { updateAlarmUseCase(updatedAlarm) }
        verify(exactly = 1) { alarmScheduler.cancelAllScheduledAlarms(alarmId) }
        verify(exactly = 1) { alarmNotificationManager.cancelAlarmNotification(alarmId) }
    }

    /**
     * Test that if updating the alarm fails, an error is returned and no further actions are taken.
     */
    @Test
    fun `invoke - update fails - returns error and does not schedule or notify`() = runTest {
        // Arrange
        coEvery { updateAlarmUseCase(any()) } returns Result.Error(RuntimeException("DB write failed"))

        // Act
        val result = toggleAlarmUseCase.invoke(alarmModel, isEnabled = true)

        // Assert
        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { updateAlarmUseCase(any()) }
        verify(exactly = 0) { alarmScheduler.scheduleSmartAlarm(any(), any()) }
        verify(exactly = 0) { alarmNotificationManager.postAlarmNotification(any(), any()) }
    }

    /**
     * Test that if the alarm is successfully updated but scheduling or posting notifications fails,
     * the operation still returns success with an empty string as no further actions were needed.
     */
    @Test
    fun `invoke - schedule or notification fail - returns success with empty string`() = runTest {
        // Arrange
        val updatedAlarm = alarmModel.copy(
            isEnabled = true,
            snoozeSettings = alarmModel.snoozeSettings.copy(isAlarmSnoozed = false, snoozedCount = alarmModel.snoozeSettings.snoozeLimit),
            alarmState =AlarmState.UPCOMING
        )
        coEvery { updateAlarmUseCase(updatedAlarm) } returns Result.Success(Unit)
        every { alarmTimeHelper.calculateNextAlarmTriggerMillis(any(), any()) } returns 123456789L
        every { alarmTimeHelper.getFormattedTimeUntilNextAlarm(any()) } returns "7:00 AM"
        every { alarmScheduler.scheduleSmartAlarm(any(), any()) } throws Exception("Scheduler failed")
        every { alarmNotificationManager.postAlarmNotification(any(), any()) } throws Exception("Notification failed")

        // Act
        val result = toggleAlarmUseCase.invoke(alarmModel, isEnabled = true)

        // Assert
        assertTrue(result is Result.Error) // The result should be an error because the side effects failed
        coVerify(exactly = 1) { updateAlarmUseCase(updatedAlarm) }
        verify(exactly = 1) { alarmScheduler.scheduleSmartAlarm(any(), any()) }
        verify(exactly = 0) { alarmNotificationManager.postAlarmNotification(any(), any()) }
    }

}