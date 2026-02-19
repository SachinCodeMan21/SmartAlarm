package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.core.utility.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import io.mockk.impl.annotations.MockK
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import org.junit.After
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import kotlinx.coroutines.test.advanceUntilIdle

/**
 * Unit tests for the [RingAlarmUseCaseImpl] class. This test class validates the behavior of the `invoke` method,
 * which handles the process of ringing an alarm, including retrieving the alarm, updating its state to "RINGING",
 * playing the alarm sound, canceling notifications, and scheduling timeouts.
 *
 * The tests cover both success and failure scenarios, including:
 * - Successful execution of the `invoke` method.
 * - Failure during the handling of missed alarms.
 * - Failure when retrieving the alarm by ID.
 * - Failure when updating the alarm's state to "RINGING".
 *
 * The tests use the MockK framework to mock dependencies and isolate the behavior of the method under test. The
 * dependencies mocked in these tests include:
 * - [UpdateAlarmUseCase]: Used to update the alarm's state in the system.
 * - [AlarmRingtoneManager]: Used to play and stop alarm sounds.
 * - [AlarmScheduler]: Used to schedule the alarm timeout.
 * - [AlarmNotificationManager]: Used to cancel alarm notifications.
 * - [SharedPrefsHelper]: Used to manage shared preferences related to alarm notifications.
 *
 * The tests use `runTest` to execute suspending functions and `advanceUntilIdle` to ensure all coroutines finish execution.
 */
@ExperimentalCoroutinesApi
class RingAlarmUseCaseImplTest {

    // Mocked dependencies of RingAlarmUseCaseImpl
    @MockK
    private lateinit var updateAlarmUseCase: UpdateAlarmUseCase

    @MockK
    private lateinit var alarmRingtoneManager: AlarmRingtoneManager

    @MockK
    private lateinit var alarmScheduler: AlarmScheduler

    @MockK
    private lateinit var alarmNotificationManager: AlarmNotificationManager

    @MockK
    private lateinit var sharedPrefsHelper: SharedPrefsHelper

    @InjectMockKs
    private lateinit var ringAlarmUseCase: RingAlarmUseCaseImpl


    // Test data
    private val alarm = AlarmModel(id = 1, label = "Wake Up", alarmSound = "sound_uri")
    private val ringingAlarm = alarm.copy(alarmState = AlarmState.RINGING)

    // Initialize mocks and other resources before each test
    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
    }

    // Clean up mocks after each test
    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Test case for the successful execution of the `invoke` method of `RingAlarmUseCaseImpl`.
     * This test validates the behavior when the alarm is updated to RINGING, and all necessary actions like canceling notifications and playing the alarm sound are executed successfully.
     */
    @Test
    fun `invoke - success case`() = runTest {

        // Arrange: Set up mocks to simulate a successful scenario
        coEvery { updateAlarmUseCase(ringingAlarm) } returns Result.Success(Unit)
        every { sharedPrefsHelper.lastActiveAlarmNotificationPref } returns alarm.id
        every { alarmNotificationManager.cancelAlarmNotification(any()) } just Runs
        every { alarmRingtoneManager.playAlarmRingtone(any(),any()) } just Runs
        every { alarmScheduler.scheduleSmartAlarmTimeout(any(), 60000L) } just Runs
        every { sharedPrefsHelper.lastActiveAlarmNotificationPref = alarm.id } just Runs

        // Act: Execute the method under test
        val result = ringAlarmUseCase.invoke(alarm) // Passing the alarm directly
        advanceUntilIdle() // Ensure any coroutines finish

        // Assert: Verify the expected behavior and results
        assertTrue(result is Result.Success) // Ensure the result is Success
        assertEquals(alarm.id, (result as Result.Success).data.id) // Check if the correct alarm is returned
        coVerify { updateAlarmUseCase(ringingAlarm) }
        verify { alarmRingtoneManager.playAlarmRingtone(any(),any()) } // Verify alarm sound was played
        verify { alarmScheduler.scheduleSmartAlarmTimeout(any(), any()) } // Verify timeout schedule
        verify { alarmNotificationManager.cancelAlarmNotification(any()) } // Verify cancellation of the notification
        verify { sharedPrefsHelper.lastActiveAlarmNotificationPref = alarm.id } // Verify shared preferences update
    }

    /**
     * Test case for the failure scenario when retrieving the alarm.
     * This test simulates a case where the alarm retrieval fails (e.g., the alarm is null).
     */
    @Test
    fun `invoke - error when retrieving alarm`() = runTest {

        // Arrange: Setup mocks to simulate an error scenario where alarm is null
        coEvery { updateAlarmUseCase(any()) } returns Result.Error(Exception("Alarm not found"))
        every { sharedPrefsHelper.lastActiveAlarmNotificationPref } returns alarm.id

        // Act: Execute the method under test
        val result = ringAlarmUseCase.invoke(alarm) // Passing the alarm directly
        advanceUntilIdle()

        // Assert: Verify the error result
        assertTrue(result is Result.Error) // Ensure the result is an error
        assertEquals("Alarm not found", (result as Result.Error).exception.message) // Verify the error message
    }

    /**
     * Test case for the failure scenario when updating the alarm state to "RINGING".
     * This test simulates a failure in updating the alarm's state.
     */
    @Test
    fun `invoke - error when updating alarm state`() = runTest {

        // Arrange: Setup mocks to simulate an error during update
        coEvery { updateAlarmUseCase(any()) } returns Result.Error(Exception("Failed to update alarm state"))
        every { sharedPrefsHelper.lastActiveAlarmNotificationPref } returns alarm.id

        // Act: Execute the method under test
        val result = ringAlarmUseCase.invoke(alarm) // Passing the alarm directly
        advanceUntilIdle()

        // Assert: Verify the error result
        assertTrue(result is Result.Error) // Ensure the result is an error
        assertEquals("Failed to update alarm state", (result as Result.Error).exception.message) // Verify the error message
    }

    /**
     * Test case for the failure scenario when cancelling the notification fails.
     * This test simulates the scenario where cancelling the notification fails.
     */
    @Test
    fun `invoke - error when cancelling notification`() = runTest {

        // Arrange: Setup mocks to simulate failure in canceling the notification
        coEvery { updateAlarmUseCase(any()) } returns Result.Success(Unit)
        every { sharedPrefsHelper.lastActiveAlarmNotificationPref } returns alarm.id
        every { alarmNotificationManager.cancelAlarmNotification(any()) } throws Exception("Failed to cancel notification")

        // Act: Execute the method under test
        val result = ringAlarmUseCase.invoke(alarm) // Passing the alarm directly
        advanceUntilIdle()

        // Assert: Verify the error result
        assertTrue(result is Result.Error) // Ensure the result is an error
        assertEquals("Failed to cancel notification", (result as Result.Error).exception.message) // Verify the error message
    }

    /**
     * Test case for the failure scenario when playing the alarm sound fails.
     * This test simulates the failure in playing the alarm sound.
     */
    @Test
    fun `invoke - error when playing alarm sound`() = runTest {

        // Arrange: Setup mocks to simulate failure in playing the alarm sound
        coEvery { updateAlarmUseCase(any()) } returns Result.Success(Unit)
        every { sharedPrefsHelper.lastActiveAlarmNotificationPref } returns alarm.id
        every { alarmRingtoneManager.playAlarmRingtone(any(),any()) } throws Exception("Failed to play alarm sound")

        // Act: Execute the method under test
        val result = ringAlarmUseCase.invoke(alarm) // Passing the alarm directly
        advanceUntilIdle()

        // Assert: Verify the error result
        assertTrue(result is Result.Error) // Ensure the result is an error
        assertEquals("Failed to play alarm sound", (result as Result.Error).exception.message) // Verify the error message
    }
}

