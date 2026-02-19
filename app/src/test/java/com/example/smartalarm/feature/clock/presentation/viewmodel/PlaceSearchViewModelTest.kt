package com.example.smartalarm.feature.clock.presentation.viewmodel

import app.cash.turbine.test
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.domain.usecase.contract.ClockUseCases
import com.example.smartalarm.feature.clock.domain.usecase.contract.PlaceSearchUseCases
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.clock.presentation.effect.PlaceSearchEffect
import com.example.smartalarm.feature.clock.presentation.event.PlaceSearchEvent
import com.example.smartalarm.feature.clock.presentation.uiState.PlaceSearchUiState
import kotlinx.coroutines.test.advanceTimeBy
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import java.io.IOException
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalCoroutinesApi
class PlaceSearchViewModelTest {

    @MockK
    private lateinit var clockUseCases: ClockUseCases

    @MockK
    private lateinit var placeSearchUseCases: PlaceSearchUseCases

    @MockK
    private lateinit var viewModel: PlaceSearchViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        viewModel = PlaceSearchViewModel(
            clockUseCases = clockUseCases,
            placeSearchUseCases = placeSearchUseCases,
            ioDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `handleEvent - QueryChanged - emits loading and success state`() = runTest {
        // Given
        val query = "New York"
        val predictions = listOf(
            PlaceModel(
            id = 1,
            fullName = "America/New_York",
            primaryName = "New York",
            timeZoneId = "America/New_York",
            offsetSeconds = -14400,
            currentTime = "12:00 AM"
        ))

        coEvery { placeSearchUseCases.getPlacePredictions(query) } returns Result.Success(predictions)

        // When
        viewModel.handleEvent(PlaceSearchEvent.QueryChanged(query))

        // Then
        viewModel.uiState.test {
            assertEquals(PlaceSearchUiState.Initial, awaitItem())
            assertEquals(PlaceSearchUiState.Loading, awaitItem())
            advanceTimeBy(1000)
            assertEquals(PlaceSearchUiState.Success(predictions), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `handleEvent - QueryChanged - emits error and snackBar effect`() = runTest {
        // Given
        val query = "ErrorCity"
        val exception = IOException("No connection")

        coEvery { placeSearchUseCases.getPlacePredictions(query) } returns Result.Error(exception)

        // When
        viewModel.handleEvent(PlaceSearchEvent.QueryChanged(query))

        // Then
        viewModel.uiState.test {
            assertEquals(PlaceSearchUiState.Initial, awaitItem())
            assertEquals(PlaceSearchUiState.Loading, awaitItem())
            advanceTimeBy(1000)
            assertEquals(PlaceSearchUiState.Error, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.uiEffect.test {
            assertEquals(PlaceSearchEffect.ShowSnackBarMessage("No internet connection"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `handleEvent - PlaceSelected - emits NavigateToHome on success`() = runTest {
        // Given
        val place =   PlaceModel(
            id = 1,
            fullName = "America/New_York",
            primaryName = "NY",
            timeZoneId = "America/New_York",
            offsetSeconds = -14400,
            currentTime = "12:00 AM"
        )
        coEvery { clockUseCases.insertPlace(place) } returns Result.Success(Unit)

        // When
        viewModel.handleEvent(PlaceSearchEvent.PlaceSelected(place))

        // Then
        viewModel.uiEffect.test {
            assertEquals(PlaceSearchEffect.NavigateToHome, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `handleEvent - PlaceSelected - emits error snackBar on failure`() = runTest {
        val place =   PlaceModel(
            id = 2,
            fullName = "Europe/London",
            primaryName = "London",
            timeZoneId = "Europe/London",
            offsetSeconds = -14400,
            currentTime = "12:00 AM"
        )
        coEvery { clockUseCases.insertPlace(place) } returns Result.Error(Exception("DB error"))

        viewModel.handleEvent(PlaceSearchEvent.PlaceSelected(place))

        viewModel.uiEffect.test {
            assertEquals(PlaceSearchEffect.ShowSnackBarMessage("Failed to save place"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `handleEvent - NavigateBack - emits Finish effect`() = runTest {
        viewModel.handleEvent(PlaceSearchEvent.NavigateBack)

        viewModel.uiEffect.test {
            assertEquals(PlaceSearchEffect.Finish, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `handleEvent - ShowSnackBarMessage - emits message`() = runTest {
        val message = "Test Message"
        viewModel.handleEvent(PlaceSearchEvent.ShowSnackBarMessage(message))

        viewModel.uiEffect.test {
            assertEquals(PlaceSearchEffect.ShowSnackBarMessage(message), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
