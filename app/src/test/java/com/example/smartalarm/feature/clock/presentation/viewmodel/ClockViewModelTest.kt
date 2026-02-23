package com.example.smartalarm.feature.clock.presentation.viewmodel

import app.cash.turbine.test
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.domain.usecase.contract.ClockUseCases
import com.example.smartalarm.feature.clock.framework.jobmanager.contract.ClockUpdaterJob
import com.example.smartalarm.feature.clock.presentation.event.ClockEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import  com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.clock.domain.model.ClockModel
import com.example.smartalarm.feature.clock.presentation.effect.ClockEffect
import io.mockk.MockKAnnotations
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import kotlin.test.Test

@ExperimentalCoroutinesApi
class ClockViewModelTest {

/*    private lateinit var viewModel: ClockViewModel

    @MockK
    private lateinit var clockUseCases: ClockUseCases

    @MockK(relaxed = true)
    private lateinit var clockUpdaterJob: ClockUpdaterJob

    private val testDispatcher = StandardTestDispatcher()

    // Shared test data
    private val placeNY = PlaceModel(
        id = 1,
        fullName = "America/New_York",
        primaryName = "NY",
        timeZoneId = "America/New_York",
        offsetSeconds = -14400,
        currentTime = "12:00 AM"
    )

    private val placeLondon = placeNY.copy(id = 2, fullName = "Europe/London")

    private fun uiModelWith(vararg places: PlaceModel) = ClockModel(
        formattedTime = "10:00 AM",
        formattedDate = "Oct 18, 2025",
        savedPlaces = places.toList()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        viewModel = ClockViewModel(clockUseCases, clockUpdaterJob, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun initViewModelWithUiModel(model: ClockModel) {
        viewModel = ClockViewModel(clockUseCases, clockUpdaterJob, testDispatcher)
        //viewModel.setInitialUiModelForTesting(model)
    }

    // Test cases follow...

    @Test
    fun `onEvent LoadSelectedTimeZones - success - starts clock updater job`() = runTest {
        coEvery { clockUseCases.getAllSavedPlaces() } returns Result.Success(listOf(placeNY))

        viewModel.onEvent(ClockEvent.LoadSelectedTimeZones)
        advanceUntilIdle()

        coVerify { clockUpdaterJob.startClockUpdaterJob(any(), listOf(placeNY), any(), any()) }
    }

    @Test
    fun `loadTimeZones - error - emits toast effect`() = runTest {
        coEvery { clockUseCases.getAllSavedPlaces() } returns Result.Error(Exception("DB error"))

        viewModel.uiEffect.test {
            viewModel.onEvent(ClockEvent.LoadSelectedTimeZones)
            advanceUntilIdle()

            assertEquals(ClockEffect.ShowToast("Error: DB error"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `startClockUpdates - onUpdate updates uiModel_, onError posts effect`() = runTest {
        val places = listOf(placeNY)

        val onUpdateSlot = slot<(List<PlaceModel>, String, String) -> Unit>()
        val onErrorSlot = slot<(Throwable) -> Unit>()

        coEvery { clockUseCases.getAllSavedPlaces() } returns Result.Success(places)
        coEvery {
            clockUpdaterJob.startClockUpdaterJob(any(), places, capture(onUpdateSlot), capture(onErrorSlot))
        } returns Unit

        viewModel.onEvent(ClockEvent.LoadSelectedTimeZones)
        advanceUntilIdle()

        val updatedPlaces = listOf(placeNY.copy(currentTime = "1:00 AM"))
        onUpdateSlot.captured.invoke(updatedPlaces, "01:00 AM", "Oct 18, 2025")

        assertEquals(
            ClockModel("01:00 AM", "Oct 18, 2025", updatedPlaces),
            viewModel.uiModel.value
        )

        val error = Exception("Test error")
        onErrorSlot.captured.invoke(error)

        viewModel.uiEffect.test {
            assertEquals(ClockEffect.ShowToast("Error: Test error"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteTimeZone - success - removes place from UI and emits DeleteTimeZone effect`() = runTest {
        val initialModel = uiModelWith(placeNY, placeLondon)
        initViewModelWithUiModel(initialModel)

        coEvery { clockUseCases.deletePlaceById(placeNY.id) } returns Result.Success(Unit)

        viewModel.onEvent(ClockEvent.DeleteTimeZone(placeNY))
        advanceUntilIdle()

        assertEquals(listOf(placeLondon), viewModel.uiModel.value.savedPlaces)

        viewModel.uiEffect.test {
            assertEquals(ClockEffect.DeleteTimeZone(placeNY), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteTimeZone - error - emits ShowToast effect`() = runTest {
        coEvery { clockUseCases.deletePlaceById(placeNY.id) } returns Result.Error(Exception("DB error"))

        viewModel.onEvent(ClockEvent.DeleteTimeZone(placeNY))
        advanceUntilIdle()

        viewModel.uiEffect.test {
            assertEquals(ClockEffect.ShowToast("Delete failed: DB error"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `undoDeletedTimeZone - success - restores place and emits success toast`() = runTest {
        val initialModel = uiModelWith(placeLondon)
        initViewModelWithUiModel(initialModel)

        coEvery { clockUseCases.insertPlace(placeNY) } returns Result.Success(Unit)

        viewModel.onEvent(ClockEvent.UndoDeletedTimeZone(placeNY))
        advanceUntilIdle()

        assertEquals(listOf(placeLondon, placeNY), viewModel.uiModel.value.savedPlaces)

        viewModel.uiEffect.test {
            assertEquals(ClockEffect.ShowToast("Undo successful"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `undoDeletedTimeZone - error - emits undo failed toast`() = runTest {
        val initialModel = uiModelWith()
        initViewModelWithUiModel(initialModel)

        coEvery { clockUseCases.insertPlace(placeNY) } returns Result.Error(Exception("Insert failed"))

        viewModel.onEvent(ClockEvent.UndoDeletedTimeZone(placeNY))
        advanceUntilIdle()

        viewModel.uiEffect.test {
            assertEquals(ClockEffect.ShowToast("Undo failed: Insert failed"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `AddNewTimeZone event - emits navigation effect`() = runTest {
        viewModel.uiEffect.test {
            viewModel.onEvent(ClockEvent.AddNewTimeZone)
            advanceUntilIdle()

            assertEquals(ClockEffect.NavigateToAddTimeZoneActivity, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ShowToastMessage event - emits correct toast`() = runTest {
        viewModel.uiEffect.test {
            viewModel.onEvent(ClockEvent.ShowToastMessage("Hello!"))
            advanceUntilIdle()

            assertEquals(ClockEffect.ShowToast("Hello!"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }*/
}

