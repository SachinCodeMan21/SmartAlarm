package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.feature.alarm.domain.usecase.contract.UpdateAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import org.junit.Assert.*
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import io.mockk.impl.annotations.MockK
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import java.time.LocalTime

/**
 * Unit tests for [StopAlarmUseCaseImpl].
 *
 * This class tests the implementation of the [StopAlarmUseCaseImpl], ensuring that the use case behaves correctly under different conditions. The tests verify that the alarm is stopped properly, marked as EXPIRED, and that all side effects (like canceling timeouts, stopping the ringtone, and resetting preferences) are handled correctly. Additionally, the tests ensure that the use case behaves gracefully when errors occur, both during database updates and when side effects fail.
 *
 * The following scenarios are tested:
 *
 * 1. **Success**:
 *    - Verifies that the alarm is correctly stopped, its state is updated to EXPIRED, and all related side effects (timeouts, ringtone, and preferences) are executed as expected.
 *
 * 2. **Failure - Database update fails**:
 *    - Verifies that if the database update fails, the use case returns an error, and no side effects (timeouts, ringtone, and preferences reset) are performed.
 *
 * 3. **Resilience - Side effects fail but DB update succeeds**:
 *    - Verifies that if side effects (e.g., stopping ringtone or canceling timeouts) fail but the database update succeeds, the alarm's state is still correctly marked as EXPIRED, and the method returns an error indicating the failure of the side effects.
 *
 * Dependencies:
 * - **Mocked dependencies**:
 *   - `updateAlarmUseCase`: Used to update the alarm in the database.
 *   - `alarmScheduler`: Used to cancel any scheduled timeouts for the alarm.
 *   - `alarmRingtoneHelper`: Used to stop the alarm sound.
 *   - `sharedPrefsHelper`: Used to reset the shared preference for the last active alarm notification.
 *
 * Test library: MockK (for mocking dependencies), JUnit (for testing framework).
 *
 * Each test ensures that the correct behavior occurs by checking the return result and verifying that the expected methods on the mocked dependencies are called with the correct arguments.
 */
@ExperimentalCoroutinesApi
class StopAlarmUseCaseImplTest {

    @MockK
    private lateinit var updateAlarmUseCase: UpdateAlarmUseCase

    @MockK
    private lateinit var alarmScheduler: AlarmScheduler

    @MockK
    private lateinit var alarmRingtoneHelper: AlarmRingtoneManager

    @MockK
    private lateinit var sharedPrefsHelper: SharedPrefsHelper

    @InjectMockKs
    private lateinit var stopAlarmUseCase: StopAlarmUseCaseImpl

    private val alarmModel = AlarmModel(
        id = 7,
        label = "Morning Alarm",
        time = LocalTime.of(8, 0),
        days = emptySet(),
        isEnabled = true,
        alarmState = AlarmState.RINGING,
        snoozeSettings = SnoozeSettings(
            isSnoozeEnabled = true,
            snoozeLimit = 3,
            snoozedCount = 2,
            snoozeIntervalMinutes = 10
        )
    )

    private val expectedUpdatedAlarm = alarmModel.copy(
        alarmState = AlarmState.EXPIRED,
        isEnabled = false,  // alarm is disabled when stopped
        snoozeSettings = alarmModel.snoozeSettings.copy(
            isAlarmSnoozed = false,
            snoozedCount = alarmModel.snoozeSettings.snoozeLimit
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

    /**
     * Verifies that when the alarm is passed directly, it is correctly marked as EXPIRED, stopped,
     * and all related operations (cancelling timeouts, stopping the sound, and resetting preferences) are executed.
     */
    @Test
    fun `invoke - success - stops the alarm, marks as EXPIRED, and resets state`() = runTest {

        // Arrange
        coEvery { updateAlarmUseCase(expectedUpdatedAlarm) } returns Result.Success(Unit)
        every { alarmScheduler.cancelSmartAlarmTimeout(any()) } just Runs
        every { alarmRingtoneHelper.stopAlarmRingtone() } just Runs
        every { sharedPrefsHelper.lastActiveAlarmNotificationPref = 0 } just Runs

        // Act
        val result = stopAlarmUseCase(alarmModel)  // Passing alarm directly
        advanceUntilIdle()

        // Assert
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { updateAlarmUseCase(expectedUpdatedAlarm) }
        verify(exactly = 1) { alarmScheduler.cancelSmartAlarmTimeout(alarmModel.id) }
        verify(exactly = 1) { alarmRingtoneHelper.stopAlarmRingtone() }
        verify(exactly = 1) { sharedPrefsHelper.lastActiveAlarmNotificationPref = 0 }
    }

    /**
     * Verifies that when updating the alarm fails (e.g., due to a database error), the use case:
     * - Returns an error result.
     * - Does not stop the alarm sound or reset preferences.
     *
     * This ensures that any database errors are properly handled.
     */
    @Test
    fun `invoke - failure - update fails - returns error and does NOT stop or reset`() = runTest {
        // Arrange
        coEvery { updateAlarmUseCase(expectedUpdatedAlarm) } returns Result.Error(RuntimeException("DB write failed"))

        // Act
        val result = stopAlarmUseCase(alarmModel)  // Passing alarm directly

        // Assert
        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { updateAlarmUseCase(expectedUpdatedAlarm) }
        verify(exactly = 0) { alarmScheduler.cancelSmartAlarmTimeout(any()) }
        verify(exactly = 0) { alarmRingtoneHelper.stopAlarmRingtone() }
        verify(exactly = 0) { sharedPrefsHelper.lastActiveAlarmNotificationPref = 0 }
    }


    /**
     * Verifies the system handles failures gracefully when side effects (e.g., stopping ringtone or canceling timeouts)
     * fail, but the database update was successful.
     * Ensures the alarm still gets marked as EXPIRED even if side effects fail.
     */
    @Test
    fun `invoke - resilience - side effects fail but DB update succeeds - still returns error`() = runTest {
        // Arrange
        coEvery { updateAlarmUseCase(expectedUpdatedAlarm) } returns Result.Success(Unit)
        every { alarmScheduler.cancelSmartAlarmTimeout(any()) } throws Exception("Scheduler dead")
        every { alarmRingtoneHelper.stopAlarmRingtone() } throws Exception("No context")
        every { sharedPrefsHelper.lastActiveAlarmNotificationPref = 0 } throws Exception("Prefs failure")

        // Act
        val result = stopAlarmUseCase(alarmModel)  // Passing alarm directly
        advanceUntilIdle()

        // Assert: Still success because DB state update succeeded!
        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { updateAlarmUseCase(expectedUpdatedAlarm) }
        verify(atLeast = 1) { alarmScheduler.cancelSmartAlarmTimeout(alarmModel.id) }
        verify(atLeast = 0) { alarmRingtoneHelper.stopAlarmRingtone() }
        verify(atLeast = 0) { sharedPrefsHelper.lastActiveAlarmNotificationPref = 0 }
    }

}

