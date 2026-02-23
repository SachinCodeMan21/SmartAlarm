package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import org.junit.Assert.*
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import io.mockk.impl.annotations.MockK
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings
import com.example.smartalarm.feature.alarm.framework.notification.model.AlarmNotificationModel
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import java.time.LocalTime

/**
 * Unit tests for the [MissedAlarmUseCaseImpl] class. This test suite validates the behavior of the `invoke` method
 * of the [MissedAlarmUseCaseImpl], which is responsible for handling missed alarms. It tests various scenarios
 * including the correct handling of single-shot and repeating alarms when they are missed, as well as error handling
 * when updating the alarm state or posting notifications fails.
 *
 * The tests cover the following scenarios:
 * - **Single-shot alarms**: Ensures that missed single-shot alarms are marked as `MISSED`, disabled, snooze settings are reset,
 *   and a notification is posted.
 * - **Repeating alarms**: Ensures that missed repeating alarms are marked as `MISSED`, remain enabled, and that the next occurrence is rescheduled.
 * - **Update failures**: Tests for scenarios where the alarm state cannot be updated due to a failure in the `updateAlarmUseCase`.
 *   In these cases, no scheduling or notifications should occur, and an error result is returned.
 * - **Side effect failures**: Verifies the resilience of the system when operations like canceling the alarm timeout or posting notifications fail,
 *   but the database update succeeds. The system should still return a success result as long as the database update is successful.
 * - **Alarm not found**: Simulates cases where the alarm cannot be updated, ensuring that no further actions (like scheduling or notifications) are performed.
 *
 * Dependencies mocked in these tests:
 * - [UpdateAlarmUseCase]: Used to update the alarm's state in the database.
 * - [AlarmScheduler]: Used to manage alarm timeouts and reschedule alarms.
 * - [AlarmNotificationManager]: Used to cancel and post alarm notifications.
 * - [AlarmTimeHelper]: Used to calculate the next alarm trigger time for repeating alarms.
 *
 * The tests use the **MockK** framework for mocking dependencies and **JUnit** for the test lifecycle. The coroutines are tested using `runTest`
 * to execute suspending functions and `advanceUntilIdle` to ensure all coroutines finish execution. Verifications are done using `coVerify`
 * and `verify` to ensure that the expected actions are performed during the execution of the `invoke` method.
 */
@ExperimentalCoroutinesApi
class MissedAlarmUseCaseImplTest {
/*

    @MockK
    private lateinit var updateAlarmUseCase: UpdateAlarmUseCase

    @MockK
    private lateinit var alarmScheduler: AlarmScheduler

    @MockK
    private lateinit var alarmNotificationManager: AlarmNotificationManager

    @MockK
    private lateinit var alarmTimeHelper: AlarmTimeHelper

    @InjectMockKs
    private lateinit var missedAlarmUseCase: MissedAlarmUseCaseImpl

    private val singleShotAlarm = AlarmModel(
        id = 7,
        label = "Morning Meeting",
        time = LocalTime.of(8, 30),
        days = emptySet(), // Not repeating
        isEnabled = true,
        alarmState = AlarmState.RINGING,
        snoozeSettings = SnoozeSettings(
            isSnoozeEnabled = true,
            snoozeLimit = 3,
            snoozedCount = 2,
            snoozeIntervalMinutes = 10
        )
    )

    private val repeatingAlarm = singleShotAlarm.copy(
        days = setOf(DayOfWeek.MON, DayOfWeek.WED, DayOfWeek.FRI)
    )

    private val expectedMissedSingleShot = singleShotAlarm.copy(
        alarmState = AlarmState.MISSED,
        isEnabled = false, // Single-shot → disabled after missed
        snoozeSettings = singleShotAlarm.snoozeSettings.copy(
            isAlarmSnoozed = false,
            snoozedCount = singleShotAlarm.snoozeSettings.snoozeLimit
        )
    )

    private val expectedMissedRepeating = repeatingAlarm.copy(
        alarmState = AlarmState.MISSED,
        isEnabled = true, // Repeating → stays enabled
        snoozeSettings = repeatingAlarm.snoozeSettings.copy(
            isAlarmSnoozed = false,
            snoozedCount = 3
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    */
/**
     * Verifies that a single-shot alarm is correctly marked as MISSED, disabled, snooze settings are reset,
     * and a notification is posted. It ensures the expected state changes and side effects occur when the alarm is missed.
     *//*

    @Test
    fun `invoke - success - single-shot alarm - marks as MISSED, disables, resets snooze, posts notification`() =
        runTest {
            // Arrange
            coEvery { updateAlarmUseCase(expectedMissedSingleShot) } returns Result.Success(Unit)
            every { alarmScheduler.cancelSmartAlarmTimeout(any()) } just Runs
            every { alarmNotificationManager.postAlarmNotification(any(), any()) } just Runs

            // Act
            val result = missedAlarmUseCase.invoke(singleShotAlarm)
            advanceUntilIdle()

            // Assert
            assertTrue(result is Result.Success)
            coVerify(exactly = 1) { updateAlarmUseCase(expectedMissedSingleShot) }
            verify(exactly = 1) { alarmScheduler.cancelSmartAlarmTimeout(singleShotAlarm.id) }
            verify(exactly = 1) {
                alarmNotificationManager.postAlarmNotification(
                    singleShotAlarm.id,
                    AlarmNotificationModel.MissedAlarmModel(expectedMissedSingleShot)
                )
            }
            verify(exactly = 0) { alarmScheduler.scheduleSmartAlarm(any(), any()) }
        }

    */
/**
     * Verifies that a repeating alarm is correctly marked as MISSED, remains enabled, and the next occurrence is rescheduled.
     *//*

    @Test
    fun `invoke - success - repeating alarm - marks as MISSED, keeps enabled, reschedules next occurrence`() =
        runTest {
            // Arrange
            val nextTriggerTime = 1739500800000L // some future timestamp
            coEvery { updateAlarmUseCase(expectedMissedRepeating) } returns Result.Success(Unit)
            every { alarmScheduler.cancelSmartAlarmTimeout(any()) } just Runs
            every { alarmTimeHelper.calculateNextAlarmTriggerMillis(any(), any()) } returns nextTriggerTime
            every { alarmScheduler.scheduleSmartAlarm(any(), any()) } just Runs
            every { alarmNotificationManager.postAlarmNotification(any(), any()) } just Runs

            // Act
            val result = missedAlarmUseCase.invoke(repeatingAlarm)
            advanceUntilIdle()

            // Assert
            assertTrue(result is Result.Success)
            coVerify(exactly = 1) { updateAlarmUseCase(expectedMissedRepeating) }
            verify(exactly = 1) { alarmScheduler.cancelSmartAlarmTimeout(repeatingAlarm.id) }
            verify(exactly = 1) { alarmTimeHelper.calculateNextAlarmTriggerMillis(any(), any()) }
            verify(exactly = 1) { alarmScheduler.scheduleSmartAlarm(repeatingAlarm.id, nextTriggerTime) }
            verify(exactly = 1) {
                alarmNotificationManager.postAlarmNotification(
                    repeatingAlarm.id,
                    AlarmNotificationModel.MissedAlarmModel(expectedMissedRepeating)
                )
            }
        }

    */
/**
     * Verifies that when updating the alarm fails (e.g., due to a database error), the use case should:
     * - Return an error result.
     * - Not attempt to schedule the alarm again.
     * - Not post any notifications.
     *//*

    @Test
    fun `invoke - failure - update fails - returns error and does NOT schedule or notify`() =
        runTest {
            // Arrange
            coEvery { updateAlarmUseCase(any()) } returns Result.Error(RuntimeException("DB write failed"))

            // Act
            val result = missedAlarmUseCase.invoke(singleShotAlarm)

            // Assert
            assertTrue(result is Result.Error)
            coVerify(exactly = 1) { updateAlarmUseCase(expectedMissedSingleShot) }
            verify(exactly = 0) { alarmScheduler.scheduleSmartAlarm(any(), any()) }
            verify(exactly = 0) { alarmNotificationManager.postAlarmNotification(any(), any()) }
        }

    */
/**
     * Verifies the resilience of the system when side effects fail but the database update is successful.
     * This test ensures that the system still returns a success result as long as the database state update succeeds,
     * even if other operations (like scheduling or notifications) fail.
     *//*

    @Test
    fun `invoke - resilience - side effects fail but DB update succeeds - still returns error`() =
        runTest {
            // Arrange
            coEvery { updateAlarmUseCase(expectedMissedSingleShot) } returns Result.Success(Unit)
            every { alarmScheduler.cancelSmartAlarmTimeout(any()) } throws Exception("Scheduler dead")
            every { alarmNotificationManager.postAlarmNotification(any(), any()) } throws Exception("No context")

            // Act
            val result = missedAlarmUseCase.invoke(singleShotAlarm)
            advanceUntilIdle()

            // Assert: Still success because DB state is correct!
            assertTrue(result is Result.Error)

            coVerify(exactly = 1) { updateAlarmUseCase(expectedMissedSingleShot) }
            verify(atLeast = 1) { alarmScheduler.cancelSmartAlarmTimeout(singleShotAlarm.id) }
            verify(atLeast = 0) { alarmNotificationManager.postAlarmNotification(any(), any()) }
        }

    */
/**
     * Verifies that when the alarm cannot be found, the use case returns an error and no updates or notifications are performed.
     *//*

    @Test
    fun `invoke - failure - alarm not found - returns error`() = runTest {
        // Arrange: We're not passing a non-existent alarm because we directly provide the alarm model now.
        // The tests for failure cases now rely on the `updateAlarmUseCase` returning an error.

        // Act
        val result = missedAlarmUseCase.invoke(singleShotAlarm)

        // Assert: Check if result is an error due to update failure.
        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { updateAlarmUseCase(expectedMissedSingleShot) }
        verify(exactly = 0) { alarmScheduler.cancelSmartAlarmTimeout(any()) }
        verify(exactly = 0) { alarmNotificationManager.postAlarmNotification(any(), any()) }
    }
*/

}
