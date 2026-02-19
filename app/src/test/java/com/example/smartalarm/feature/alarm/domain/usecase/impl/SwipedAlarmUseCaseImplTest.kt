package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.usecase.contract.DeleteAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import kotlin.test.Test
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.R
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.framework.controller.contract.AlarmServiceController
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import io.mockk.coVerify
import io.mockk.verify

/**
 * Unit tests for the [SwipedAlarmUseCaseImpl] class, which handles the action of swiping an alarm
 * for deletion and canceling its associated scheduled actions and notifications.
 *
 * The tests in this class verify the correct behavior of the `SwipedAlarmUseCaseImpl` implementation
 * by simulating different scenarios, such as:
 * 1. A successful alarm swipe, where the alarm is deleted, and its scheduled actions and notifications
 *    are canceled.
 * 2. A failure in deleting the alarm, which ensures that no further actions are taken, and an error is returned.
 * 3. A failure in canceling the alarm's scheduled actions after deletion, where an error result is returned.
 * 4. A special case where the alarm is in the **RINGING** state, where the notification should not be canceled
 *    but the alarm is still deleted, and other actions like scheduling and stopping the alarm service should still occur.
 *
 * The tests make use of mocking frameworks such as [MockK] to simulate the behaviors of external dependencies
 * like `DeleteAlarmUseCase`, `AlarmScheduler`, `AlarmNotificationManager`, and `AlarmServiceController`.
 *
 * Test methods:
 * - `invoke - success - deletes and cancels alarm`: Verifies that the alarm is successfully deleted, and its
 *   scheduled actions and notification are canceled.
 * - `invoke - failure - delete fails - returns error`: Ensures that if alarm deletion fails, no further actions
 *   are taken, and an error result is returned.
 * - `invoke - failure - cancel alarm fails - returns error`: Verifies that if cancellation of the scheduled alarm
 *   fails after deletion, an error result is returned.
 * - `invoke - ringing alarm - notification not canceled`: Verifies that if the alarm is in the **RINGING** state,
 *   the notification is not canceled, but the alarm is still deleted, and other actions are taken.
 *
 * The tests rely on the use of [runTest] from Kotlin Coroutines for asynchronous operations.
 */
@ExperimentalCoroutinesApi
class SwipedAlarmUseCaseImplTest {

    @MockK
    private lateinit var deleteAlarmUseCase: DeleteAlarmUseCase

    @MockK
    private lateinit var alarmSchedular: AlarmScheduler

    @MockK
    private lateinit var alarmNotificationManager: AlarmNotificationManager

    @MockK
    private lateinit var alarmRingtoneManager: AlarmRingtoneManager

    @MockK
    private lateinit var alarmServiceController: AlarmServiceController

    @MockK
    private lateinit var resourceProvider: ResourceProvider

    @InjectMockKs
    private lateinit var swipedAlarmUseCase: SwipedAlarmUseCaseImpl

    private val alarmId = 1
    private val alarmState = AlarmState.UPCOMING

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Test the successful swiping of an alarm, which results in the alarm being deleted,
     * and associated actions (scheduling and notification) being canceled.
     */
    @Test
    fun `invoke - success - deletes and cancels alarm`() = runTest {
        // Arrange: Mocking successful deletion and successful cancellation of alarm
        coEvery { deleteAlarmUseCase(alarmId) } returns Result.Success(Unit)
        every { alarmSchedular.cancelAllScheduledAlarms(alarmId) } just Runs
        every { alarmNotificationManager.cancelAlarmNotification(alarmId) } just Runs

        // Act: Swiping the alarm
        val result = swipedAlarmUseCase.invoke(alarmId, alarmState)

        // Assert: Verifying the result is a success and cancellation of alarm and notification
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { deleteAlarmUseCase(alarmId) }
        verify(exactly = 1) { alarmSchedular.cancelAllScheduledAlarms(alarmId) }
        verify(exactly = 1) { alarmNotificationManager.cancelAlarmNotification(alarmId) }
    }

    /**
     * Test when deleting the alarm fails, it returns an error with a localized message.
     */
    @Test
    fun `invoke - failure - delete fails - returns error`() = runTest {
        // Arrange: Mocking failure in alarm deletion
        coEvery { deleteAlarmUseCase(alarmId) } returns Result.Error(Exception("Delete failed"))
        every { resourceProvider.getString(R.string.error_failed_to_delete_alarm) } returns "Failed to delete alarm"

        // Act: Swiping the alarm
        val result = swipedAlarmUseCase.invoke(alarmId, alarmState)

        // Assert: Verifying that an error is returned and the alarm is not canceled
        assertTrue(result is Result.Error)
        assertEquals("Failed to delete alarm", (result as Result.Error).exception.message)
        coVerify(exactly = 1) { deleteAlarmUseCase(alarmId) }
        verify(exactly = 0) { alarmSchedular.cancelAllScheduledAlarms(any()) }
        verify(exactly = 0) { alarmNotificationManager.cancelAlarmNotification(any()) }
    }

    /**
     * Test when canceling the alarm fails after deletion, it returns an error result.
     */
    @Test
    fun `invoke - failure - cancel alarm fails - returns error`() = runTest {
        // Arrange: Mocking successful alarm deletion but failure in cancellation
        coEvery { deleteAlarmUseCase(alarmId) } returns Result.Success(Unit)
        every { alarmSchedular.cancelAllScheduledAlarms(alarmId) } throws Exception("Cancel failed")
        every { alarmNotificationManager.cancelAlarmNotification(alarmId) } just Runs

        // Act: Swiping the alarm
        val result = swipedAlarmUseCase.invoke(alarmId, alarmState)

        // Assert: Verifying the result is an error due to cancellation failure
        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { deleteAlarmUseCase(alarmId) }
        verify(exactly = 1) { alarmSchedular.cancelAllScheduledAlarms(alarmId) }
        verify(exactly = 0) { alarmNotificationManager.cancelAlarmNotification(any()) }
    }

    /**
     * Test when the alarm is in RINGING state, the notification is not canceled, and the alarm service
     * and ringtone are stopped.
     */
    @Test
    fun `invoke - ringing alarm - notification not canceled and service stopped`() = runTest {
        // Arrange: Mocking successful deletion and actions for ringing alarm state
        val ringingAlarmState = AlarmState.RINGING
        coEvery { deleteAlarmUseCase(alarmId) } returns Result.Success(Unit)
        every { alarmSchedular.cancelAllScheduledAlarms(alarmId) } just Runs
        every { alarmRingtoneManager.stopAlarmRingtone() } just Runs
        every { alarmServiceController.stopAlarmService() } just Runs

        // Act: Swiping the alarm
        val result = swipedAlarmUseCase.invoke(alarmId, ringingAlarmState)

        // Assert: Verifying the result is a success and stopping the alarm service and ringtone
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { deleteAlarmUseCase(alarmId) }
        verify(exactly = 1) { alarmSchedular.cancelAllScheduledAlarms(alarmId) }
        verify(exactly = 0) { alarmNotificationManager.cancelAlarmNotification(any()) }
        verify(exactly = 1) { alarmRingtoneManager.stopAlarmRingtone() }
        verify(exactly = 1) { alarmServiceController.stopAlarmService() }
    }
}

