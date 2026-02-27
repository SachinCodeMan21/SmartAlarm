package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.core.framework.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Unit tests for the [SnoozeAlarmUseCaseImpl] class. This test class validates the behavior of the `invoke` method,
 * which handles the process of snoozing an alarm. It ensures that the alarm state is updated to "SNOOZED", the alarm sound
 * is stopped, the timeout is canceled, and the next snooze alarm is scheduled properly. Additionally, it tests the posting
 * of notifications and resetting the last active alarm notification preference.
 *
 * The tests cover both success and failure scenarios, including:
 * - Successful execution where the alarm is snoozed, notifications are posted, and the snooze alarm is scheduled.
 * - Failure when the alarm cannot be found by its ID (getAlarmByIdUseCase returns an error).
 * - Failure when updating the alarm state to "SNOOZED" (updateAlarmUseCase returns an error).
 * - Failure when scheduling the snooze alarm or posting the notification fails after the state is updated.
 *
 * The tests use the MockK framework to mock dependencies and isolate the behavior of the method under test. The dependencies
 * mocked in these tests include:
 * - [UpdateAlarmUseCase]: Used to update the alarm's state in the system.
 * - [AlarmScheduler]: Used to schedule and cancel alarms.
 * - [AlarmRingtoneManager]: Used to stop the alarm sound.
 * - [AlarmNotificationManager]: Used to post and cancel notifications.
 * - [AlarmTimeHelper]: Used to calculate the next snooze time.
 * - [SharedPrefsHelper]: Used to manage shared preferences related to the alarm notification state.
 *
 * The tests use `runTest` to execute suspending functions and `advanceUntilIdle` to ensure that all coroutines finish execution
 * before assertions are made.
 */
@ExperimentalCoroutinesApi
class SnoozeAlarmUseCaseImplTest {

/*    // Mock dependencies for SnoozeAlarmUseCaseImpl
    @MockK
    private lateinit var updateAlarmUseCase: UpdateAlarmUseCase

    @MockK
    private lateinit var alarmScheduler: AlarmScheduler

    @MockK
    private lateinit var alarmRingtoneHelper: AlarmRingtoneManager

    @MockK
    private lateinit var alarmNotificationManager: AlarmNotificationManager

    @MockK
    private lateinit var alarmTimeHelper: AlarmTimeHelper

    @MockK
    private lateinit var sharedPrefsHelper: SharedPrefsHelper

    // Instance of the use case being tested, injected with mocks
    @InjectMockKs
    private lateinit var snoozeAlarmUseCase: SnoozeAlarmUseCaseImpl

    // Test data
    private val alarmId = 1
    private val alarm = AlarmModel(
        id = alarmId,
        label = "Wake Up",
        alarmSound = "sound_uri",
        snoozeSettings = SnoozeSettings(snoozeIntervalMinutes = 10, snoozedCount = 3)
    )
    private val snoozedAlarm =  alarm.copy(
        snoozeSettings = alarm.snoozeSettings.copy(
            isAlarmSnoozed = true,
            snoozedCount = alarm.snoozeSettings.snoozedCount - 1
        ),
        alarmState = AlarmState.SNOOZED
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    *//**
     * Test case for the successful execution of the `invoke` method in `SnoozeAlarmUseCaseImpl`.
     * This test validates the correct behavior when the alarm is retrieved, updated to SNOOZED,
     * the next snooze is scheduled, and notifications are posted.
     *//*
    @Test
    fun `invoke - success case`() = runTest {

        // Arrange: Setup mocks for a successful snooze operation
        coEvery { updateAlarmUseCase(snoozedAlarm) } returns Result.Success(Unit)
        every { alarmRingtoneHelper.stopAlarmRingtone() } just Runs
        every { alarmScheduler.cancelSmartAlarmTimeout(alarm.id) } just Runs
        every { alarmTimeHelper.getNextSnoozeMillis(any()) } returns 600000L // 10 minutes in milliseconds
        every { alarmScheduler.scheduleSnoozeAlarm(any(), any()) } just Runs
        every { alarmNotificationManager.postAlarmNotification(any(), any()) } just Runs
        every { sharedPrefsHelper.lastActiveAlarmNotificationPref = 0 } just Runs

        // Act: Execute the method under test
        snoozeAlarmUseCase.invoke(alarm)
        advanceUntilIdle()

        // Assert: Verify the expected behavior and results
        verify { alarmRingtoneHelper.stopAlarmRingtone() } // Ensure the alarm sound was stopped
        verify { alarmScheduler.cancelSmartAlarmTimeout(alarm.id) } // Ensure the timeout was canceled
        coVerify { updateAlarmUseCase(snoozedAlarm) } // Ensure the alarm state was updated to SNOOZED
        verify { alarmScheduler.scheduleSnoozeAlarm(alarm.id, 600000L) } // Verify the snooze time was scheduled
        verify { alarmNotificationManager.postAlarmNotification(any(), any()) } // Ensure the notification was posted
        verify { sharedPrefsHelper.lastActiveAlarmNotificationPref = 0 } // Ensure the shared preference was reset
    }


    *//**
     * Test case for when the alarm state update fails (updateAlarmUseCase returns an error).
     * This simulates a failure when trying to update the alarm state to SNOOZED.
     *//*
    @Test
    fun `invoke - error when updating alarm state`() = runTest {

        // Arrange: Setup mocks to simulate an error during the state update
        coEvery { updateAlarmUseCase(snoozedAlarm) } returns Result.Error(Exception("Failed to update alarm state"))

        // Act: Execute the method under test
        val result = snoozeAlarmUseCase.invoke(alarm)

        // Assert: Verify that the result is an error
        assertTrue(result is Result.Error)
        assertEquals("Failed to update alarm state", (result as Result.Error).exception.message)
    }

    *//**
     * Test case for when there is an error while scheduling the snooze alarm or posting the notification.
     * This simulates a failure in scheduling or posting after the alarm state is updated to SNOOZED.
     *//*
    @Test
    fun `invoke - error when scheduling snooze alarm or posting notification`() = runTest {

        // Arrange: Setup mocks for the successful alarm retrieval and state update
        coEvery { updateAlarmUseCase(snoozedAlarm) } returns Result.Success(Unit)
        every { alarmRingtoneHelper.stopAlarmRingtone() } just Runs
        every { alarmScheduler.cancelSmartAlarmTimeout(alarm.id) } just Runs
        coEvery { alarmTimeHelper.getNextSnoozeMillis(any()) } returns 600000L // 10 minutes in milliseconds
        every { alarmScheduler.scheduleSnoozeAlarm(any(), any()) } throws Exception("Failed to schedule snooze")
        every { alarmNotificationManager.postAlarmNotification(any(), any()) } just Runs

        // Act: Execute the method under test
        val result = snoozeAlarmUseCase.invoke(alarm)

        // Assert: Verify that the result is an error when scheduling the snooze alarm fails
        assertTrue(result is Result.Error)
        assertEquals("Failed to schedule snooze", (result as Result.Error).exception.message)
    }*/
}
