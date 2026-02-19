package com.example.smartalarm.feature.alarm.presentation.viewmodel.mission

import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.domain.model.MissionType
import com.example.smartalarm.feature.alarm.presentation.event.mission.TypingMissionEvent
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TypingMissionViewModelTest {

    @MockK
    private lateinit var mockResourceProvider: ResourceProvider

    private lateinit var testDispatcher: TestDispatcher

    private lateinit var viewModel: TypingMissionViewModel



    @Before
    fun setup() {
        MockKAnnotations.init(this)

        // Mocking the resource provider methods
        every { mockResourceProvider.getStringArray(any()) } returns arrayOf(
            "The quick brown fox", "jumps over the lazy dog"
        )
        every { mockResourceProvider.getString(any()) } returns "Test Feedback"

        // Create an instance of the ViewModel
        testDispatcher = StandardTestDispatcher()
        viewModel = TypingMissionViewModel(mockResourceProvider, testDispatcher)
    }


    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test start round`() {
        // Initialize mission first
        viewModel.handleEvent(TypingMissionEvent.InitializeMission(Mission(type = MissionType.Typing, rounds = 2, iconResId = 0)))

        // Start the first round
        viewModel.handleEvent(TypingMissionEvent.StartMission)

        // Verify state after starting round
        assertEquals("Round 1 of 2", viewModel.uiState.value.roundText)
        assertTrue(viewModel.uiState.value.isInputEnabled)
    }


}