package com.example.smartalarm.feature.alarm.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class AlarmRepositoryImplTest {

/*
    @MockK
    private lateinit var alarmLocalDataSource: AlarmLocalDataSource

    private lateinit var alarmRepository: AlarmRepositoryImpl
    private lateinit var testDispatcher: TestDispatcher

    private val testAlarmEntity = AlarmEntity(
        id = 1, label = "Test Alarm", time = LocalTime.of(8, 0),
        isDailyAlarm = true, days = setOf(DayOfWeek.MON, DayOfWeek.WED, DayOfWeek.FRI),
        volume = 70, isVibrateEnabled = true, alarmSound = "default_sound",
        snoozeSettings = SnoozeSettings(), isEnabled = true,
        alarmState = AlarmState.UPCOMING.toString()
    )

    private val testMissionEntity = MissionEntity(
        id = 1, alarmId = 1, type = "memory", difficulty = "EASY", rounds = 3,
        iconResId = R.drawable.ic_memory, isCompleted = false
    )

    private val testAlarmWithMissions = AlarmWithMissions(
        alarm = testAlarmEntity,
        missions = listOf(testMissionEntity)
    )

    private val testAlarmModel = AlarmModel(
        id = 1, label = "Test Alarm", time = LocalTime.of(8, 0),
        isDailyAlarm = true, days = setOf(DayOfWeek.MON, DayOfWeek.WED, DayOfWeek.FRI),
        missions = listOf(
            Mission(
                type = MissionType.Memory,
                difficulty = Difficulty.EASY,
                rounds = 3,
                iconResId = R.drawable.ic_memory,
                isCompleted = false
            )
        ),
        volume = 70, isVibrateEnabled = true, alarmSound = "default_sound",
        snoozeSettings = SnoozeSettings(), isEnabled = true,
        alarmState = AlarmState.UPCOMING
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        alarmRepository = AlarmRepositoryImpl(alarmLocalDataSource)
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        Dispatchers.resetMain()
    }


    // ── getAlarms ───────────────────────────────────────────────────────────────

    @Test
    fun `getAlarms - when alarms exist - emits list of mapped alarm models`() = runTest {
        // Given
        coEvery { alarmLocalDataSource.getAllAlarms() } returns flowOf(listOf(testAlarmWithMissions))

        // When
        val result = alarmRepository.getAlarms().first()

        // Then
        // Assert that result is a non-empty List
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `getAlarms - when no alarms exist - emits empty list`() = runTest {
        coEvery { alarmLocalDataSource.getAllAlarms() } returns flowOf(emptyList())

        val emitted = alarmRepository.getAlarms().first()

        assertTrue(emitted.isEmpty())
    }

    @Test
    fun `getAlarms - when data source emits multiple updates - propagates all changes`() = runTest {
        val alarm1 = testAlarmWithMissions
        val alarm2 = testAlarmWithMissions.copy(
            alarm = testAlarmEntity.copy(id = 2, label = "Second Alarm")
        )

        coEvery { alarmLocalDataSource.getAllAlarms() } returns flowOf(
            listOf(alarm1),
            listOf(alarm1, alarm2)
        )

        val emissions = alarmRepository.getAlarms().take(2).toList()

        assertEquals(2, emissions.size)
        assertEquals(1, emissions[0].size)
        assertEquals(2, emissions[1].size)
    }

    @Test
    fun `getAlarmById - when alarm has no missions - returns model with empty mission list`() = runTest {
        val alarmWithoutMissions = testAlarmWithMissions.copy(missions = emptyList())
        coEvery { alarmLocalDataSource.getAlarmById(1) } returns alarmWithoutMissions

        val result = alarmRepository.getAlarmById(1)

        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.missions.isEmpty())
    }



    // ── getAlarmById ────────────────────────────────────────────────────────────

    @Test
    fun `getAlarmById - when alarm exists - returns success with mapped model`() = runTest {
        coEvery { alarmLocalDataSource.getAlarmById(1) } returns testAlarmWithMissions

        val result = alarmRepository.getAlarmById(1)

        assertTrue(result is Result.Success)
        assertEquals(testAlarmModel.id, (result as Result.Success).data.id)
    }

    @Test
    fun `getAlarmById - when alarm does not exist - returns error with NoSuchElementException`() = runTest {
        coEvery { alarmLocalDataSource.getAlarmById(999) } returns null

        val result = alarmRepository.getAlarmById(999)

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is NoSuchElementException)
    }

    @Test
    fun `getAlarmById - when data source throws exception - propagates error`() = runTest {
        val expectedException = RuntimeException("Database connection lost")
        coEvery { alarmLocalDataSource.getAlarmById(any()) } throws expectedException

        val result = alarmRepository.getAlarmById(1)

        assertTrue(result is Result.Error)
        assertEquals(expectedException, (result as Result.Error).exception)
    }



    // ── saveAlarm ───────────────────────────────────────────────────────────────

    @Test
    fun `saveAlarm - when save succeeds - returns new generated id`() = runTest {
        val newId = 42
        coEvery { alarmLocalDataSource.saveAlarmWithMissions(any(), any()) } returns newId

        val result = alarmRepository.saveAlarm(testAlarmModel.copy(id = 0))

        assertTrue(result is Result.Success)
        assertEquals(newId, (result as Result.Success).data)
    }

    @Test
    fun `saveAlarm - when data source fails - returns error`() = runTest {
        val error = RuntimeException("Constraint violation")
        coEvery { alarmLocalDataSource.saveAlarmWithMissions(any(), any()) } throws error

        val result = alarmRepository.saveAlarm(testAlarmModel.copy(id = 0))

        assertTrue(result is Result.Error)
        assertEquals(error, (result as Result.Error).exception)
    }



    // ── updateAlarm ─────────────────────────────────────────────────────────────

    @Test
    fun `updateAlarm - when update succeeds - returns success`() = runTest {
        coEvery { alarmLocalDataSource.updateAlarmWithMissions(any(), any()) } just Runs

        val result = alarmRepository.updateAlarm(testAlarmModel)

        assertTrue(result is Result.Success)
    }

    @Test
    fun `updateAlarm - when data source throws - returns error`() = runTest {
        val error = RuntimeException("Update failed")
        coEvery { alarmLocalDataSource.updateAlarmWithMissions(any(), any()) } throws error

        val result = alarmRepository.updateAlarm(testAlarmModel)

        assertTrue(result is Result.Error)
        assertEquals(error, (result as Result.Error).error)
    }



    // ── deleteAlarmById ─────────────────────────────────────────────────────────

    @Test
    fun `deleteAlarmById - when delete operation succeeds - returns success`() = runTest {
        coEvery { alarmLocalDataSource.deleteAlarmById(any()) } just Runs

        val result = alarmRepository.deleteAlarmById(1)

        assertTrue(result is Result.Success)
    }

    @Test
    fun `deleteAlarmById - when data source throws - returns error`() = runTest {
        val error = RuntimeException("Foreign key violation")
        coEvery { alarmLocalDataSource.deleteAlarmById(any()) } throws error

        val result = alarmRepository.deleteAlarmById(5)

        assertTrue(result is Result.Error)
        assertEquals(error, (result as Result.Error).exception)
    }
*/

}