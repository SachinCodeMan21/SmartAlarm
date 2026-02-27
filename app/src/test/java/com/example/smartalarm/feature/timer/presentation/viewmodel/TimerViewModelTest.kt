package com.example.smartalarm.feature.timer.presentation.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TimerViewModelTest {

/*    // Mocked dependencies
    @MockK lateinit var getAllTimersUseCase: GetAllTimersUseCase
    @MockK lateinit var saveTimerUseCase: SaveTimerUseCase
    @MockK lateinit var systemClockHelper: SystemClockHelper
    @MockK lateinit var timerInputStateManager: TimerInputStateManager
    @MockK lateinit var resourceProvider: ResourceProvider

    // ViewModel under test
    @InjectMockKs
    private lateinit var viewModel: TimerViewModel

    // Test dispatcher
    private lateinit var testDispatcher: TestDispatcher

    // Setup before each test
    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this, relaxUnitFun = true)

        every { resourceProvider.getString(R.string._0) } returns "0"
        every { resourceProvider.getString(R.string._00) } returns "00"
        every { timerInputStateManager.getFormattedTime() } returns "00h : 00m : 00s"
        every { timerInputStateManager.isStartButtonVisible() } returns false
        every { timerInputStateManager.removeLastDigit() } just Runs
    }

    // Cleanup after each test
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    //----------------------------------------------------------
    // Restore Timer Screen
    //----------------------------------------------------------

    *//**
     * Tests the behavior of restoring the UI state when no timers are running.
     * It ensures that the delete button is not visible when there are no active timers.
     *//*
    @Test
    fun `restoreTimerScreenState sets areTimersAlreadyRunning to false when no timers exist`() = runTest {
        // Arrange
        coEvery { getAllTimersUseCase() } returns flowOf(emptyList())

        // Act
        viewModel.initTimerUIState()
        advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isDeleteTimerButtonVisible)
        }
    }

    *//**
     * Tests the behavior of restoring the UI state when timers are running.
     * It ensures that the delete button is visible when timers are active.
     *//*
    @Test
    fun `restoreTimerScreenState sets areTimersAlreadyRunning to true when timers exist`() = runTest {
        // Arrange
        coEvery { getAllTimersUseCase() } returns flowOf(listOf(TimerModel()))

        // Act
        viewModel.initTimerUIState()
        advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isDeleteTimerButtonVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    *//**
     * Ensures that the UI state is correctly restored even when the use case fails.
     * It verifies that an error message is shown in case of a failure.
     *//*
    @Test
    fun `restoreTimerScreenState handles use case error gracefully and shows error message`() = runTest {
        // Arrange: Simulate error from use case
        val errorMessage = "Failed To Load Timers. Try Again Later"
        coEvery { getAllTimersUseCase() } returns flow { throw RuntimeException("Database failure") }
        every { resourceProvider.getString(R.string.failed_to_load_timers) } returns errorMessage

        viewModel.uiEffect.test {
            // Act
            viewModel.initTimerUIState()
            advanceUntilIdle()

            // Assert
            assertEquals(TimerEffect.ShowSnackBar(errorMessage), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }


    //----------------------------------------------------------
    // Handle Timer Screen Keypad Click
    //----------------------------------------------------------

    *//**
     * Tests the behavior when a valid digit is pressed on the keypad.
     * It ensures that the digit is appended to the input and the UI is updated accordingly.
     *//*
    @Test
    fun `handleKeypadClick should append digit when non-zero digit is pressed`() = runTest {
        // Arrange
        val label = "5"

        every { timerInputStateManager.appendDigit(label) } just Runs
        every { timerInputStateManager.getFormattedTime() } returns "00h : 00m : 05s"
        every { timerInputStateManager.isStartButtonVisible() } returns true

        // Act
        viewModel.handleEvent(TimerEvent.HandleKeypadClick(label))

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("00h : 00m : 05s", state.formattedTime)
            assertTrue(state.isStartButtonVisible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    *//**
     * Tests that the "0" or "00" digit is not appended when the input is empty.
     * Ensures that invalid inputs don't alter the UI state.
     *//*
    @Test
    fun `handleKeypadClick should not append zero or double zero when input is empty`() = runTest {
        // Arrange
        val labels = listOf(TimerViewModel.KEY_ZERO, TimerViewModel.KEY_DOUBLE_ZERO)

        every { timerInputStateManager.isStartButtonVisible() } returns false
        every { timerInputStateManager.getFormattedTime() } returns "00h : 00m : 00s"

        for (label in labels) {
            // Act
            viewModel.handleEvent(TimerEvent.HandleKeypadClick(label))

            // Assert
            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals("00h : 00m : 00s", state.formattedTime)
                assertFalse(state.isStartButtonVisible)
                cancelAndIgnoreRemainingEvents()
            }

            verify(exactly = 0) { timerInputStateManager.appendDigit(any()) }
        }
    }

    *//**
     * Tests the behavior when the backspace key is pressed.
     * It ensures that the last digit is removed when backspace is pressed, and the UI is updated.
     *//*
    @Test
    fun `handleKeypadClick should remove last digit when backspace is pressed and input is not empty`() = runTest {
        // Arrange
        val expectedLabel = "00h : 00m : 02s"

        every { timerInputStateManager.removeLastDigit() } just Runs
        every { timerInputStateManager.getFormattedTime() } returns expectedLabel
        every { timerInputStateManager.isStartButtonVisible() } returns false

        // Act
        viewModel.handleEvent(TimerEvent.HandleKeypadClick("25"))
        viewModel.handleEvent(TimerEvent.HandleKeypadClick(TimerViewModel.KEY_BACKSPACE))
        advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(expectedLabel, state.formattedTime)
            assertFalse(state.isStartButtonVisible)
            cancelAndIgnoreRemainingEvents()
        }

        verify(exactly = 1) { timerInputStateManager.removeLastDigit() }
    }

    *//**
     * Tests that the UI state doesn't update when backspace is pressed on an empty input.
     * Ensures that the backspace key does not alter the state when there is nothing to remove.
     *//*
    @Test
    fun `handleKeypadClick should not update state when backspace is pressed and input is empty`() = runTest {
        // Arrange
        val initialFormattedTime = "00h : 00m : 00s"

        every { timerInputStateManager.removeLastDigit() } just Runs
        every { timerInputStateManager.getFormattedTime() } returns initialFormattedTime
        every { timerInputStateManager.isStartButtonVisible() } returns false

        // Act - capture state before and after backspace press
        val beforeState = viewModel.uiState.value

        viewModel.handleEvent(TimerEvent.HandleKeypadClick(TimerViewModel.KEY_BACKSPACE))
        advanceUntilIdle()

        val afterState = viewModel.uiState.value

        // Assert - state has not changed
        assertEquals(beforeState, afterState)

        // Confirm removeLastDigit was still called
        verify(exactly = 1) { timerInputStateManager.removeLastDigit() }
    }

    //----------------------------------------------------------
    // Handle Timer Screen StartTimer Event Click
    //----------------------------------------------------------

    *//**
     * Tests the behavior when the "Start Timer" button is clicked with valid input.
     * Ensures that a new timer is created and the user is navigated to the timer screen.
     *//*
    @Test
    fun `startTimer initiates a new timer when input is valid`() = runTest {
        // Arrange
        val durationMillis = 60000L
        every { timerInputStateManager.timerInputToMillis() } returns durationMillis
        every { systemClockHelper.elapsedRealtime() } returns 1000L
        coEvery { saveTimerUseCase(any()) } returns Result.Success(Unit)
        every { timerInputStateManager.clearInput() } just Runs

        viewModel.uiEffect.test {
            // Act
            viewModel.handleEvent(TimerEvent.HandleStartTimerClick)
            advanceUntilIdle()

            // Assert
            assertEquals(TimerEffect.NavigateToShowTimerScreen, awaitItem())
            coVerify { saveTimerUseCase.invoke(any()) }
            verify { systemClockHelper.elapsedRealtime() }
            verify { timerInputStateManager.clearInput() }
            cancelAndIgnoreRemainingEvents()
        }
    }

    *//**
     * Tests that the "Start Timer" button does nothing when the input duration is zero or less.
     * Ensures that the timer is not started with invalid input.
     *//*
    @Test
    fun `handleStartTimer does nothing when input duration is zero or less`() = runTest {
        // Arrange
        every { timerInputStateManager.timerInputToMillis() } returns 0L

        // Act
        viewModel.handleEvent(TimerEvent.HandleStartTimerClick)
        advanceUntilIdle()

        // Assert
        verify(exactly = 0) { timerInputStateManager.clearInput() }
    }

    *//**
     * Ensures that an error is shown when the save timer use case fails.
     * Verifies that the appropriate error message is displayed.
     *//*
    @Test
    fun `handleStartTimer emits showSnackBar effect when saveTimerUseCase fails`() = runTest {
        // Arrange
        val durationMillis = 60000L
        val errorMessage = "Failed To Save Timer. Please Try Again Later."
        every { timerInputStateManager.timerInputToMillis() } returns durationMillis
        every { systemClockHelper.elapsedRealtime() } returns 1000L
        every { resourceProvider.getString(R.string.unable_to_start_the_timer) } returns errorMessage
        coEvery { saveTimerUseCase(any()) } returns Result.Error(Exception(errorMessage))
        every { timerInputStateManager.clearInput() } just Runs

        viewModel.uiEffect.test {
            // Act
            viewModel.handleEvent(TimerEvent.HandleStartTimerClick)
            advanceUntilIdle()

            // Assert
            val effect = awaitItem()
            assertTrue(effect is TimerEffect.ShowSnackBar)
            assertEquals(errorMessage, effect.message)

            verify(exactly = 0) { timerInputStateManager.clearInput() }
        }
    }*/
}


