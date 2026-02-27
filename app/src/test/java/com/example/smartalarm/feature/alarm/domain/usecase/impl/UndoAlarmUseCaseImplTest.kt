package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.usecase.contract.SaveAlarmUseCase
import com.example.smartalarm.feature.alarm.framework.notification.manager.AlarmNotificationManager
import com.example.smartalarm.feature.alarm.framework.scheduler.contract.AlarmScheduler
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Unit tests for the [UndoAlarmUseCaseImpl] class, which restores a deleted alarm by saving it back to the system,
 * scheduling it to trigger at the correct time, and posting a notification for the restored alarm.
 *
 * These tests validate the behavior of the `invoke` function, which handles the restoration of the alarm and
 * manages the scheduling and notification posting process.
 *
 * The following scenarios are covered:
 * 1. **Success**: The alarm is successfully saved, scheduled, and a notification is posted.
 * 2. **Failure (Save Failed)**: If saving the alarm fails, an error is returned, and no scheduling or notification occurs.
 * 3. **Failure (Scheduling Failed)**: If scheduling the alarm fails, an error is returned, and no notification is posted.
 *
 * Dependencies are mocked using MockK, including:
 * - [SaveAlarmUseCase]: Responsible for saving the alarm data back into the system.
 * - [AlarmScheduler]: Schedules the alarm to trigger at the appropriate time.
 * - [AlarmNotificationManager]: Posts notifications related to the alarm.
 * - [AlarmTimeHelper]: Calculates the next alarm trigger time based on the alarm's time and repeat settings.
 * - [ResourceProvider]: Provides localized strings for error messages and UI texts.
 *
 * @constructor Creates an instance of [UndoAlarmUseCaseImplTest] with the required mocked dependencies.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UndoAlarmUseCaseImplTest {

/*    @MockK
    private lateinit var saveAlarmUseCase: SaveAlarmUseCase

    @MockK
    private lateinit var alarmSchedular: AlarmScheduler

    @MockK
    private lateinit var alarmNotificationManager: AlarmNotificationManager

    @MockK
    private lateinit var alarmTimeHelper: AlarmTimeHelper

    @MockK
    private lateinit var resourceProvider: ResourceProvider

    @InjectMockKs
    private lateinit var undoAlarmUseCase: UndoAlarmUseCaseImpl

    private val alarmModel = AlarmModel(
        id = 0,
        label = "Morning Alarm",
        time = LocalTime.of(7, 0),
        days = setOf(DayOfWeek.MON),
        isEnabled = true,
        alarmState = AlarmState.UPCOMING,
        snoozeSettings = SnoozeSettings()
    )

    *//**
     * Initializes the mocks and sets up the test environment.
     *//*
    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
    }

    *//**
     * Cleans up mocks after each test to ensure that no interactions are left from previous tests.
     *//*
    @After
    fun tearDown() {
        unmockkAll()
    }

    *//**
     * Tests the successful restoration of an alarm, including saving the alarm, scheduling it, and posting a notification.
     *
     * Verifies that the alarm is saved successfully, scheduled for the correct time, and a notification is posted.
     * Also checks the return value is the correctly formatted time for the alarm.
     *//*
    @Test
    fun `invoke - success - restores alarm, schedules and posts notification`() = runTest {
        // Arrange
        val savedAlarmId = 1
        val nextTriggerTime = 1234567890L
        val newAlarmModel = alarmModel.copy(id = savedAlarmId)
        coEvery { saveAlarmUseCase(alarmModel) } returns Result.Success(savedAlarmId)
        every { alarmTimeHelper.calculateNextAlarmTriggerMillis(newAlarmModel.time, newAlarmModel.days) } returns nextTriggerTime
        every { alarmSchedular.scheduleSmartAlarm(any(), any()) } just Runs
        every { alarmNotificationManager.postAlarmNotification(any(), any()) } just Runs
        every { alarmTimeHelper.getFormattedTimeUntilNextAlarm(any()) } returns "7:00 AM"

        // Act
        val result = undoAlarmUseCase.invoke(alarmModel)

        // Assert
        assertTrue(result is Result.Success)
        assertEquals("7:00 AM", (result as Result.Success).data)
        coVerify { saveAlarmUseCase(alarmModel) }
        verify { alarmSchedular.scheduleSmartAlarm(newAlarmModel.id, nextTriggerTime) }
        verify { alarmNotificationManager.postAlarmNotification(newAlarmModel.id, AlarmNotificationModel.UpcomingAlarmModel(newAlarmModel, nextTriggerTime)) }
    }

    *//**
     * Tests the failure scenario when saving the alarm fails.
     *
     * Verifies that an error is returned and no scheduling or notification occurs when saving the alarm fails.
     *//*
    @Test
    fun `invoke - failure - save alarm fails - returns error`() = runTest {
        // Arrange
        coEvery { saveAlarmUseCase(alarmModel) } returns Result.Error(Exception("Save failed"))

        // Act
        val result = undoAlarmUseCase.invoke(alarmModel)

        // Assert
        assertTrue(result is Result.Error)
        coVerify { saveAlarmUseCase(alarmModel) }
        verify(exactly = 0) { alarmSchedular.scheduleSmartAlarm(any(), any()) }
        verify(exactly = 0) { alarmNotificationManager.postAlarmNotification(any(), any()) }
    }

    *//**
     * Tests the failure scenario when scheduling the alarm fails.
     *
     * Verifies that an error is returned and no notification is posted when scheduling the alarm fails.
     *//*
    @Test
    fun `invoke - failure - schedule alarm fails - returns error`() = runTest {
        // Arrange
        val savedAlarmId = 1
        val newAlarmModel = alarmModel.copy(id = savedAlarmId)
        coEvery { saveAlarmUseCase(alarmModel) } returns Result.Success(savedAlarmId)
        val nextTriggerTime = 1234567890L
        every { alarmTimeHelper.calculateNextAlarmTriggerMillis(newAlarmModel.time, newAlarmModel.days) } returns nextTriggerTime
        every { alarmSchedular.scheduleSmartAlarm(any(), any()) } throws Exception("Scheduling failed")

        // Act
        val result = undoAlarmUseCase.invoke(alarmModel)

        // Assert
        assertTrue(result is Result.Error)
        coVerify { saveAlarmUseCase(alarmModel) }
        verify { alarmSchedular.scheduleSmartAlarm(newAlarmModel.id, nextTriggerTime) }
        verify(exactly = 0) { alarmNotificationManager.postAlarmNotification(any(), any()) }
    }*/
}
