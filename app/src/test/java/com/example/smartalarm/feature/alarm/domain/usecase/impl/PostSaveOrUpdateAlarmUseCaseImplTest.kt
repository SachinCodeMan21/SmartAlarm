package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import java.time.LocalTime
import kotlin.test.Test
import com.example.smartalarm.core.model.Result
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.verify

/**
 * Unit tests for the [PostSaveOrUpdateAlarmUseCaseImpl] class.
 *
 * This test suite ensures that the [PostSaveOrUpdateAlarmUseCaseImpl] class behaves correctly by testing:
 * - Successful scheduling and notification posting when the alarm is enabled.
 * - Handling of the scenario where the alarm is disabled (no scheduling or notifications).
 * - Handling of errors when an exception occurs during the process.
 *
 * It uses [MockK] to mock the dependencies of the [PostSaveOrUpdateAlarmUseCaseImpl], including:
 * - [AlarmScheduler]: Responsible for scheduling the alarm.
 * - [AlarmNotificationManager]: Responsible for posting notifications.
 * - [AlarmTimeHelper]: Provides utility methods to calculate time until the alarm triggers.
 *
 * **Test cases**:
 * - `should schedule alarm and post notification when alarm is enabled`: Tests the successful case when the alarm is enabled.
 * - `should return empty string when alarm is disabled`: Tests the case when the alarm is disabled.
 * - `should return error when an exception occurs during scheduling or notification posting`: Tests the case where an error occurs during scheduling or notification posting.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PostSaveOrUpdateAlarmUseCaseImplTest {

    // Mocking the required dependencies using annotations
    @MockK
    private lateinit var mockAlarmScheduler: AlarmScheduler

    @MockK
    private lateinit var mockAlarmNotificationManager: AlarmNotificationManager

    @MockK
    private lateinit var mockAlarmTimeHelper: AlarmTimeHelper

    @InjectMockKs
    private lateinit var postSaveOrUpdateAlarmUseCaseImpl: PostSaveOrUpdateAlarmUseCaseImpl

    private val alarm = AlarmModel(
        id = 1,
        time = LocalTime.of(8, 0),
        days = setOf(DayOfWeek.MON),
        isEnabled = true // Can be toggled for the test
    )
    private val formattedTimeUntilAlarm = "Next alarm in 3 hours"

    @Before
    fun setUp() {
        // Initialize MockK
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        // Verify all interactions were verified
        clearAllMocks()
    }

    /**
     * Tests the successful scheduling and notification posting when the alarm is enabled.
     * Verifies the interactions with [AlarmScheduler], [AlarmNotificationManager], and [AlarmTimeHelper].
     */
    @Test
    fun `should schedule alarm and post notification when alarm is enabled`() = runTest {
        // Arrange
        val remainingTimeMillis = 3600000L // 1 hour
        every { mockAlarmTimeHelper.calculateNextAlarmTriggerMillis(alarm.time, alarm.days) } returns remainingTimeMillis
        every { mockAlarmScheduler.scheduleSmartAlarm(alarm.id, remainingTimeMillis) } just Runs
        every { mockAlarmNotificationManager.postAlarmNotification(any(), any()) } returns Unit
        every { mockAlarmTimeHelper.getFormattedTimeUntilNextAlarm(remainingTimeMillis) } returns formattedTimeUntilAlarm

        // Act
        val result = postSaveOrUpdateAlarmUseCaseImpl.invoke(alarm)

        // Assert
        assertTrue(result is Result.Success)
        assertEquals(formattedTimeUntilAlarm, (result as Result.Success).data)
        verify { mockAlarmScheduler.scheduleSmartAlarm(alarm.id, remainingTimeMillis) }
        verify { mockAlarmNotificationManager.postAlarmNotification(any(), any()) }
        verify { mockAlarmTimeHelper.getFormattedTimeUntilNextAlarm(remainingTimeMillis) }
    }

    /**
     * Tests the scenario where the alarm is disabled.
     * Verifies that no scheduling or notification occurs, and an empty string is returned.
     */
    @Test
    fun `should return empty string when alarm is disabled`() = runTest {

        // Arrange
        val disabledAlarm = alarm.copy(isEnabled = false)

        // Act
        val result = postSaveOrUpdateAlarmUseCaseImpl.invoke(disabledAlarm)

        // Assert
        assertTrue(result is Result.Success)
        assertEquals("", (result as Result.Success).data)
        verify(exactly = 0) { mockAlarmScheduler.scheduleSmartAlarm(any(), any()) }
        verify(exactly = 0) { mockAlarmNotificationManager.postAlarmNotification(any(), any()) }
    }

    /**
     * Tests the scenario where an exception occurs during scheduling or notification posting.
     * Verifies that an error is returned.
     */
    @Test
    fun `should return error when an exception occurs during scheduling or notification posting`() = runTest {
        // Arrange
        val exceptionMessage = "Error occurred while scheduling or posting notification"
        every { mockAlarmTimeHelper.calculateNextAlarmTriggerMillis(alarm.time, alarm.days) } throws Exception(exceptionMessage)

        // Act
        val result = postSaveOrUpdateAlarmUseCaseImpl.invoke(alarm)

        // Assert
        assertTrue(result is Result.Error)
        assertEquals(exceptionMessage, (result as Result.Error).exception.message)
        verify(exactly = 0) { mockAlarmScheduler.scheduleSmartAlarm(any(), any()) }
        verify(exactly = 0) { mockAlarmNotificationManager.postAlarmNotification(any(), any()) }
    }
}
