package com.example.smartalarm.feature.stopwatch.data.repository

import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.stopwatch.data.datasource.contract.StopwatchLocalDataSource
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchLapEntity
import com.example.smartalarm.feature.stopwatch.data.local.relation.StopWatchWithLaps
import com.example.smartalarm.feature.stopwatch.domain.model.StopWatchLapModel
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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
class StopWatchRepositoryImplTest {
/*

    // The repository and its local data source are initialized here
    private lateinit var localDataSource: StopwatchLocalDataSource
    private lateinit var repository: StopWatchRepositoryImpl


    // Set up the mocks and repository before each test
    @Before
    fun setup() {
        localDataSource = mockk()
        repository = StopWatchRepositoryImpl(localDataSource)
    }

    // Clear all mocks after each test
    @After
    fun tearDown() {
        clearAllMocks()
    }



    //====================================================
    // GetStopwatch Test Methods Scenarios
    //====================================================

    @Test
    fun getStopwatchById_whenNotFound_shouldReturn_successWithNull() = runTest {

        // Arrange
        coEvery { localDataSource.getStopwatchWithLaps(any()) } returns null

        // Act
        val result = repository.getStopwatchById(99)

        // Assert
        assertTrue(result is Result.Success)
        assertNull((result as Result.Success).data)
        coVerify(exactly = 1) { localDataSource.getStopwatchWithLaps(any()) }
    }

    @Test
    fun getStopwatchById_whenDataSourceThrows_shouldReturn_error() = runTest {

        // Arrange
        coEvery { localDataSource.getStopwatchWithLaps(any()) } throws RuntimeException()

        // Act
        val result = repository.getStopwatchById(1)

        // Assert
        assertTrue(result is Result.Error)
        coVerify(exactly = 1) { localDataSource.getStopwatchWithLaps(any()) }
    }

    @Test
    fun getStopwatchById_whenFound_shouldReturnSuccessWithMappedDomainModel() = runTest {

        // Arrange
        val stopwatchWithLaps = StopWatchWithLaps(getStopwatchEntity(), getStopwatchLapEntity())
        val expectedStopwatch = StopwatchModel(
            id = 1,
            startTime = 100L,
            elapsedTime = 500L,
            endTime = 500L,
            isRunning = false,
            lapCount = 2,
            lapTimes = listOf(
                StopWatchLapModel(lapIndex = 1, lapStartTime = 0L, lapElapsedTime = 200L, lapEndTime = 200L),
                StopWatchLapModel(lapIndex = 2, lapStartTime = 200L, lapElapsedTime = 500L, lapEndTime = 500L)
            )
        )

        coEvery { localDataSource.getStopwatchWithLaps(any()) } returns stopwatchWithLaps

        // Act
        val result = repository.getStopwatchById(1)

        // Assert
        val data = (result as? Result.Success)?.data

        assertTrue(result is Result.Success)
        assertNotNull(data)
        assertEquals(expectedStopwatch, data)

        coVerify(exactly = 1) { localDataSource.getStopwatchWithLaps(any()) }

    }



    //====================================================
    // SaveStopwatch Test Methods Scenarios
    //====================================================

    @Test
    fun saveStopwatch_whenDataSourceThrows_shouldReturn_error() = runTest {

        // Arrange
        coEvery { localDataSource.saveStopwatchWithLaps(any(), any()) } throws RuntimeException()

        // Act
        val result = repository.saveStopwatch(StopwatchModel())

        // Assert
        assertTrue(result is Result.Error)
        coVerify(exactly = 1) {
            localDataSource.saveStopwatchWithLaps(any(), any())
        }

    }

    @Test
    fun saveStopwatch_withEmptyLapsWhenSuccessful_shouldReturnSuccess() = runTest {

        // Arrange
        coEvery { localDataSource.saveStopwatchWithLaps(any(), any()) } just Runs

        // Act
        val result = repository.saveStopwatch(StopwatchModel())

        // Assert
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { localDataSource.saveStopwatchWithLaps(any(), any()) }
    }

    @Test
    fun saveStopwatch_withNonEmptyLapsWhenSuccessful_shouldReturnSuccess() = runTest {

        // Arrange
        val stopwatchModel = StopwatchModel(
            id = 1,
            startTime = 100L,
            elapsedTime = 500L,
            endTime = 500L,
            isRunning = false,
            lapTimes = listOf(
                StopWatchLapModel(lapIndex = 1, lapStartTime = 0L, lapElapsedTime = 200L, lapEndTime = 200L),
                StopWatchLapModel(lapIndex = 2, lapStartTime = 200L, lapElapsedTime = 500L, lapEndTime = 500L)
            )
        )

        coEvery { localDataSource.saveStopwatchWithLaps(any(), any()) } just Runs

        // Act
        val result = repository.saveStopwatch(stopwatchModel)

        // Assert
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { localDataSource.saveStopwatchWithLaps(any(), any()) }
    }



    //====================================================
    // Delete Stopwatch Test Methods Scenarios
    //====================================================

    @Test
    fun deleteStopwatchById_whenDataSourceThrows_shouldReturnError() = runTest {

        // Arrange
        val exception = RuntimeException("DB failure")
        coEvery { localDataSource.deleteStopwatchWithLaps(any()) } throws exception

        // Act
        val result = repository.deleteStopwatch(1)

        // Assert
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
        coVerify(exactly = 1) { localDataSource.deleteStopwatchWithLaps(any()) }
    }

    @Test
    fun deleteStopwatchById_whenIdDoesNotExist_shouldReturnSuccess() = runTest {

        // Arrange
        coEvery { localDataSource.deleteStopwatchWithLaps(any()) } just Runs

        // Act
        val result = repository.deleteStopwatch(99)

        // Assert
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { localDataSource.deleteStopwatchWithLaps(any()) }
    }

    @Test
    fun deleteStopwatchById_whenSuccessful_shouldReturnSuccess() = runTest {

        // Arrange
        coEvery { localDataSource.deleteStopwatchWithLaps(any()) } just Runs

        // Act
        val result = repository.deleteStopwatch(1)

        // Assert
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) { localDataSource.deleteStopwatchWithLaps(any()) }
    }



    //====================================================
    // Helper Methods
    //====================================================

    private fun getStopwatchEntity() : StopWatchEntity{
        return StopWatchEntity(
            id = 1,
            startTime = 100L,
            elapsedTime = 500L,
            endTime = 500L,
            isRunning = false,
            lapCount = 2
        )
    }
    private fun getStopwatchLapEntity() : List<StopWatchLapEntity>{
        return listOf(
            StopWatchLapEntity(
                stopwatchId = 1,
                lapIndexId = 1,
                lapStartTime = 0L,
                lapElapsedTime = 200L,
                lapEndTime = 200L
            ),
            StopWatchLapEntity(
                stopwatchId = 1,
                lapIndexId = 2,
                lapStartTime = 200L,
                lapElapsedTime = 500L,
                lapEndTime = 500L
            )
        )
    }
*/


}
