package com.example.smartalarm.feature.alarm.presentation.viewmodel.editor

import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class AlarmEditorHomeViewModelTest {

/*
    @MockK
    private lateinit var getAlarmByIdUseCase: GetAlarmByIdUseCase
    @MockK
    private lateinit var saveAlarmUseCase: SaveAlarmUseCase
    @MockK
    private lateinit var updateAlarmUseCase: UpdateAlarmUseCase
    @MockK
    private lateinit var postSaveOrUpdateAlarmUseCase: PostSaveOrUpdateAlarmUseCase
    @MockK
    private lateinit var alarmEditorStateManager: AlarmEditorHomeStateManager

    @MockK
    private lateinit var alarmUiMapper: AlarmUiMapper

    @MockK
    private lateinit var permissionManager: PermissionManager

    @MockK
    private lateinit var numberFormatter: NumberFormatter

    @InjectMockKs
    private lateinit var viewModel: AlarmEditorViewModel

    private val ioDispatcher = StandardTestDispatcher()


    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        Dispatchers.setMain(ioDispatcher)
        every { alarmEditorStateManager.getAlarmState } returns MutableStateFlow(AlarmModel())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }



    // ---------------------------------------------------------------------------------------------
    //  handleSystemEvent Tests
    // ---------------------------------------------------------------------------------------------

    @Test
    fun `handleSystemEvent InitializeAlarmEditorState with id 0 initializes new alarm state`() = runTest {

        // Act
        viewModel.handleSystemEvent(AlarmEditorSystemEvent.InitializeAlarmEditorState(0))

        // Assert
        verify { alarmEditorStateManager.initAlarmState() }
        coVerify(exactly = 0) { getAlarmByIdUseCase(any()) }
    }

    @Test
    fun `handleSystemEvent InitializeAlarmEditorState with valid id loads existing alarm`() = runTest {
        // Arrange
        val alarmId = 10
        val alarm = AlarmModel(id = alarmId)
        coEvery { getAlarmByIdUseCase(alarmId) } returns Result.Success(alarm)

        // Act
        viewModel.handleSystemEvent(AlarmEditorSystemEvent.InitializeAlarmEditorState(alarmId))
        advanceUntilIdle()

        // Assert
        verify { alarmEditorStateManager.initAlarmState() }
        verify { alarmEditorStateManager.setAlarm(alarm) }
        coVerify { getAlarmByIdUseCase(alarmId) }
    }

    @Test
    fun `handleSystemEvent InitializeAlarmEditorState on error shows toast with exception message`() = runTest {
        // Arrange
        val alarmId = 10
        val exceptionMessage = "Database error"
        coEvery { getAlarmByIdUseCase(alarmId) } returns Result.Error(RuntimeException(exceptionMessage))

        // Act
        viewModel.handleSystemEvent(AlarmEditorSystemEvent.InitializeAlarmEditorState(alarmId))

        // Assert
        viewModel.uiEffect.test {
            val effect = awaitItem()
            assertIs<AlarmEditorEffect.ShowToastMessage>(effect)
            assertEquals(exceptionMessage, effect.toastMessage)
            cancelAndIgnoreRemainingEvents()
            coVerify { getAlarmByIdUseCase(alarmId) }
            verify(exactly = 0) { alarmEditorStateManager.setAlarm(any()) }
        }
    }

    @Test
    fun `handleSystemEvent SnoozeUpdated updates snooze settings`() = runTest {
        // Arrange
        val updatedSettings = SnoozeSettings(snoozeIntervalMinutes = 10)

        // Act
        viewModel.handleSystemEvent(AlarmEditorSystemEvent.SnoozeUpdated(updatedSettings))

        // Assert
        verify { alarmEditorStateManager.updateSnooze(updatedSettings) }
    }

    @Test
    fun `handleSystemEvent RetryPendingSaveAction triggers save when called`() = runTest {
        // Act
        viewModel.handleSystemEvent(AlarmEditorSystemEvent.RetryPendingSaveAction)

        // Assert
        verify { viewModel.handleUserEvent(AlarmEditorUserEvent.SaveOrUpdateAlarmClick) }
    }

    @Test
    fun `handleSystemEvent ExactAlarmPermissionGranted triggers save`() = runTest {
        // Act
        viewModel.handleSystemEvent(AlarmEditorSystemEvent.ExactAlarmPermissionGranted)

        // Assert
        verify { viewModel.handleUserEvent(AlarmEditorUserEvent.SaveOrUpdateAlarmClick) }
    }

    @Test
    fun `handleSystemEvent PostNotificationPermissionGranted triggers save`() = runTest {
        // Act
        viewModel.handleSystemEvent(AlarmEditorSystemEvent.PostNotificationPermissionGranted)

        // Assert
        verify { viewModel.handleUserEvent(AlarmEditorUserEvent.SaveOrUpdateAlarmClick) }
    }




    // ---------------------------------------------------------------------------------------------
    //  handleUserEvent – Mission Related
    // ---------------------------------------------------------------------------------------------

    @Test
    fun `HandleMissionItemPlaceHolderClick emits ShowMissionPickerBottomSheet with null existingMission`() = runTest {

        // Arrange
        val position = 2
        val usedMissions = listOf<Mission>()
        every { alarmEditorStateManager.getAlarmState.value.missions } returns usedMissions

        viewModel.uiEffect.test {

            // Act
            viewModel.handleUserEvent(AlarmEditorUserEvent.HandleMissionItemPlaceHolderClick(position))

            // Assert
            val effect = awaitItem()
            assertIs<AlarmEditorEffect.ShowMissionPickerBottomSheet>(effect)
            assertEquals(position, effect.position)
            assertNull(effect.existingMission)
            assertEquals(usedMissions, effect.usedMissions)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `HandleMissionItemClick emits ShowMissionPickerBottomSheet with existingMission and filtered usedMissions`() = runTest {

        // Arrange
        val position = 0
        val existing = Mission(type = MissionType.Shake, iconResId = 1)
        val otherMissions = listOf(Mission(type = MissionType.Maths, iconResId = 2))
        every { alarmEditorStateManager.getAlarmState.value.missions } returns listOf(existing) + otherMissions


        viewModel.uiEffect.test {

            // Act
            viewModel.handleUserEvent(AlarmEditorUserEvent.HandleMissionItemClick(position, existing))

            // Assert
            val effect = awaitItem()
            assertIs<AlarmEditorEffect.ShowMissionPickerBottomSheet>(effect)
            assertEquals(position, effect.position)
            assertEquals(existing, effect.existingMission)
            assertEquals(otherMissions, effect.usedMissions)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `HandleRemoveMissionClick removes mission at position`() = runTest {
        val position = 1
        viewModel.handleUserEvent(AlarmEditorUserEvent.HandleRemoveMissionClick(position))
        verify { alarmEditorStateManager.removeMissionAt(position) }
    }

    @Test
    fun `AlarmMissionSelected emits ShowSelectedMissionBottomSheet`() = runTest {

        // Arrange
        val position = 1
        val mission = Mission(type = MissionType.Memory, iconResId = 1)


        viewModel.uiEffect.test {

            // Act
            viewModel.handleUserEvent(AlarmEditorUserEvent.AlarmMissionSelected(position, mission))

            // Assert
            val effect = awaitItem()
            assertIs<AlarmEditorEffect.ShowSelectedMissionBottomSheet>(effect)
            assertEquals(position, effect.position)
            assertEquals(mission, effect.selectedMission)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `StartAlarmMissionPreview navigates with preview alarm containing only the selected mission`() = runTest {

        // Arrange
        val mission = Mission(type = MissionType.Maths, iconResId = 1)
        val currentAlarm = AlarmModel(id = 5)
        every { alarmEditorStateManager.getAlarmState.value } returns currentAlarm

        viewModel.uiEffect.test {

            // Act
            viewModel.handleUserEvent(AlarmEditorUserEvent.StartAlarmMissionPreview(mission))

            // Assert
            val effect = awaitItem()
            assertIs<AlarmEditorEffect.NavigateToAlarmActivityForMissionPreview>(effect)
            assertEquals(listOf(mission), effect.previewAlarmModel.missions)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `UpdateAlarmMission updates mission at position`() = runTest {
        val position = 2
        val mission = Mission(type = MissionType.Shake, iconResId = 1)

        viewModel.handleUserEvent(AlarmEditorUserEvent.UpdateAlarmMission(position, mission))

        verify { alarmEditorStateManager.updateMission(position, mission) }
    }



    // ---------------------------------------------------------------------------------------------
    //  handleUserEvent – Basic Settings
    // ---------------------------------------------------------------------------------------------

    @Test
    fun `LabelChanged updates label`() = runTest {
        viewModel.handleUserEvent(AlarmEditorUserEvent.LabelChanged("Morning Alarm"))
        verify { alarmEditorStateManager.updateLabel("Morning Alarm") }
    }

    @Test
    fun `TimeChanged updates time`() = runTest {
        viewModel.handleUserEvent(AlarmEditorUserEvent.TimeChanged(8, 15, 0))
        verify { alarmEditorStateManager.updateTime(8, 15, 0) }
    }

    @Test
    fun `VolumeChanged updates volume`() = runTest {
        viewModel.handleUserEvent(AlarmEditorUserEvent.VolumeChanged(80))
        verify { alarmEditorStateManager.updateVolume(80) }
    }

    @Test
    fun `VibrationToggled updates vibration`() = runTest {
        viewModel.handleUserEvent(AlarmEditorUserEvent.VibrationToggled(true))
        verify { alarmEditorStateManager.updateVibration(true) }
    }

    @Test
    fun `LaunchAlarmSoundPicker emits LaunchAlarmSoundPicker with current URI parsed`() = runTest {

        // Arrange
        val uriString = "content://ringtone/123"
        every { alarmEditorStateManager.getAlarmState.value.alarmSound } returns uriString

        viewModel.uiEffect.test {

            // Act
            viewModel.handleUserEvent(AlarmEditorUserEvent.LaunchAlarmSoundPicker)

            // Assert
            val effect = awaitItem()
            assertIs<AlarmEditorEffect.LaunchAlarmSoundPicker>(effect)
            assertEquals(uriString, effect.existingAlarmSound)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `RingtoneSelected updates ringtone`() = runTest {
        val uriString = "content://new/ringtone"
        viewModel.handleUserEvent(AlarmEditorUserEvent.RingtoneSelected(uriString))
        verify { alarmEditorStateManager.updateRingtone(uriString) }
    }

    @Test
    fun `EditSnoozeClick navigates to snooze screen with current settings`() = runTest {

        // Arrange
        val settings = SnoozeSettings()
        every { alarmEditorStateManager.getAlarmState.value.snoozeSettings } returns settings

        viewModel.uiEffect.test {

            // Act
            viewModel.handleUserEvent(AlarmEditorUserEvent.EditSnoozeClick)

            // Assert
            val effect = awaitItem()
            assertIs<AlarmEditorEffect.NavigateToSnoozeAlarmFragment>(effect)
            assertEquals(settings, effect.snoozeSettings)
            cancelAndIgnoreRemainingEvents()
        }
    }


    // ---------------------------------------------------------------------------------------------
    //  saveOrUpdateAlarm Tests
    // ---------------------------------------------------------------------------------------------

    @Test
    fun `saveOrUpdateAlarm - all permissions granted - shows loading, saves, shows toast and finishes`() = runTest {

        // Arrange
        val savedAlarmId = 1
        val currentAlarm = AlarmModel(id = 0, label = "Wake Up")
        val savedAlarm = AlarmModel(id = savedAlarmId, label = "Wake Up")
        val formattedTimeUntilNextAlarmTrigger = "Alarm set for 07:30 AM tomorrow"


        every { permissionManager.isPostNotificationPermissionGranted() } returns true
        every { permissionManager.isFullScreenNotificationPermissionGranted() } returns true
        every { permissionManager.isScheduleExactAlarmPermissionGranted() } returns true
        every { alarmEditorStateManager.getAlarmState.value } returns currentAlarm
        every {  postSaveOrUpdateAlarmUseCase(savedAlarm) } returns Result.Success(formattedTimeUntilNextAlarmTrigger)
        coEvery { saveAlarmUseCase(any()) } returns Result.Success(savedAlarmId)


        viewModel.uiEffect.test {

            // Act
            viewModel.handleUserEvent(AlarmEditorUserEvent.SaveOrUpdateAlarmClick)
            advanceUntilIdle()

            // Assert
            assertIs<AlarmEditorEffect.ShowSaveUpdateLoadingIndicator>(awaitItem()) // Show loading indicator
            assertIs<AlarmEditorEffect.ShowSaveUpdateLoadingIndicator>(awaitItem()) // Hide loading indicator after delay

            assertIs<AlarmEditorEffect.ShowToastMessage>(awaitItem()).apply {
                assertEquals(formattedTimeUntilNextAlarmTrigger, toastMessage) // Verify success message
            }
            assertIs<AlarmEditorEffect.FinishEditorActivity>(awaitItem()) // Verify finish activity effect
            cancelAndIgnoreRemainingEvents()


            // Verify the correct use case was called (save in this case)
            coVerify { saveAlarmUseCase(currentAlarm) }
            coVerify(exactly = 0) { updateAlarmUseCase(any()) }  // Ensure update is not called

        }



    }

    @Test
    fun `saveOrUpdateAlarm - all permissions granted - shows loading, updates, shows toast and finishes`() = runTest {

        // Arrange
        val updatedAlarm = AlarmModel(id = 1, label = "Wake Up")
        val formattedTimeUntilNextAlarmTrigger = "Alarm set for 07:30 AM tomorrow"

        every { permissionManager.isPostNotificationPermissionGranted() } returns true
        every { permissionManager.isFullScreenNotificationPermissionGranted() } returns true
        every { permissionManager.isScheduleExactAlarmPermissionGranted() } returns true
        every { alarmEditorStateManager.getAlarmState.value } returns updatedAlarm
        every {  postSaveOrUpdateAlarmUseCase(updatedAlarm) } returns Result.Success(formattedTimeUntilNextAlarmTrigger)
        coEvery { updateAlarmUseCase(any()) } returns Result.Success(Unit)

        viewModel.uiEffect.test {

            // Act
            viewModel.handleUserEvent(AlarmEditorUserEvent.SaveOrUpdateAlarmClick)

            // Assert
            assertIs<AlarmEditorEffect.ShowSaveUpdateLoadingIndicator>(awaitItem()) // Show loading indicator
            assertIs<AlarmEditorEffect.ShowSaveUpdateLoadingIndicator>(awaitItem()) // Hide loading indicator after delay
            assertIs<AlarmEditorEffect.ShowToastMessage>(awaitItem()).apply {
                assertEquals(formattedTimeUntilNextAlarmTrigger, toastMessage) // Verify success message
            }
            assertIs<AlarmEditorEffect.FinishEditorActivity>(awaitItem()) // Verify finish activity effect
            cancelAndIgnoreRemainingEvents()

            // Verify the correct use case was called (update in this case)
            coVerify { updateAlarmUseCase(updatedAlarm) }
            coVerify(exactly = 0) { saveAlarmUseCase(any()) }

        }

    }

    @Test
    fun `saveOrUpdateAlarm - missing post notification - requests it first`() = runTest {

        // Arrange
        every { permissionManager.isPostNotificationPermissionGranted() } returns false


        viewModel.uiEffect.test {

            // Act
            viewModel.handleUserEvent(AlarmEditorUserEvent.SaveOrUpdateAlarmClick)

            // Assert
            val effect = awaitItem()
            assertIs<AlarmEditorEffect.LaunchPostNotificationPermissionRequest>(effect)
            cancelAndIgnoreRemainingEvents()

            coVerify(exactly = 0) { saveAlarmUseCase(any()) }

        }

    }

    @Test
    fun `saveOrUpdateAlarm - post granted but full-screen denied - requests full-screen intent`() = runTest {

        // Arrange
        every { permissionManager.isPostNotificationPermissionGranted() } returns true
        every { permissionManager.isFullScreenNotificationPermissionGranted() } returns false

        viewModel.uiEffect.test {

            //Act
            viewModel.handleUserEvent(AlarmEditorUserEvent.SaveOrUpdateAlarmClick)

            // Assert
            val effect = awaitItem()
            assertIs<AlarmEditorEffect.LaunchFullScreenNotificationPermissionRequest>(effect)
            cancelAndIgnoreRemainingEvents()

            coVerify(exactly = 0) { saveAlarmUseCase(any()) }

        }

    }

    @Test
    fun `saveOrUpdateAlarm - post and full-screen granted but exact alarm denied - requests exact alarm`() = runTest {

        // Arrange
        every { permissionManager.isPostNotificationPermissionGranted() } returns true
        every { permissionManager.isFullScreenNotificationPermissionGranted() } returns true
        every { permissionManager.isScheduleExactAlarmPermissionGranted() } returns false

        viewModel.uiEffect.test {

            // Act
            viewModel.handleUserEvent(AlarmEditorUserEvent.SaveOrUpdateAlarmClick)

            //Assert
            val effect = awaitItem()
            assertIs<AlarmEditorEffect.LaunchExactAlarmPermissionRequest>(effect)
            cancelAndIgnoreRemainingEvents()

            coVerify(exactly = 0) { saveAlarmUseCase(any()) }

        }
    }

    @Test
    fun `saveOrUpdateAlarm - save fails - shows error via ShowError effect`() = runTest {

        // Arrange
        every { permissionManager.isPostNotificationPermissionGranted() } returns true
        every { permissionManager.isFullScreenNotificationPermissionGranted() } returns true
        every { permissionManager.isScheduleExactAlarmPermissionGranted() } returns true

        val errorMsg = "Failed to perform alarm post save scheduling. Please try again later."
        coEvery { saveAlarmUseCase(any()) } returns Result.Error(RuntimeException(errorMsg))
        coEvery { updateAlarmUseCase(any()) } returns Result.Error(RuntimeException(errorMsg))
        every { alarmEditorStateManager.getAlarmState.value } returns AlarmModel()

        viewModel.uiEffect.test {

            // Act
            viewModel.handleUserEvent(AlarmEditorUserEvent.SaveOrUpdateAlarmClick)
            advanceUntilIdle()

            // Assert
            assertIs<AlarmEditorEffect.ShowSaveUpdateLoadingIndicator>(awaitItem()) // true
            assertIs<AlarmEditorEffect.ShowSaveUpdateLoadingIndicator>(awaitItem()) // false
            assertIs<AlarmEditorEffect.ShowError>(awaitItem()).apply {
                assertEquals(errorMsg, message)
            }
            expectNoEvents()
        }
    }

    @Test
    fun `saveOrUpdateAlarm - update fails - shows error via ShowError effect`() = runTest {

        // Arrange
        every { permissionManager.isPostNotificationPermissionGranted() } returns true
        every { permissionManager.isFullScreenNotificationPermissionGranted() } returns true
        every { permissionManager.isScheduleExactAlarmPermissionGranted() } returns true

        val errorMsg = "Failed to perform alarm post update scheduling. Please try again later."
        coEvery { saveAlarmUseCase(any()) } returns Result.Error(RuntimeException(errorMsg))
        coEvery { updateAlarmUseCase(any()) } returns Result.Error(RuntimeException(errorMsg))

        val existingAlarm = AlarmModel(id = 1, label = "Wake Up")
        every { alarmEditorStateManager.getAlarmState.value } returns existingAlarm


        // Assert
        viewModel.uiEffect.test {

            // Act
            viewModel.handleUserEvent(AlarmEditorUserEvent.SaveOrUpdateAlarmClick)

            // Assert
            assertIs<AlarmEditorEffect.ShowSaveUpdateLoadingIndicator>(awaitItem()) // true
            assertIs<AlarmEditorEffect.ShowSaveUpdateLoadingIndicator>(awaitItem()) // false
            assertIs<AlarmEditorEffect.ShowError>(awaitItem()).apply {
                assertEquals(errorMsg, message)
            }

            expectNoEvents()
        }

        // Verify the update use case was called with the existing alarm
        coVerify { updateAlarmUseCase(existingAlarm) }

    }


    // ---------------------------------------------------------------------------------------------
    //  Helper Method Tests
    // ---------------------------------------------------------------------------------------------

    @Test
    fun `getTwoDigitFormattedNumber delegates to numberFormatter with leading zero flag`() {
        val number = 7
        val expected = "07"
        every { numberFormatter.formatLocalizedNumber(7L, true) } returns expected

        val result = viewModel.getLocalizedNumber(number)

        assertEquals(expected, result)
        verify { numberFormatter.formatLocalizedNumber(7L, true) }
    }
*/

}
