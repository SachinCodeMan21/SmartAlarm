package com.example.smartalarm.feature.timer.presentation.viewmodel

import com.example.smartalarm.feature.timer.framework.jobmanager.contract.ShowTimerJobManager
import com.example.smartalarm.feature.timer.domain.manager.ShowTimerStateManager
import com.example.smartalarm.feature.timer.utility.TimerRingtonePlayer
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import com.example.smartalarm.feature.timer.presentation.uistate.ShowTimerUiState
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ShowTimerViewModelTest {

/*
    @MockK
    private lateinit var timerUseCase: TimerUseCasesFacade

    @MockK
    private lateinit var timerStateManager: ShowTimerStateManager

    @MockK
    private lateinit var jobManager: ShowTimerJobManager

    @RelaxedMockK
    private lateinit var timerRingtoneHelper: TimerRingtonePlayer

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: ShowTimerViewModel


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Default behavior
        every { timerStateManager.getTimers() } returns emptyList()
        every { timerStateManager.getTimersFlow() } returns MutableStateFlow(emptyList())

        viewModel = ShowTimerViewModel(
            timerUseCase = timerUseCase,
            timerStateManager = timerStateManager,
            jobManager = jobManager,
            timerRingtoneHelper = timerRingtoneHelper,
            defaultDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    // ------------------------------------------------------------------------
    // Initial State
    // ------------------------------------------------------------------------
    @Test
    fun `initial uiState is Loading`() = runTest {
        assertEquals(ShowTimerUiState.Loading, viewModel.uiState.value)
    }

*/

}
