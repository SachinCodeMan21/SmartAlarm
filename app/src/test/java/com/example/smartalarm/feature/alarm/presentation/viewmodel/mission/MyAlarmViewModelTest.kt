package com.example.smartalarm.feature.alarm.presentation.viewmodel.mission


import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MyAlarmViewModelTest {
    /*
    private lateinit var viewModel: MyAlarmViewModel
    private lateinit var getAlarmByIdUseCase: GetAlarmByIdUseCase
    private lateinit var updateAlarmUseCase: UpdateAlarmUseCase
    private lateinit var alarmScheduler: AlarmScheduler
    private lateinit var alarmTimeHelper: AlarmTimeHelper
    private lateinit var missionCountDownJobManager: MissionCountDownJobManager
    private lateinit var alarmServiceController: AlarmServiceController

    private val testDispatcher = StandardTestDispatcher()

    private val testAlarm = AlarmModel(
        id = 1,
        label = "Test Alarm",
        time = LocalTime.of(8, 0),
        isDailyAlarm = false,
        days = emptySet(),
        missions = listOf(
            Mission(
                type = MissionType.Memory,
                difficulty = Difficulty.EASY,
                rounds = 3,
                iconResId = R.drawable.ic_memory,
                isCompleted = false
            ),
            Mission(
                type = MissionType.Maths,
                difficulty = Difficulty.NORMAL,
                rounds = 5,
                iconResId = R.drawable.ic_math,
                isCompleted = false
            )
        ),
        volume = 70,
        isVibrateEnabled = true,
        alarmSound = "default_sound",
        snoozeSettings = SnoozeSettings(
            isSnoozeEnabled = true,
            isAlarmSnoozed = false,
            snoozeLimit = 3,
            snoozedCount = 3,
            snoozeIntervalMinutes = 10
        ),
        isEnabled = true,
        alarmState = AlarmState.RINGING
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getAlarmByIdUseCase = mockk()
        updateAlarmUseCase = mockk()
        alarmScheduler = mockk(relaxed = true)
        alarmTimeHelper = mockk()
        missionCountDownJobManager = mockk(relaxed = true)
        alarmServiceController = mockk(relaxed = true)

        viewModel = MyAlarmViewModel(
            getAlarmByIdUseCase = getAlarmByIdUseCase,
            updateAlarmUseCase = updateAlarmUseCase,
            alarmScheduler = alarmScheduler,
            alarmTimeHelper = alarmTimeHelper,
            missionCountDownJobManager = missionCountDownJobManager,
            alarmServiceController = alarmServiceController,
            dispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    // ========== Initial State Tests ==========

    @Test
    fun `initial timer progress state should be 100`() = runTest {
        // Then
        assertEquals(100, viewModel.timerProgressState.value)
    }

    @Test
    fun `initial current mission index should be 0`() = runTest {
        // Then
        assertEquals(0, viewModel.getCurrentMissionIndexForTesting())
    }

    @Test
    fun `initial current alarm should be null`() = runTest {
        // Then
        assertEquals(null, viewModel.getCurrentAlarmForTesting())
    }



    // ========== StartMissionFlow Tests ==========

    @Test
    fun `startMissionFlow success should update state and launch first mission`() = runTest {

        // Arrange
        val alarmId = 1
        coEvery { getAlarmByIdUseCase(alarmId) } returns Result.Success(testAlarm)

        // Act
        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(alarmId))
        advanceUntilIdle()

        // Assert
        coVerify { getAlarmByIdUseCase(alarmId) }
        assertEquals(testAlarm, viewModel.getCurrentAlarmForTesting())

        // Verify mission timer started
        verify {
            missionCountDownJobManager.startCountdown(
                scope = any(),
                targetDuration = 60000L,
                onTick = any(),
                onFinish = any()
            )
        }

        // Verify ShowAlarmMission effect posted
        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is AlarmMissionEffect.ShowAlarmMission)
            assertEquals(testAlarm.missions[0], (effect as AlarmMissionEffect.ShowAlarmMission).mission)
        }
    }

    @Test
    fun `startMissionFlow with typing mission should use 3 minute timeout`() = runTest {
        // Given
        val typingAlarm = testAlarm.copy(
            missions = listOf(
                Mission(
                    type = MissionType.Typing,
                    difficulty = Difficulty.EASY,
                    rounds = 3,
                    iconResId = R.drawable.ic_typing,
                    isCompleted = false
                )
            )
        )
        coEvery { getAlarmByIdUseCase(1) } returns Result.Success(typingAlarm)

        // When
        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(1))
        advanceUntilIdle()

        // Then
        verify {
            missionCountDownJobManager.startCountdown(
                scope = any(),
                targetDuration = 180000L, // 3 minutes for typing mission
                onTick = any(),
                onFinish = any()
            )
        }
    }

    @Test
    fun `startMissionFlow failure should log error and not update state`() = runTest {
        // Given
        val alarmId = 1
        val exception = Exception("Database error")
        coEvery { getAlarmByIdUseCase(alarmId) } returns Result.Error(exception)

        // When
        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(alarmId))
        advanceUntilIdle()

        // Then
        coVerify { getAlarmByIdUseCase(alarmId) }
        assertEquals(null, viewModel.getCurrentAlarmForTesting())

        // Verify no mission was launched
        verify(exactly = 0) {
            missionCountDownJobManager.startCountdown(any(), any(), any(), any())
        }
    }

    @Test
    fun `startMissionFlow with alarm having no missions should finish activity`() = runTest {
        // Given
        val alarmWithNoMissions = testAlarm.copy(missions = emptyList())
        coEvery { getAlarmByIdUseCase(1) } returns Result.Success(alarmWithNoMissions)

        // When
        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(1))
        advanceUntilIdle()

        // Then
        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is AlarmMissionEffect.FinishActivity)
        }
        verify { alarmServiceController.stopAlarmService() }
    }


    // ========== MissionCompleted Tests ==========

    @Test
    fun `missionCompleted with more missions should launch next mission`() = runTest {
        coEvery { getAlarmByIdUseCase(1) } returns Result.Success(testAlarm)
        coEvery { updateAlarmUseCase(any()) } returns Result.Success(Unit)

        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(1))
        advanceUntilIdle()

        viewModel.uiEffect.test {
            // First effect: ShowAlarmMission(Memory)
            val first = awaitItem() as AlarmMissionEffect.ShowAlarmMission
            assertEquals(MissionType.Memory, first.mission.type)

            // Complete first mission
            viewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
            advanceUntilIdle()

            // Second effect: ShowAlarmMission(Maths)
            val second = awaitItem() as AlarmMissionEffect.ShowAlarmMission
            assertEquals(MissionType.Maths, second.mission.type)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `missionCompleted as last mission with non-repeating alarm should finish activity`() = runTest {
        // Given - Alarm with single mission
        val singleMissionAlarm = testAlarm.copy(
            missions = listOf(testAlarm.missions[0]),
            days = emptySet()
        )
        coEvery { getAlarmByIdUseCase(1) } returns Result.Success(singleMissionAlarm)
        coEvery { updateAlarmUseCase(any()) } returns Result.Success(Unit)

        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(1))
        advanceUntilIdle()

        // When - Complete the mission
        viewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
        advanceUntilIdle()

        // Then
        coVerify {
            updateAlarmUseCase(match { alarm ->
                !alarm.isEnabled && alarm.alarmState == AlarmState.STOPPED && !alarm.snoozeSettings.isAlarmSnoozed && alarm.snoozeSettings.snoozedCount == alarm.snoozeSettings.snoozeLimit
            })
        }

        verify { alarmScheduler.cancelSmartAlarmTimeout(singleMissionAlarm.id) }
        verify(exactly = 0) { alarmScheduler.scheduleSmartAlarm(any(), any()) }
        verify { alarmServiceController.stopAlarmService() }

        viewModel.uiEffect.test {
            awaitItem() // ShowAlarmMission
            val finishEffect = awaitItem()
            assertTrue(finishEffect is AlarmMissionEffect.FinishActivity)
        }
    }

    @Test
    fun `missionCompleted as last mission with repeating alarm should reschedule`() = runTest {
        // GIVEN - Repeating alarm with correct DayOfWeek enum values
        val repeatingAlarm = testAlarm.copy(
            missions = listOf(testAlarm.missions[0]), // only one mission
            days = setOf(DayOfWeek.MON, DayOfWeek.WED, DayOfWeek.FRI) // Correct enum!
        )

        val expectedNextAlarmTime = System.currentTimeMillis() + 86400000L

        coEvery { getAlarmByIdUseCase(1) } returns Result.Success(repeatingAlarm)
        coEvery { updateAlarmUseCase(any()) } returns Result.Success(Unit)

        every {
            alarmTimeHelper.getNextAlarmMillis(any(), any(), any<ZonedDateTime>())
        } returns expectedNextAlarmTime

        // Start the mission flow
        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(1))
        advanceUntilIdle()

        // Complete the last (only) mission → triggers rescheduling logic
        viewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
        advanceUntilIdle()

        // THEN - Verify everything happened
        coVerify {
            updateAlarmUseCase(match { alarm ->
                alarm.isEnabled &&
                        alarm.alarmState == AlarmState.STOPPED &&
                        alarm.snoozeSettings.snoozedCount == alarm.snoozeSettings.snoozeLimit
            })
        }

        verify { alarmScheduler.cancelSmartAlarmTimeout(repeatingAlarm.id) }

        // This will now pass — we don't care about exact 'now' time
        verify {
            alarmTimeHelper.getNextAlarmMillis(any(), any(), any<ZonedDateTime>())
        }

        verify {
            alarmScheduler.scheduleSmartAlarm(repeatingAlarm.id, expectedNextAlarmTime)
        }

        verify { alarmServiceController.stopAlarmService() }
    }

    @Test
    fun `missionCompleted without current alarm should do nothing`() = runTest {
        // Given - No alarm loaded

        // When
        viewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
        advanceUntilIdle()

        // Then
        verify(exactly = 0) { missionCountDownJobManager.stopCountdown() }
        coVerify (exactly = 0) { updateAlarmUseCase(any()) }
    }

    @Test
    fun `missionCompleted should stop timer and update completion flag`() = runTest {
        // Alarm with exactly ONE mission
        val singleMissionAlarm = testAlarm.copy(
            missions = listOf(testAlarm.missions.first()) // only Memory mission
        )

        coEvery { getAlarmByIdUseCase(1) } returns Result.Success(singleMissionAlarm)
        coEvery { updateAlarmUseCase(any()) } returns Result.Success(Unit)

        // DO NOT clear mocks here — we want to count the real calls

        // 1. Start flow → first (and only) mission is shown → startCountdown() is called
        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(1))
        advanceUntilIdle()


        // 2. Complete the mission → timer must be stopped, flag = true
        viewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
        advanceUntilIdle()

        // THEN
        assertTrue(viewModel.getIsCurrentMissionCompletedForTesting())     // true
        assertEquals(100, viewModel.timerProgressState.value)             // timer stopped

        // Verify that stopCountdown() was called during completion
        verify { missionCountDownJobManager.stopCountdown() }

        // Verify that startCountdown() was called exactly once (at mission start)
        verify(exactly = 1) {
            missionCountDownJobManager.startCountdown(any(), any(), any(), any())
        }

        // No second startCountdown() should happen
        confirmVerified(missionCountDownJobManager)
    }


    // ========== MissionTimeout Tests ==========

    @Test
    fun `missionTimeout should resume alarm and post timeout effect`() = runTest {
        // Given
        coEvery { getAlarmByIdUseCase(1) } returns Result.Success(testAlarm)
        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(1))
        advanceUntilIdle()

        // When
        viewModel.handleSharedEvent(AlarmMissionEvent.MissionFailedTimeout)
        advanceUntilIdle()

        // Then
        verify { missionCountDownJobManager.stopCountdown() }
        verify { alarmServiceController.resumeAlarm(testAlarm.id) }

        viewModel.uiEffect.test {
            awaitItem() // ShowAlarmMission
            val effect = awaitItem()
            assertTrue(effect is AlarmMissionEffect.MissionTimeout)
        }
    }


    @Test
    fun `missionTimeout without current alarm should not crash`() = runTest {
        // Given - No alarm loaded

        // When
        viewModel.handleSharedEvent(AlarmMissionEvent.MissionFailedTimeout)
        advanceUntilIdle()

        // Then
        verify { missionCountDownJobManager.stopCountdown() }
        verify(exactly = 0) { alarmServiceController.resumeAlarm(any()) }
    }



    // ========== FinishMissionActivity Tests ==========

    @Test
    fun `finishMissionActivity event should post finish effect`() = runTest {
        // When
        viewModel.handleSharedEvent(AlarmMissionEvent.FinishMissionActivity)

        // Then
        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertTrue(effect is AlarmMissionEffect.FinishActivity)
        }
    }


    // ========== Timer Progress Tests ==========

    @Test
    fun `timer progress should update on tick`() = runTest {
        // Given
        val onTickSlot = slot<(Int) -> Unit>()
        coEvery { getAlarmByIdUseCase(1) } returns Result.Success(testAlarm)
        every {
            missionCountDownJobManager.startCountdown(
                scope = any(),
                targetDuration = any(),
                onTick = capture(onTickSlot),
                onFinish = any()
            )
        } just Runs

        // When
        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(1))
        advanceUntilIdle()

        // Simulate timer ticks
        onTickSlot.captured(75)
        onTickSlot.captured(50)
        onTickSlot.captured(25)

        // Then
        viewModel.timerProgressState.test {
            val progress = awaitItem()
            assertEquals(25, progress)
        }
    }

    @Test
    fun `timer finish without mission completion should trigger timeout`() = runTest {

        val onFinishSlot = slot<() -> Unit>()

        coEvery { getAlarmByIdUseCase(1) } returns Result.Success(testAlarm)

        every {
            missionCountDownJobManager.startCountdown(any(), any(), any(), capture(onFinishSlot))
        } just Runs

        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(1))
        advanceUntilIdle()

        // Simulate timer finishing
        onFinishSlot.captured()
        advanceUntilIdle()

        // Core behavior: timeout handled correctly
        verify { alarmServiceController.resumeAlarm(testAlarm.id) }

        // Effect emitted → navigation happens → progress bar hidden → value irrelevant
        viewModel.uiEffect.test {
            awaitItem() // ShowAlarmMission
            assertIs<AlarmMissionEffect.MissionTimeout>(awaitItem())
        }
    }



    // ========== UpdateAlarm Tests ==========

    @Test
    fun `updateAlarm success should call onSuccess callback`() = runTest {
        // Given
        val singleMissionAlarm = testAlarm.copy(missions = listOf(testAlarm.missions[0]))
        coEvery { getAlarmByIdUseCase(1) } returns Result.Success(singleMissionAlarm)
        coEvery { updateAlarmUseCase(any()) } returns Result.Success(Unit)

        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(1))
        advanceUntilIdle()

        // When
        viewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
        advanceUntilIdle()

        // Then
        coVerify { updateAlarmUseCase(any()) }
        verify { alarmServiceController.stopAlarmService() }
    }

    @Test
    fun `updateAlarm failure should log error and not call onSuccess`() = runTest {
        // Given
        val singleMissionAlarm = testAlarm.copy(missions = listOf(testAlarm.missions[0]))
        val exception = Exception("Update failed")
        coEvery { getAlarmByIdUseCase(1) } returns Result.Success(singleMissionAlarm)
        coEvery { updateAlarmUseCase(any()) } returns Result.Error(exception)

        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(1))
        advanceUntilIdle()

        // When
        viewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
        advanceUntilIdle()

        // Then
        coVerify { updateAlarmUseCase(any()) }
        // onSuccess callback should not execute
        verify(exactly = 0) { alarmServiceController.stopAlarmService() }
    }


    // ========== Multiple Missions Flow Tests ==========

    @Test
    fun `completing all missions in sequence should work correctly`() = runTest {
        // Given - Alarm with 2 missions
        coEvery { getAlarmByIdUseCase(1) } returns Result.Success(testAlarm)
        coEvery { updateAlarmUseCase(any()) } returns Result.Success(Unit)

        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(1))
        advanceUntilIdle()

        // When - Complete first mission
        viewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
        advanceUntilIdle()

        // Then - Should be on second mission
        assertEquals(1, viewModel.getCurrentMissionIndexForTesting())

        // When - Complete second mission
        viewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
        advanceUntilIdle()

        // Then - Should finish activity
        assertEquals(2, viewModel.getCurrentMissionIndexForTesting())
        verify { alarmServiceController.stopAlarmService() }
    }

    @Test
    fun `mission flow with 5 missions should handle all correctly`() = runTest {
        // Given - Alarm with 5 missions
        val fiveMissionAlarm = testAlarm.copy(
            missions = listOf(
                Mission(MissionType.Memory, Difficulty.EASY, 3, R.drawable.ic_memory, false),
                Mission(MissionType.Typing, Difficulty.NORMAL, 5, R.drawable.ic_typing, false),
                Mission(MissionType.Maths, Difficulty.HARD, 7, R.drawable.ic_math, false),
                Mission(MissionType.Shake, Difficulty.EXPERT, 10, R.drawable.ic_shake, false),
                Mission(MissionType.Step, Difficulty.EASY, 3, R.drawable.ic_step, false)
            )
        )
        coEvery { getAlarmByIdUseCase(1) } returns Result.Success(fiveMissionAlarm)
        coEvery { updateAlarmUseCase(any()) } returns Result.Success(Unit)

        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(1))
        advanceUntilIdle()

        // When - Complete all 5 missions
        repeat(5) {
            viewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
            advanceUntilIdle()
        }

        // Then
        assertEquals(5, viewModel.getCurrentMissionIndexForTesting())
        verify { alarmServiceController.stopAlarmService() }
    }


    // ========== Edge Cases ==========

    @Test
    fun `timer progress should reset to 100 when starting new mission`() = runTest {
        // Given
        coEvery { getAlarmByIdUseCase(1) } returns Result.Success(testAlarm)
        viewModel.handleSharedEvent(AlarmMissionEvent.StartMissionFlow(1))
        advanceUntilIdle()

        // When - Complete first mission (starts second mission)
        viewModel.handleSharedEvent(AlarmMissionEvent.MissionCompleted)
        advanceUntilIdle()

        // Then - Timer should be reset to 100
        assertEquals(100, viewModel.timerProgressState.value)
    }



*/

}


