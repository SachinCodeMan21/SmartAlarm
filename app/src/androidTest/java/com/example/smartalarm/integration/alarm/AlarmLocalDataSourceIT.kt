package com.example.smartalarm.integration.alarm

import com.example.smartalarm.R
import com.example.smartalarm.feature.alarm.data.datasource.contract.AlarmLocalDataSource
import com.example.smartalarm.feature.alarm.data.local.entity.AlarmEntity
import com.example.smartalarm.feature.alarm.data.local.entity.MissionEntity
import com.example.smartalarm.feature.alarm.data.local.relation.AlarmWithMissions
import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime
import javax.inject.Inject

@HiltAndroidTest
class AlarmLocalDataSourceIT {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dataSource: AlarmLocalDataSource


    // Dummy data for tests
    private val testAlarmEntity = AlarmEntity(
        label = "Test Alarm",
        time = LocalTime.of(8, 0),
        isDailyAlarm = true,
        days = setOf(DayOfWeek.MON, DayOfWeek.WED),
        volume = 50,
        isVibrateEnabled = true,
        alarmSound = "default",
        snoozeSettings = SnoozeSettings(),
        isEnabled = true,
        alarmState = "active"
    )

    private val testMissionsEntity = listOf(
        MissionEntity(
            alarmId = 0, // This will be set when the alarm is inserted
            type = "Task",
            difficulty = "Medium",
            rounds = 3,
            iconResId = R.drawable.ic_memory,
            isCompleted = false
        )
    )


    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun getAllAlarms_whenAlarmExists_returns_allAlarmsFromDB() = runTest {

        // Arrange
        val insertedId = dataSource.saveAlarmWithMissions(testAlarmEntity, testMissionsEntity)

        // Act
        val alarms = dataSource.getAllAlarms().first()

        // Assert
        val expectedAlarmWithMissions = AlarmWithMissions(testAlarmEntity.copy(id = insertedId), testMissionsEntity.map { it.copy(id = 1, alarmId = insertedId) })
        assertTrue(alarms.isNotEmpty())
        assert(alarms.contains(expectedAlarmWithMissions))
    }

    @Test
    fun getAllAlarms_whenAlarmDoesNotExists_returns_emptyList() = runTest {

        // Act
        val alarms = dataSource.getAllAlarms().first()

        // Assert
        val expectedAlarmWithMissions = emptyList<AlarmWithMissions>()
        assertTrue(alarms.isEmpty())
        assertEquals(expectedAlarmWithMissions, alarms)
    }

    @Test
    fun saveAlarmWithMissions_newAlarmInsertOnSuccess_saveAlarmWithMissionAndReturnAlarmId() = runTest {

        // Act
        val alarmId = dataSource.saveAlarmWithMissions(testAlarmEntity, testMissionsEntity)

        // Assert
        val savedAlarm = dataSource.getAlarmById(alarmId)
        assertNotNull(alarmId)
        assertEquals(testAlarmEntity.copy(id = alarmId), savedAlarm?.alarm)
        assertEquals(testMissionsEntity.map { it.copy(id = 1, alarmId = alarmId) }, savedAlarm?.missions)
    }

    @Test
    fun saveAlarmWithMissions_onExistingAlarm_throwIllegalArgumentException() = runTest {
        // Arrange: Insert the first alarm
        val alarmId = dataSource.saveAlarmWithMissions(testAlarmEntity, testMissionsEntity)

        // Try inserting the same alarm with a non-zero ID
        val newAlarm = testAlarmEntity.copy(id = alarmId, label = "Hello")  // Same ID as the existing alarm

        // Act & Assert: Ensure that inserting the alarm with an existing ID throws IllegalArgumentException
        try {
            dataSource.saveAlarmWithMissions(newAlarm, testMissionsEntity)
            fail("Expected IllegalArgumentException to be thrown")
        } catch (e: IllegalArgumentException) {
            assertEquals("Alarm ID must be 0 for new alarms", e.message)
        }

    }

    @Test
    fun updateAlarmWithMissions_onExistingAlarmUpdateSuccess_updateAlarmWithMission() = runTest {

        // Act
        val alarmId = dataSource.saveAlarmWithMissions(testAlarmEntity, testMissionsEntity)
        dataSource.updateAlarmWithMissions(testAlarmEntity.copy(id = alarmId, label = "Hello"), testMissionsEntity.map { it.copy(id = 1, alarmId = alarmId) })


        // Assert
        val savedAlarm = dataSource.getAlarmById(alarmId)
        assertEquals(testAlarmEntity.copy(id = alarmId, label = "Hello"), savedAlarm?.alarm)
        assertEquals(testMissionsEntity.map { it.copy(id = 1, alarmId = alarmId) }, savedAlarm?.missions)
    }

    @Test
    fun updateAlarmWithMissions_onInvalidAlarmId_throwIllegalArgumentException() = runTest {
        // Arrange: Insert the first alarm
        dataSource.saveAlarmWithMissions(testAlarmEntity, testMissionsEntity)

        // Try inserting the same alarm with a non-zero ID (simulating an invalid update with id = 0)
        val invalidAlarm = testAlarmEntity.copy(id = 0, label = "Updated Alarm")

        // Act & Assert: Ensure that updating with an ID of 0 throws IllegalArgumentException
        try {
            dataSource.updateAlarmWithMissions(invalidAlarm, testMissionsEntity)
            fail("Expected IllegalArgumentException to be thrown")
        } catch (e: IllegalArgumentException) {
            assertEquals("Cannot update alarm with ID = 0", e.message)
        }
    }

    @Test
    fun deleteAlarmById_alarmDoesNotExist_returnsNull() = runTest {
        // Try deleting an alarm that doesn't exist (non-existent alarm ID)
        val nonExistentAlarmId = 9999

        // Act: Delete the non-existing alarm by its ID
        dataSource.deleteAlarmById(nonExistentAlarmId)

        // Assert: Try to fetch the alarm again, it should still be null
        val alarm = dataSource.getAlarmById(nonExistentAlarmId)
        assertNull(alarm)  // No alarm should exist with this ID
    }

    @Test
    fun deleteAlarmById_alarmExists_alarmDeletedSuccessfully() = runTest {
        // Insert alarm and missions into the database
        val alarmId = dataSource.saveAlarmWithMissions(testAlarmEntity, testMissionsEntity)

        // Act: Delete the alarm by its ID
        dataSource.deleteAlarmById(alarmId)

        // Assert: Try to fetch the alarm again, it should be null as it was deleted
        val alarm = dataSource.getAlarmById(alarmId)
        assertNull(alarm)  // Alarm should be deleted and not found in the database
    }

}
