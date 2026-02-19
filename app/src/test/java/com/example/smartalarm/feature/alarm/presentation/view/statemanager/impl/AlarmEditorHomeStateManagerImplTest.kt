package com.example.smartalarm.feature.alarm.presentation.view.statemanager.impl

import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.domain.model.MissionType
import com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings
import com.example.smartalarm.feature.alarm.framework.manager.contract.AlarmRingtoneManager
import com.example.smartalarm.feature.alarm.utility.helper.contract.AlarmTimeHelper
import com.example.smartalarm.feature.alarm.presentation.view.statemanager.contract.AlarmEditorHomeStateManager
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for [AlarmEditorHomeStateManagerImpl].
 *
 * Verifies that the alarm state manager correctly initializes and updates
 * [AlarmModel] state through various operations such as setting the alarm time,
 * label, ringtone, volume, vibration, daily mode, and missions.
 */
class AlarmEditorHomeStateManagerImplTest {

    @MockK
    private lateinit var alarmRingtoneHelper: AlarmRingtoneManager

    @MockK
    private lateinit var alarmTimeHelper: AlarmTimeHelper

    @InjectMockKs
    private lateinit var alarmStateManager: AlarmEditorHomeStateManagerImpl

    /**
     * Sets up mocks and initializes [AlarmEditorHomeStateManagerImpl] before each test.
     */
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        alarmStateManager = AlarmEditorHomeStateManagerImpl(alarmRingtoneHelper, alarmTimeHelper)
    }

    /**
     * Clears all mocks after each test.
     */
    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Test that [AlarmEditorHomeStateManagerImpl.initAlarmState] sets the default alarm values.
     */
    @Test
    fun `initAlarmState sets default values`() = runTest {

        // Arrange
        val alarmSoundString = "content://media/internal/audio/media/1"
        every { alarmRingtoneHelper.getDefaultRingtoneUri().toString() } returns alarmSoundString

        // Act
        alarmStateManager.initAlarmState()

        // Assert
        val alarmState = alarmStateManager.getAlarmState.first()
        val now = LocalTime.now().withSecond(0).withNano(0)
        assertEquals(now.hour, alarmState.time.hour)
        assertEquals(now.minute, alarmState.time.minute)
        assertEquals(alarmSoundString, alarmState.alarmSound)
        assertTrue(alarmState.days.isEmpty())
    }

    /**
     * Test that [AlarmEditorHomeStateManagerImpl.setAlarm] correctly sets a provided [AlarmModel].
     */
    @Test
    fun `setAlarm sets the alarm state`() = runTest {
        // Arrange
        val alarm = AlarmModel(
            time = LocalTime.of(7, 30),
            alarmSound = "content://media/internal/audio/media/1",
            days = setOf(DayOfWeek.MON, DayOfWeek.WED)
        )

        // Act
        alarmStateManager.setAlarm(alarm)

        // Assert
        val state = alarmStateManager.getAlarmState.first()
        assertEquals(alarm, state)
    }

    /**
     * Test that [AlarmEditorHomeStateManagerImpl.updateLabel] updates the alarm label.
     */
    @Test
    fun `updateLabel changes label`() = runTest {
        // Arrange
        val label = "Wake Up"

        // Act
        alarmStateManager.updateLabel(label)

        // Assert
        val state = alarmStateManager.getAlarmState.first()
        assertEquals(label, state.label)
    }

    /**
     * Test that [AlarmEditorHomeStateManager.updateTime] calls [AlarmTimeHelper] and updates the time.
     */
    @Test
    fun `updateTime uses alarmTimeHelper`() = runTest {
        // Arrange
        every { alarmTimeHelper.convertTo24HourTime(7, 15, 0) } returns LocalTime.of(7, 15)

        // Act
        alarmStateManager.updateTime(7, 15, 0)

        // Assert
        val state = alarmStateManager.getAlarmState.first()
        assertEquals(LocalTime.of(7, 15), state.time)
    }

    /**
     * Test that [AlarmEditorHomeStateManagerImpl.updateIsDaily] sets daily mode and restores previous days correctly.
     */
    @Test
    fun `updateIsDaily sets daily alarm and restores previous days`() = runTest {
        // Arrange
        val initialDays = setOf(DayOfWeek.MON, DayOfWeek.FRI)
        alarmStateManager.setAlarm(AlarmModel(days = initialDays))

        // Act
        alarmStateManager.updateIsDaily(true)
        var state = alarmStateManager.getAlarmState.first()

        // Assert
        assertTrue(state.isDailyAlarm)
        assertEquals(DayOfWeek.entries.toSet(), state.days)

        // Act
        alarmStateManager.updateIsDaily(false)
        state = alarmStateManager.getAlarmState.first()

        // Assert
        assertFalse(state.isDailyAlarm)
        assertEquals(initialDays, state.days)
    }

    /**
     * Test that [AlarmEditorHomeStateManagerImpl.toggleDay] adds and removes days and sets the daily flag.
     */
    @Test
    fun `toggleDay adds and removes day and sets daily flag`() = runTest {
        // Arrange
        val monday = DayOfWeek.MON.ordinal

        // Act
        alarmStateManager.toggleDay(monday)
        var state = alarmStateManager.getAlarmState.first()

        // Assert
        assertTrue(state.days.contains(DayOfWeek.MON))
        assertFalse(state.isDailyAlarm)

        // Act
        alarmStateManager.toggleDay(monday)
        state = alarmStateManager.getAlarmState.first()

        // Assert
        assertFalse(state.days.contains(DayOfWeek.MON))
    }

    /**
     * Test that [AlarmEditorHomeStateManagerImpl.updateVolume] updates the alarm volume.
     */
    @Test
    fun `updateVolume sets volume`() = runTest {
        // Act
        alarmStateManager.updateVolume(50)

        // Assert
        val state = alarmStateManager.getAlarmState.first()
        assertEquals(50, state.volume)
    }

    /**
     * Test that [AlarmEditorHomeStateManagerImpl.updateVibration] toggles vibration.
     */
    @Test
    fun `updateVibration sets vibration`() = runTest {
        // Act
        alarmStateManager.updateVibration(true)
        var state = alarmStateManager.getAlarmState.first()

        // Assert
        assertTrue(state.isVibrateEnabled)

        // Act
        alarmStateManager.updateVibration(false)
        state = alarmStateManager.getAlarmState.first()

        // Assert
        assertFalse(state.isVibrateEnabled)
    }

    /**
     * Test that [AlarmEditorHomeStateManagerImpl.updateRingtone] updates the alarm ringtone URI.
     */
    @Test
    fun `updateRingtone sets ringtone`() = runTest {
        // Arrange
        val uri = "new_ringtone_uri"

        // Act
        alarmStateManager.updateRingtone(uri)

        // Assert
        val state = alarmStateManager.getAlarmState.first()
        assertEquals(uri, state.alarmSound)
    }

    /**
     * Test that [AlarmEditorHomeStateManagerImpl.updateSnooze] sets the snooze configuration.
     */
    @Test
    fun `updateSnooze sets snoozeSettings`() = runTest {
        // Arrange
        val snooze = SnoozeSettings(isAlarmSnoozed = true, snoozedCount = 2, snoozeLimit = 3)

        // Act
        alarmStateManager.updateSnooze(snooze)

        // Assert
        val state = alarmStateManager.getAlarmState.first()
        assertEquals(snooze, state.snoozeSettings)
    }

    /**
     * Test that [AlarmEditorHomeStateManagerImpl.updateMission] adds a new mission or updates an existing one.
     */
    @Test
    fun `updateMission adds and updates missions`() = runTest {
        // Arrange
        val mission1 = Mission(type = MissionType.Memory, iconResId = 0)
        val mission2 = Mission(type = MissionType.Typing, iconResId = 1)

        // Act
        alarmStateManager.updateMission(0, mission1)
        var state = alarmStateManager.getAlarmState.first()

        // Assert
        assertEquals(listOf(mission1), state.missions)

        // Act
        alarmStateManager.updateMission(0, mission2)
        state = alarmStateManager.getAlarmState.first()

        // Assert
        assertEquals(listOf(mission2), state.missions)
    }

    /**
     * Test that [AlarmEditorHomeStateManagerImpl.removeMissionAt] removes a mission at a given index.
     */
    @Test
    fun `removeMissionAt removes mission`() = runTest {
        // Arrange
        val mission = Mission(MissionType.Memory, iconResId = 0)
        alarmStateManager.updateMission(0, mission)

        // Act
        alarmStateManager.removeMissionAt(0)

        // Assert
        val state = alarmStateManager.getAlarmState.first()
        assertTrue(state.missions.isEmpty())
    }
}
