package com.example.smartalarm.feature.stopwatch.data.manager

import app.cash.turbine.test
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test

class StopwatchInMemoryStateManagerTest {

    private lateinit var stateManager: StopwatchInMemoryStateManagerImpl

    @Before
    fun setup() {
        stateManager = StopwatchInMemoryStateManagerImpl()
    }

    @Test
    fun `initial state should be default StopwatchModel`() {
        val current = stateManager.getCurrentState()
        assertThat(current.isRunning).isFalse()
        assertThat(current.elapsedTime).isEqualTo(0L)
        assertThat(current.lapTimes).isEmpty()
    }

    @Test
    fun `updateFromDatabase should override current memory state regardless of status`() {
        val dbModel = StopwatchModel(elapsedTime = 5000L, isRunning = false)

        stateManager.updateFromDatabase(dbModel)

        assertThat(stateManager.getCurrentState().elapsedTime).isEqualTo(5000L)
    }

    @Test
    fun `updateFromTicker should update state when stopwatch is running`() {
        // Setup: Set state to running first via database sync or initial logic
        val runningState = StopwatchModel(isRunning = true, elapsedTime = 1000L)
        stateManager.updateFromDatabase(runningState)

        // Action: Ticker sends an update
        val tickerUpdate = StopwatchModel(isRunning = true, elapsedTime = 1100L)
        stateManager.updateFromTicker(tickerUpdate)

        // Assert
        assertThat(stateManager.getCurrentState().elapsedTime).isEqualTo(1100L)
    }

    @Test
    fun `updateFromTicker should ignore updates when stopwatch is NOT running`() {
        // Setup: State is paused/stopped
        val pausedState = StopwatchModel(isRunning = false, elapsedTime = 1000L)
        stateManager.updateFromDatabase(pausedState)

        // Action: Ticker attempts to send an update (e.g. a delayed pulse)
        val tickerUpdate = StopwatchModel(isRunning = false, elapsedTime = 1500L)
        stateManager.updateFromTicker(tickerUpdate)

        // Assert: Value remains 1000L
        assertThat(stateManager.getCurrentState().elapsedTime).isEqualTo(1000L)
    }

    @Test
    fun `state Flow should emit new values when updated`() = runTest {
        // Using Turbine to test the Flow stream
        stateManager.state.test {
            // 1. Initial emission
            assertThat(awaitItem().elapsedTime).isEqualTo(0L)

            // 2. Trigger update
            val newState = StopwatchModel(elapsedTime = 99L)
            stateManager.updateFromDatabase(newState)

            // 3. Verify emission
            assertThat(awaitItem().elapsedTime).isEqualTo(99L)

            cancelAndIgnoreRemainingEvents()
        }
    }
}