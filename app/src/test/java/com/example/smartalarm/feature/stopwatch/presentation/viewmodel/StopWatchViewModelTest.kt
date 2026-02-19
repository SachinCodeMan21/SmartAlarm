package com.example.smartalarm.feature.stopwatch.presentation.viewmodel

import app.cash.turbine.test
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.framework.jobmanager.contract.BlinkEffectJobManager
import com.example.smartalarm.feature.stopwatch.framework.jobmanager.contract.StopwatchTickerJobManager
import com.example.smartalarm.feature.stopwatch.presentation.event.StopwatchEvent
import  com.example.smartalarm.core.model.Result
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.stopwatch.presentation.effect.StopwatchEffect
import com.example.smartalarm.feature.stopwatch.presentation.mapper.StopwatchUiMapper
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchUiModel
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import com.example.smartalarm.R
import com.example.smartalarm.core.permission.PermissionManager
import com.example.smartalarm.feature.stopwatch.domain.model.StopWatchLapModel
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.TestDispatcher
import kotlin.test.assertEquals


@OptIn(ExperimentalCoroutinesApi::class)
class StopWatchViewModelTest {
/*

    @MockK
    private lateinit var stopwatchUseCase: StopwatchUseCases

    @MockK
    private lateinit var stopwatchUiMapper: StopwatchUiMapper

    @MockK
    private lateinit var resourceProvider: ResourceProvider

    @MockK
    lateinit var permissionManager: PermissionManager

    @MockK
    private lateinit var blinkEffectJobManager: BlinkEffectJobManager

    @MockK
    private lateinit var stopwatchTickerJobManager: StopwatchTickerJobManager

    @MockK
    private lateinit var stopwatchStateManager: StopwatchStateManager

    @InjectMockKs
    private lateinit var viewModel: StopWatchViewModel

    private lateinit var testDispatcher: TestDispatcher


    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this, relaxed = true)

        // Mock the behavior of permission manager
        every { permissionManager.isPostNotificationPermissionGranted() } returns true

        // Mock all error string resources used in ViewModel
        every { resourceProvider.getString(R.string.failed_to_restore_stopwatch_state) } returns "Failed to restore stopwatch state"
        every { resourceProvider.getString(R.string.failed_to_start_stopwatch) } returns "Failed to start stopwatch"
        every { resourceProvider.getString(R.string.failed_to_pause_stopwatch_state) } returns "Failed to pause stopwatch"
        every { resourceProvider.getString(R.string.failed_to_reset_stopwatch_state) } returns "Failed to reset stopwatch"
        every { resourceProvider.getString(R.string.failed_to_record_lap_stopwatch_state) } returns "Failed to record stopwatch lap"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }


    // ================================================================================================
    // Initial State Tests
    // ================================================================================================

    @Test
    fun initialState_shouldBeZeroed() = runTest {
        // Assert initial state is zeroed
        assertEquals(StopwatchUiModel(), viewModel.uiState.value)
    }


    // ================================================================================================
    // StartForegroundService Tests
    // ================================================================================================

    @Test
    fun startForegroundService_whenElapsedTimeIsZeroOrNegative_shouldStopJobsAndEmitBlinkVisibility() =
        runTest {

            // Arrange
            val currentState = StopwatchModel(elapsedTime = 0L, isRunning = true)
            every { stopwatchStateManager.getCurrentStopwatchState() } returns currentState

            viewModel.uiEffect.test {

                // Act
                viewModel.handleEvent(StopwatchEvent.StartForegroundService)

                // Assert
                verify(exactly = 1) { stopwatchTickerJobManager.stop() }
                verify(exactly = 1) { blinkEffectJobManager.stopBlinking() }
                assertEquals(StopwatchEffect.BlinkVisibilityChanged(true), awaitItem())
                expectNoEvents()
            }
        }

    @Test
    fun startForegroundService_whenElapsedTimeIsPositive_shouldEmitBlinkVisibilityAndStartForegroundService() =
        runTest {
            // Arrange
            val currentState = StopwatchModel(elapsedTime = 5000L, isRunning = true)
            every { stopwatchStateManager.getCurrentStopwatchState() } returns currentState

            viewModel.uiEffect.test {
                // Act
                viewModel.handleEvent(StopwatchEvent.StartForegroundService)

                // Assert
                verify(exactly = 1) { blinkEffectJobManager.stopBlinking() }
                verify(exactly = 1) { stopwatchTickerJobManager.stop() }
                assertEquals(StopwatchEffect.BlinkVisibilityChanged(true), awaitItem())
                assertEquals(StopwatchEffect.StartForegroundService, awaitItem())
                expectNoEvents()
            }
        }

    @Test
    fun stopwatchForegroundService_shouldAlwaysEmitStopForegroundEffect() =
        runTest {
            viewModel.uiEffect.test {
                // Act
                viewModel.handleEvent(StopwatchEvent.StopwatchForegroundService)

                // Assert
                assert(awaitItem() is StopwatchEffect.StopForegroundService)
                cancelAndIgnoreRemainingEvents()
            }
        }



    // ================================================================================================
    // Restore Stopwatch State Tests
    // ================================================================================================

    @Test
    fun restoreStopwatch_whenNoSavedState_shouldRestoreInitialState() = runTest {
        // Arrange
        val initStopwatch = StopwatchModel()

        coEvery { stopwatchUseCase.getSavedStopwatchState() } returns Result.Success(null)
        every { stopwatchStateManager.restoreStopwatchState(initStopwatch) } just runs

        // Act
        viewModel.handleEvent(StopwatchEvent.RestoreStopwatch)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { stopwatchUseCase.getSavedStopwatchState() }
        verify(exactly = 1) { stopwatchStateManager.restoreStopwatchState(initStopwatch) }
    }

    @Test
    fun restoreStopwatch_whenSavedStateIsRunning_shouldStartStopwatchAndStopBlinking() = runTest {
        // Arrange
        val restoredRunningState = StopwatchModel(elapsedTime = 1000L, isRunning = true)

        coEvery { stopwatchUseCase.getSavedStopwatchState() } returns Result.Success(restoredRunningState)
        every { stopwatchStateManager.restoreStopwatchState(restoredRunningState) } just runs
        every { stopwatchStateManager.getCurrentStopwatchState() } returns restoredRunningState
        coEvery { stopwatchUseCase.startStopwatch(restoredRunningState) } returns Result.Success(restoredRunningState)
        every { blinkEffectJobManager.stopBlinking() } just Runs

        viewModel.uiEffect.test {
            // Act
            viewModel.handleEvent(StopwatchEvent.RestoreStopwatch)
            advanceUntilIdle()

            // Assert
            assert(awaitItem() is StopwatchEffect.BlinkVisibilityChanged)
            cancelAndIgnoreRemainingEvents()

            coVerify(exactly = 1) { stopwatchUseCase.getSavedStopwatchState() }
            verify(exactly = 1) { stopwatchStateManager.restoreStopwatchState(restoredRunningState) }
            verify(exactly = 1) { blinkEffectJobManager.stopBlinking() }
            coVerify(exactly = 1) { stopwatchUseCase.startStopwatch(restoredRunningState) }
            verify(exactly = 1) { stopwatchTickerJobManager.start(any(), any(), any()) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun restoreStopwatch_whenSavedStateIsPausedWithPositiveTime_shouldStartBlinking() = runTest {
        // Arrange
        val restoredPausedState = StopwatchModel(elapsedTime = 1000L, isRunning = false)

        coEvery { stopwatchUseCase.getSavedStopwatchState() } returns Result.Success(restoredPausedState)
        every { stopwatchStateManager.restoreStopwatchState(restoredPausedState) } just runs
        every { stopwatchStateManager.getCurrentStopwatchState() } returns restoredPausedState
        every { blinkEffectJobManager.startBlinking(scope = any(), onVisibilityChanged = any()) } just Runs

        viewModel.uiEffect.test {

            // Act
            viewModel.handleEvent(StopwatchEvent.RestoreStopwatch)
            advanceUntilIdle()

            // Assert
            coVerify(exactly = 1) { stopwatchUseCase.getSavedStopwatchState() }
            verify(exactly = 1) { stopwatchStateManager.restoreStopwatchState(restoredPausedState) }
            verify(exactly = 1) { blinkEffectJobManager.startBlinking(scope = any(), onVisibilityChanged = any()) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun restoreStopwatch_whenPausedWithZeroOrNegativeTime_shouldUpdateToInitialStateWithoutBlinking() = runTest {
        // Arrange
        val restoredPausedState = StopwatchModel(elapsedTime = 0L, isRunning = false)
        val restoredStopwatchUiModel = StopwatchUiModel(isRunning = false)

        coEvery { stopwatchUseCase.getSavedStopwatchState() } returns Result.Success(restoredPausedState)
        every { stopwatchUiMapper.mapToUiModel(restoredPausedState) } returns restoredStopwatchUiModel

        viewModel.uiEffect.test {
            // Act
            viewModel.handleEvent(StopwatchEvent.RestoreStopwatch)
            advanceUntilIdle()

            // Assert
            assertEquals(stopwatchUiMapper.mapToUiModel(restoredPausedState), viewModel.uiState.value)
            cancelAndIgnoreRemainingEvents()

            coVerify(exactly = 1) { stopwatchUseCase.getSavedStopwatchState() }
            verify(exactly = 1) { stopwatchStateManager.restoreStopwatchState(restoredPausedState) }
            verify(exactly = 1) { stopwatchUiMapper.mapToUiModel(restoredPausedState) }
            verify(exactly = 0) { blinkEffectJobManager.startBlinking(scope = any(), onVisibilityChanged = any()) }
        }
    }

    @Test
    fun restoreStopwatch_whenRestoreFails_shouldEmitErrorAndResetToInitialState() = runTest {
        // Arrange
        val errorMessage = "Failed to restore"
        coEvery { stopwatchUseCase.getSavedStopwatchState() } returns Result.Error(Exception("db error"))
        every { resourceProvider.getString(R.string.failed_to_restore_stopwatch_state) } returns errorMessage

        viewModel.uiEffect.test {
            // Act
            viewModel.handleEvent(StopwatchEvent.RestoreStopwatch)
            advanceUntilIdle()

            val newEffect = awaitItem()
            assertEquals(StopwatchEffect.ShowError(errorMessage), newEffect)
            assertEquals(errorMessage, (newEffect as StopwatchEffect.ShowError).message)
            assertEquals(StopwatchUiModel(), viewModel.uiState.value)

            coVerify(exactly = 1) { stopwatchUseCase.getSavedStopwatchState() }
            verify(exactly = 0) { stopwatchStateManager.restoreStopwatchState(any()) }
            expectNoEvents()
        }
    }



    // ================================================================================================
    // Start / Pause Toggle Tests
    // ================================================================================================

    @Test
    fun toggleRunState_whenPermissionNotGrantedAndNotRequested_shouldRequestNotificationPermission() =
        runTest {
            // Arrange
            every { permissionManager.isPostNotificationPermissionGranted() } returns false

            viewModel.uiEffect.test {
                // Act
                viewModel.handleEvent(StopwatchEvent.ToggleRunState)

                // Assert
                coVerify(exactly = 0) {
                    stopwatchUseCase.startStopwatch(any())
                    stopwatchUseCase.pauseStopwatch(any())
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun toggleRunState_whenPermissionGrantedAndPaused_shouldStartStopwatch() = runTest {
            // Arrange
            val pausedState = StopwatchModel(isRunning = false)
            val startedState = pausedState.copy(isRunning = true)
            every { permissionManager.isPostNotificationPermissionGranted() } returns true
            every { stopwatchStateManager.getCurrentStopwatchState() } returns pausedState
            coEvery { stopwatchUseCase.startStopwatch(pausedState) } returns Result.Success(startedState)

            // Act
            viewModel.handleEvent(StopwatchEvent.ToggleRunState)
            advanceUntilIdle()

            // Assert
            verify(exactly = 2) { stopwatchStateManager.getCurrentStopwatchState() }
            coVerify(exactly = 1) { stopwatchUseCase.startStopwatch(pausedState) }
            verify(exactly = 1) { stopwatchTickerJobManager.start(any(), any(), any()) }
        }

    @Test
    fun toggleRunState_whenPermissionNotGrantedButPreviouslyRequested_shouldStartStopwatchAndBlink() =
         runTest {
            // Arrange
            val pausedState = StopwatchModel(isRunning = false)
            val startedState = pausedState.copy(isRunning = true)

            coEvery { stopwatchUseCase.startStopwatch(pausedState) } returns Result.Success(startedState)
            every { permissionManager.isPostNotificationPermissionGranted() } returns false
            every { stopwatchStateManager.getCurrentStopwatchState() } returns pausedState

            viewModel.uiEffect.test {
                // Act
                viewModel.handleEvent(StopwatchEvent.ToggleRunState)
                advanceUntilIdle()

                // Assert
                assert(awaitItem() is StopwatchEffect.BlinkVisibilityChanged)
                coVerify(exactly = 1) { stopwatchUseCase.startStopwatch(pausedState) }
                verify(exactly = 1) { stopwatchTickerJobManager.start(any(), any(), any()) }
            }
        }

    @Test
    fun toggleRunState_whenStartFails_shouldEmitErrorAndStopBlinking() = runTest {
        // Arrange
        val pausedState = StopwatchModel(isRunning = false)
        val errorMessage = "Failed to start the stopwatch"

        every { permissionManager.isPostNotificationPermissionGranted() } returns true
        every { stopwatchStateManager.getCurrentStopwatchState() } returns pausedState
        coEvery { stopwatchUseCase.startStopwatch(pausedState) } returns Result.Error(Exception(errorMessage))
        every { blinkEffectJobManager.stopBlinking() } just Runs
        every { resourceProvider.getString(R.string.failed_to_start_stopwatch) } returns errorMessage

        viewModel.uiEffect.test {
            // Act
            viewModel.handleEvent(StopwatchEvent.ToggleRunState)
            advanceUntilIdle()

            // Assert
            val blinkEffect = awaitItem()
            val errorEffect = awaitItem()

            assert(blinkEffect is StopwatchEffect.BlinkVisibilityChanged)
            assertEquals(StopwatchEffect.ShowError(errorMessage), errorEffect)
            assertEquals(errorMessage, (errorEffect as StopwatchEffect.ShowError).message)

            coVerify(exactly = 1) { blinkEffectJobManager.stopBlinking() }
            verify(exactly = 2) { stopwatchStateManager.getCurrentStopwatchState() }
            coVerify(exactly = 1) { stopwatchUseCase.startStopwatch(pausedState) }
            verify(exactly = 0) { stopwatchTickerJobManager.start(any(), any(), any()) }
        }
    }

    @Test
    fun toggleRunState_whenPauseFails_shouldEmitError() = runTest {
        // Arrange
        val startedState = StopwatchModel(isRunning = true)
        val errorMessage = "Failed to pause the stopwatch"

        every { permissionManager.isPostNotificationPermissionGranted() } returns true
        every { stopwatchStateManager.getCurrentStopwatchState() } returns startedState
        every { resourceProvider.getString(R.string.failed_to_pause_stopwatch_state) } returns errorMessage
        coEvery { stopwatchUseCase.startStopwatch(startedState) } returns Result.Error(Exception(errorMessage))

        viewModel.uiEffect.test {
            // Act
            viewModel.handleEvent(StopwatchEvent.ToggleRunState)
            advanceUntilIdle()

            // Assert
            val effect = awaitItem()
            assertEquals(StopwatchEffect.ShowError(errorMessage), effect)
            assertEquals(errorMessage, (effect as StopwatchEffect.ShowError).message)
            coVerify(exactly = 2) { stopwatchStateManager.getCurrentStopwatchState() }
            coVerify(exactly = 1) { stopwatchUseCase.pauseStopwatch(startedState) }
            verify(exactly = 1) { stopwatchTickerJobManager.stop() }
        }
    }



    // ================================================================================================
    // Reset Tests
    // ================================================================================================

    @Test
    fun resetStopwatch_whenRunning_shouldStopJobsDeleteStateAndResetToInitial() = runTest {
        // Arrange
        val startedState = StopwatchModel(isRunning = true)
        val initialState = StopwatchModel()

        every { stopwatchStateManager.getCurrentStopwatchState() } returns startedState
        coEvery { stopwatchUseCase.deleteStopWatchById(startedState) } returns Result.Success(Unit)
        every { blinkEffectJobManager.stopBlinking() } just runs
        every { stopwatchTickerJobManager.stop() } just runs
        every { stopwatchStateManager.updateStopwatchState(initialState) } just runs

        viewModel.uiEffect.test {
            // Act
            viewModel.handleEvent(StopwatchEvent.ResetStopwatch)
            advanceUntilIdle()

            // Assert
            assert(awaitItem() is StopwatchEffect.BlinkVisibilityChanged)
            verify(exactly = 1) { blinkEffectJobManager.stopBlinking() }
            verify(exactly = 1) { stopwatchTickerJobManager.stop() }
            verify(exactly = 1) { stopwatchStateManager.getCurrentStopwatchState() }
            coVerify(exactly = 1) { stopwatchUseCase.deleteStopWatchById(startedState) }
            verify(exactly = 1) { stopwatchStateManager.updateStopwatchState(initialState) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun resetStopwatch_whenPaused_shouldStopJobsDeleteStateAndResetToInitial() = runTest {
        // Arrange
        val pausedState = StopwatchModel(isRunning = false)
        val initialState = StopwatchModel()

        every { stopwatchStateManager.getCurrentStopwatchState() } returns pausedState
        coEvery { stopwatchUseCase.deleteStopWatchById(pausedState) } returns Result.Success(Unit)
        every { blinkEffectJobManager.stopBlinking() } just runs
        every { stopwatchTickerJobManager.stop() } just runs
        every { stopwatchStateManager.updateStopwatchState(initialState) } just runs

        viewModel.uiEffect.test {
            // Act
            viewModel.handleEvent(StopwatchEvent.ResetStopwatch)
            advanceUntilIdle()

            // Assert
            assert(awaitItem() is StopwatchEffect.BlinkVisibilityChanged)
            verify(exactly = 1) { blinkEffectJobManager.stopBlinking() }
            verify(exactly = 1) { stopwatchTickerJobManager.stop() }
            verify(exactly = 1) { stopwatchStateManager.getCurrentStopwatchState() }
            coVerify(exactly = 1) { stopwatchUseCase.deleteStopWatchById(pausedState) }
            verify(exactly = 1) { stopwatchStateManager.updateStopwatchState(initialState) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun resetStopwatch_whenDeletionFails_shouldEmitErrorAndStillStopJobs() = runTest {

        // Arrange
        val startedState = StopwatchModel(elapsedTime = 5000L, isRunning = true)
        val errorMessage = "Failed To Reset Stopwatch"

        every { stopwatchStateManager.getCurrentStopwatchState() } returns startedState
        coEvery { stopwatchUseCase.deleteStopWatchById(startedState) } returns Result.Error(Exception())
        every { resourceProvider.getString(R.string.failed_to_reset_stopwatch_state) } returns errorMessage
        every { blinkEffectJobManager.stopBlinking() } just runs
        every { stopwatchTickerJobManager.stop() } just runs

        viewModel.uiEffect.test {
            // Act
            viewModel.handleEvent(StopwatchEvent.ResetStopwatch)
            advanceUntilIdle()

            // Assert
            val blinkEffect = awaitItem()
            val errorEffect = awaitItem()

            assert(blinkEffect is StopwatchEffect.BlinkVisibilityChanged)
            assertEquals(StopwatchEffect.ShowError(errorMessage), errorEffect)
            assertEquals(errorMessage, (errorEffect as StopwatchEffect.ShowError).message)

            verify(exactly = 1) { blinkEffectJobManager.stopBlinking() }
            verify(exactly = 1) { stopwatchTickerJobManager.stop() }
            verify(exactly = 1) { stopwatchStateManager.getCurrentStopwatchState() }
            coVerify(exactly = 1) { stopwatchUseCase.deleteStopWatchById(startedState) }
            cancelAndIgnoreRemainingEvents()
        }
    }



    // =============================================================================================
    // Record Lap
    // =============================================================================================

    @Test
    fun recordLap_whenRunning_shouldSaveLapAndUpdateState() =
        runTest {


            // Arrange
            val currentModel = StopwatchModel(
                elapsedTime = 10000L,
                isRunning = true,
                lapTimes = listOf(
                    StopWatchLapModel(
                        lapIndex = 1,
                        lapStartTime = 7000L,
                        lapElapsedTime = 3000L,
                        lapEndTime = 10000L
                    )
                )
            )
            val updatedStopwatchModel = currentModel.copy(
                lapCount = 1,
                lapTimes = listOf(
                    StopWatchLapModel(
                        lapIndex = 1,
                        lapStartTime = 7000L,
                        lapElapsedTime = 3000L,
                        lapEndTime = 10000L
                    )
                )
            )

            every { stopwatchStateManager.getCurrentStopwatchState() } returns currentModel
            coEvery { stopwatchUseCase.lapStopwatch(currentModel) } returns Result.Success(updatedStopwatchModel)
            every { stopwatchStateManager.updateStopwatchState(updatedStopwatchModel) } just runs

            // Act
            viewModel.handleEvent(StopwatchEvent.RecordStopwatchLap)
            advanceUntilIdle()

            // Assert
            coVerify(exactly = 1) { stopwatchUseCase.lapStopwatch(currentModel) }
            verify(exactly = 1) { stopwatchStateManager.updateStopwatchState(updatedStopwatchModel) }
        }

    @Test
    fun recordLap_whenLapUseCaseFails_shouldEmitError() = runTest {

        // Arrange
        val errorMessage = "Failed To Record Stopwatch Lap"
        val currentModel = StopwatchModel(
            elapsedTime = 10000L,
            isRunning = true,
            lapTimes = listOf(
                StopWatchLapModel(
                    lapIndex = 1,
                    lapStartTime = 7000L,
                    lapElapsedTime = 3000L,
                    lapEndTime = 30000L
                )
            )
        )

        every { stopwatchStateManager.getCurrentStopwatchState() } returns currentModel
        coEvery { stopwatchUseCase.lapStopwatch(currentModel) } returns Result.Error(Exception(errorMessage))
        every { resourceProvider.getString(R.string.failed_to_record_lap_stopwatch_state) } returns errorMessage

        viewModel.uiEffect.test {

            // Act
            viewModel.handleEvent(StopwatchEvent.RecordStopwatchLap)
            advanceUntilIdle()

            // Assert
            val effect = awaitItem()
            assertEquals(StopwatchEffect.ShowError(errorMessage), effect)
            assertEquals(errorMessage, (effect as StopwatchEffect.ShowError).message)

            coVerify(exactly = 1) { stopwatchUseCase.lapStopwatch(currentModel) }
            verify(exactly = 1) { resourceProvider.getString(R.string.failed_to_record_lap_stopwatch_state) }
        }

    }
*/

}
