package com.example.smartalarm.integration.alarm

import com.example.smartalarm.feature.alarm.domain.enums.AlarmState
import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings
import com.example.smartalarm.feature.alarm.domain.repository.AlarmRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import com.example.smartalarm.core.model.Result
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime
import javax.inject.Inject

@HiltAndroidTest
class AlarmRepositoryIT {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var alarmRepository: AlarmRepository


    // Dummy data for tests
    private val testAlarmModel = AlarmModel(
        label = "Test Alarm",
        time = LocalTime.of(8, 0),
        isDailyAlarm = true,
        days = setOf(DayOfWeek.MON, DayOfWeek.WED),
        volume = 50,
        isVibrateEnabled = true,
        alarmSound = "default",
        snoozeSettings = SnoozeSettings(),
        isEnabled = true,
        alarmState = AlarmState.UPCOMING
    )


    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun getAlarms_whenAlarmsExist_returnsAllDomainMappedAlarmsInList() = runTest {

        // Arrange: Insert an alarm via the repository
        val result = alarmRepository.saveAlarm(testAlarmModel)
        val alarmId = (result as Result.Success).data

        // Act: Get all alarms from the repository
        val alarms = alarmRepository.getAlarms().first()

        // Assert: Ensure that the list contains the alarm in the domain model
        assertTrue(alarms.isNotEmpty())
        assertTrue(alarms.contains(testAlarmModel.copy(alarmId)))
    }

    @Test
    fun getAlarms_whenNoAlarmsExist_returnsEmptyList() = runTest {
        // Act: Get all alarms from the repository when no alarms have been inserted
        val alarms = alarmRepository.getAlarms().first()

        // Assert: The list should be empty
        assertTrue(alarms.isEmpty())
    }

    @Test
    fun saveAlarm_whenNewAlarmInserted_returnsAlarmId() = runTest {
        // Act: Save a new alarm
        val result = alarmRepository.saveAlarm(testAlarmModel)

        // Assert: Ensure the result is successful and contains a valid alarm ID
        assertTrue(result is Result.Success)
        assertNotNull((result as Result.Success).data)
    }

    @Test
    fun saveAlarm_whenExistingAlarmInserted_returnsError() = runTest {

        // Arrange: Save a new alarm
        val alarmId = (alarmRepository.saveAlarm(testAlarmModel) as Result.Success).data

        // Act
        val result = alarmRepository.saveAlarm(testAlarmModel.copy(id=alarmId, label = "Hello"))

        //Assert
        assertTrue(result is Result.Error)
        assertEquals("Alarm ID must be 0 for new alarms", (result as Result.Error).exception.message)
    }

    @Test
    fun updateAlarm_whenAlarmExists_updatesSuccessfully() = runTest {

        // Arrange: Insert an alarm and get its ID
        val alarmId = (alarmRepository.saveAlarm(testAlarmModel) as Result.Success).data
        val updatedAlarmModel = testAlarmModel.copy(id = alarmId, label = "Updated Alarm")

        // Act: Update the alarm via the repository
        val result = alarmRepository.updateAlarm(updatedAlarmModel)

        // Assert: Ensure that the result is successful
        assertTrue(result is Result.Success)

        // Assert: Ensure the alarm is updated correctly
        val updatedAlarm = (alarmRepository.getAlarmById(alarmId) as Result.Success).data
        assertNotNull(updatedAlarm)
        assertEquals(updatedAlarmModel, updatedAlarm)
    }

    @Test
    fun updateAlarm_withNewAlarmIdZero_throwsError() = runTest {
        // Arrange: Prepare a non-existent alarm model
        val nonExistentAlarm = testAlarmModel // An ID that doesn't exist

        // Act: Attempt to update a non-existent alarm
        val result = alarmRepository.updateAlarm(nonExistentAlarm)

        // Assert: Ensure the result is an error
        assertTrue(result is Result.Error)
        assertEquals("Cannot update alarm with ID = 0", (result as Result.Error).exception.message)
    }

    @Test
    fun deleteAlarm_whenAlarmExists_deletesSuccessfully() = runTest {
        // Arrange: Insert an alarm and get its ID
        val alarmId = (alarmRepository.saveAlarm(testAlarmModel) as Result.Success).data

        // Act: Delete the alarm via the repository
        val result = alarmRepository.deleteAlarmById(alarmId)

        // Assert: Ensure that the delete operation was successful
        assertTrue(result is Result.Success)
    }

    @Test
    fun deleteAlarm_whenAlarmDoesNotExist_returnsSuccessAndDoesNothing() = runTest {
        // Act: Try deleting a non-existing alarm by ID
        val nonExistentAlarmId = 9999
        val result = alarmRepository.deleteAlarmById(nonExistentAlarmId)

        // Assert: Ensure that the delete operation returns an error
        assertTrue(result is Result.Success)
    }

}

