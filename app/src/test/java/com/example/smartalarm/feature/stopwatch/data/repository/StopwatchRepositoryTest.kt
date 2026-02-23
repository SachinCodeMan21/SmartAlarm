package com.example.smartalarm.feature.stopwatch.data.repository

import android.database.sqlite.SQLiteException
import com.example.smartalarm.core.exception.AppError
import com.example.smartalarm.feature.stopwatch.data.datasource.contract.StopwatchLocalDataSource
import com.example.smartalarm.feature.stopwatch.data.manager.StopwatchInMemoryStateManager
import com.example.smartalarm.feature.stopwatch.data.mapper.StopwatchMapper.toEntity
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import com.example.smartalarm.core.model.Result


/**
 * Unit tests for the `StopWatchRepositoryImpl` class.
 *
 * This test class verifies the functionality of the `StopWatchRepositoryImpl` by testing various methods
 * related to saving, retrieving, and deleting stopwatches. It uses a mock `StopwatchLocalDataSource` to
 * simulate interactions with the local data source and tests the repository's behavior in different scenarios,
 * including:
 * - Successfully saving a stopwatch and laps
 * - Handling errors when saving a stopwatch
 * - Retrieving a stopwatch by ID, both when found and not found
 * - Handling errors when retrieving a stopwatch
 * - Deleting a stopwatch by ID and handling errors during deletion
 *
 * Each test case ensures that the repository correctly delegates operations to the local data source and
 * properly handles success and error cases by returning appropriate `Result` types.
 *
 * The test data is generated using mock data factory methods, which simulate the stopwatch model,
 * entities, and laps.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StopwatchRepositoryTest {

    private lateinit var repository: StopwatchRepositoryImpl

    // Mocks for dependencies
    private lateinit var localDataSource: StopwatchLocalDataSource
    private lateinit var inMemoryStateManager: StopwatchInMemoryStateManager

    @Before
    fun setUp() {
        // Initialize mocks using MockK
        localDataSource = mockk(relaxed = true)
        inMemoryStateManager = mockk(relaxed = true)

        // Initialize the repository
        repository = StopwatchRepositoryImpl(localDataSource, inMemoryStateManager)
    }

    @Test
    fun `test persistStopwatch success`() = runTest {
        // Arrange
        val stopwatch = StopwatchModel(
            isRunning = true,
            startTime = 100L,
            elapsedTime = 0L,
            lapTimes = emptyList(),
            lapCount = 0
        )
        val stateEntity = stopwatch.toEntity()
        val lapEntities = stopwatch.lapTimes.map { it.toEntity(stateEntity.id) }

        // Stub the localDataSource behavior
        coEvery { localDataSource.saveStopwatchWithLaps(stateEntity, lapEntities) } just Runs

        // Act
        val result = repository.persistStopwatch(stopwatch)

        // Assert
        assertTrue(result is Result.Success)
        coVerify { localDataSource.saveStopwatchWithLaps(stateEntity, lapEntities) }
    }

    @Test
    fun `test persistStopwatch failure due to exception`() = runTest {
        // Arrange
        val stopwatch = StopwatchModel(isRunning = true, startTime = 100L, elapsedTime = 0L, lapTimes = emptyList(), lapCount = 0)
        val stateEntity = stopwatch.toEntity()
        val lapEntities = stopwatch.lapTimes.map { it.toEntity(stateEntity.id) }

        // Simulate an exception thrown from localDataSource
        coEvery { localDataSource.saveStopwatchWithLaps(stateEntity, lapEntities) } throws SQLiteException("Database error")

        // Act
        val result = repository.persistStopwatch(stopwatch)

        // Assert
        assertTrue(result is Result.Error)
        // Expecting the database unavailable error type, not Unknown
        assertEquals((result as Result.Error).error, AppError.Database.Unavailable)
    }

    @Test
    fun `test deleteStopwatch success`() = runTest {
        // Arrange
        coEvery { localDataSource.deleteStopwatchSession() } just Runs

        // Act
        val result = repository.deleteStopwatch()

        // Assert
        assertTrue(result is Result.Success)
        coVerify { localDataSource.deleteStopwatchSession() }
    }

    @Test
    fun `test deleteStopwatch failure due to exception`() = runTest {
        // Arrange: Simulate a database error when attempting to delete the stopwatch
        coEvery { localDataSource.deleteStopwatchSession() } throws SQLiteException("Database error")

        // Act: Call the deleteStopwatch method
        val result = repository.deleteStopwatch()

        // Assert: The result should be an error of type AppError.Database.Unavailable
        assertTrue(result is Result.Error)
        assertEquals((result as Result.Error).error, AppError.Database.Unavailable)
    }

    @Test
    fun `test getCurrentStopwatchState returns in-memory state`() {
        // Arrange
        val expectedStopwatch = StopwatchModel(isRunning = false, startTime = 0L, elapsedTime = 0L, lapTimes = emptyList(), lapCount = 0)
        every { inMemoryStateManager.getCurrentState() } returns expectedStopwatch

        // Act
        val result = repository.getCurrentStopwatchState()

        // Assert
        assertEquals(expectedStopwatch, result)
        verify { inMemoryStateManager.getCurrentState() }
    }

    @Test
    fun `test updateTickerState calls in-memory state manager`() {
        // Arrange
        val updatedStopwatch = StopwatchModel(isRunning = true, startTime = 100L, elapsedTime = 0L, lapTimes = emptyList(), lapCount = 0)

        // Act
        repository.updateTickerState(updatedStopwatch)

        // Assert
        verify { inMemoryStateManager.updateFromTicker(updatedStopwatch) }
    }

}

