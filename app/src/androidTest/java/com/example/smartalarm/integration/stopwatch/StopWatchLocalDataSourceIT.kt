package com.example.smartalarm.integration.stopwatch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartalarm.feature.stopwatch.data.datasource.contract.StopwatchLocalDataSource
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchLapEntity
import com.example.smartalarm.feature.stopwatch.data.local.relation.StopWatchWithLaps
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class StopWatchLocalDataSourceIT {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var stopwatchLocalDatasource: StopwatchLocalDataSource

    @Before
    fun setup() {
        hiltRule.inject()
    }


    // ================================================================================================
    // Get Stopwatch Integration Tests
    // ================================================================================================

    @Test
    fun getStopwatchWithLaps_whenStopwatchDoesNotExist_shouldReturnNull() = runTest {

        // Arrange: Empty database (no data inserted)

        // Act: Try to retrieve non-existent stopwatch
        val result = stopwatchLocalDatasource.getStopwatchWithLaps(999)

        // Assert: Should return null
        Assert.assertNull(result)
    }

    @Test
    fun getStopwatchWithLaps_whenNoLapsRecorded_shouldReturnStopwatchWithEmptyLaps() = runTest {

        // Arrange: Insert stopwatch without any laps
        val stopwatchEntity = createStopwatchEntity(id = 2, elapsedTime = 3000L, isRunning = false)
        val expected = StopWatchWithLaps(stopwatchEntity, emptyList())
        stopwatchLocalDatasource.saveStopwatchWithLaps(stopwatchEntity, emptyList())

        // Act
        val result = stopwatchLocalDatasource.getStopwatchWithLaps(2)

        // Assert
        Assert.assertNotNull(result)
        Assert.assertEquals(expected, result)
    }

    @Test
    fun getStopwatchWithLaps_whenLapsExist_shouldReturnStopwatchWithLaps() = runTest {

        // Arrange: Insert stopwatch and laps directly into database
        val stopwatchEntity = createStopwatchEntity(id = 1, elapsedTime = 10000L)
        val lapEntities = createSingleLapEntity()

        val expectedStopwatch = StopWatchWithLaps(stopwatchEntity, lapEntities)

        // Insert using DAO directly to set up test data
        stopwatchLocalDatasource.saveStopwatchWithLaps(stopwatchEntity, lapEntities)

        // Act: Retrieve using data source
        val result = stopwatchLocalDatasource.getStopwatchWithLaps(1)

        // Assert: Verify complete integration
        Assert.assertNotNull(result)
        Assert.assertEquals(expectedStopwatch, result)

    }



    // ================================================================================================
    // Save Stopwatch Integration Tests
    // ================================================================================================

    @Test
    fun saveStopwatchWithLaps_whenNewStopwatch_shouldPersistStopwatchAndLaps() = runTest {

        // Arrange: Create new stopwatch with laps
        val stopwatchEntity = createStopwatchEntity(id = 1, elapsedTime = 15000L)
        val lapEntities = createSingleLapEntity()

        val expectedStopwatch = StopWatchWithLaps(stopwatchEntity, lapEntities)


        // Act: Save using data source
        stopwatchLocalDatasource.saveStopwatchWithLaps(stopwatchEntity, lapEntities)

        // Assert: Verify data persisted by retrieving directly from DAO
        val savedData = stopwatchLocalDatasource.getStopwatchWithLaps(1)
        Assert.assertNotNull(savedData)
        Assert.assertEquals(expectedStopwatch, savedData)
    }

    @Test
    fun saveStopwatchWithLaps_whenStopwatchExists_shouldUpdateStopwatchAndReplaceLaps() = runTest {

            // Arrange: Insert initial stopwatch
            val initialStopwatch =
                createStopwatchEntity(id = 1, elapsedTime = 5000L, isRunning = true)
            stopwatchLocalDatasource.saveStopwatchWithLaps(initialStopwatch, emptyList())

            // Act: Update with new elapsed time and add laps
            val updatedStopwatch =
                createStopwatchEntity(id = 1, elapsedTime = 12000L, isRunning = false)
            val newLaps = createSingleLapEntity()
            val expected = StopWatchWithLaps(updatedStopwatch, newLaps)
            stopwatchLocalDatasource.saveStopwatchWithLaps(updatedStopwatch, newLaps)

            // Assert: Verify update was successful
            val result = stopwatchLocalDatasource.getStopwatchWithLaps(1)
            Assert.assertNotNull(result)
            Assert.assertEquals(expected, result)

        }

    @Test
    fun saveStopwatchWithLaps_whenLapListIsEmpty_shouldPersistStopwatchWithoutLaps() = runTest {

        // Arrange
        val stopwatchEntity = createStopwatchEntity(id = 3, elapsedTime = 2000L)
        val expected = StopWatchWithLaps(stopwatchEntity, emptyList())

        // Act: Save without any laps
        stopwatchLocalDatasource.saveStopwatchWithLaps(stopwatchEntity, emptyList())

        // Assert: Stopwatch saved, no laps
        val result = stopwatchLocalDatasource.getStopwatchWithLaps(3)
        Assert.assertNotNull(result)
        Assert.assertEquals(expected, result)
    }

    @Test
    fun saveStopwatchWithLaps_whenMultipleLapsProvided_shouldPersistAllLaps() = runTest {
        // Arrange: Create stopwatch with many laps
        val stopwatch = createStopwatchEntity(id = 1, elapsedTime = 50000L)
        val manyLaps = createMultipleLapEntity()
        val expected = StopWatchWithLaps(stopwatch, manyLaps)


        // Act
        stopwatchLocalDatasource.saveStopwatchWithLaps(stopwatch, manyLaps)

        // Assert: All laps persisted
        val result = stopwatchLocalDatasource.getStopwatchWithLaps(1)
        Assert.assertNotNull(result)
        Assert.assertEquals(expected, result)
    }


    // ================================================================================================
    // Delete Stopwatch Integration Tests
    // ================================================================================================

    @Test
    fun deleteStopwatchWithLaps_whenStopwatchExists_shouldRemoveStopwatchAndLaps() = runTest {
        // Arrange: Insert stopwatch with laps
        val stopwatch = createStopwatchEntity(id = 1, elapsedTime = 10000L)
        val laps = createSingleLapEntity()
        stopwatchLocalDatasource.saveStopwatchWithLaps(stopwatch, laps)

        // Verify data exists before deletion
        val beforeDelete = stopwatchLocalDatasource.getStopwatchWithLaps(1)
        Assert.assertNotNull(beforeDelete)

        // Act: Delete stopwatch
        stopwatchLocalDatasource.deleteStopwatchWithLaps(1)

        // Assert: Stopwatch and laps are removed
        val afterDelete = stopwatchLocalDatasource.getStopwatchWithLaps(1)
        Assert.assertNull(afterDelete)
    }

    @Test
    fun deleteStopwatchWithLaps_whenStopwatchDoesNotExist_shouldDoNothing() = runTest {
        // Arrange: Empty database

        // Act: Try to delete non-existent stopwatch (should not throw exception)
        stopwatchLocalDatasource.deleteStopwatchWithLaps(999)

        // Assert: No errors, database still empty
        val result = stopwatchLocalDatasource.getStopwatchWithLaps(999)
        Assert.assertNull(result)
    }

    @Test
    fun deleteStopwatchWithLaps_whenMultipleStopwatchesExist_shouldDeleteOnlySpecifiedOne() =
        runTest {

            // Arrange: Insert multiple stopwatches
            val stopwatch1 = createStopwatchEntity(id = 1, elapsedTime = 5000L)
            val stopwatch2 = createStopwatchEntity(id = 2, elapsedTime = 8000L)
            val stopwatch3 = createStopwatchEntity(id = 3, elapsedTime = 12000L)

            stopwatchLocalDatasource.saveStopwatchWithLaps(stopwatch1, emptyList())
            stopwatchLocalDatasource.saveStopwatchWithLaps(stopwatch2, emptyList())
            stopwatchLocalDatasource.saveStopwatchWithLaps(stopwatch3, emptyList())

            // Act: Delete only stopwatch 2
            stopwatchLocalDatasource.deleteStopwatchWithLaps(2)

            // Assert: Only stopwatch 2 is deleted, others remain
            val deleted = stopwatchLocalDatasource.getStopwatchWithLaps(2)
            val remaining1 = stopwatchLocalDatasource.getStopwatchWithLaps(1)
            val remaining3 = stopwatchLocalDatasource.getStopwatchWithLaps(3)

            Assert.assertNull(deleted)
            Assert.assertNotNull(remaining1)
            Assert.assertNotNull(remaining3)
        }

    @Test
    fun deleteStopwatchWithLaps_whenStopwatchHasLaps_shouldCascadeDeleteAllLaps() = runTest {
        // Arrange: Insert stopwatch with many laps
        val stopwatch = createStopwatchEntity(id = 1, elapsedTime = 30000L)
        val laps = (1..5).map { lapNumber ->
            StopWatchLapEntity(
                stopwatchId = 1,
                lapIndexId = lapNumber,
                lapStartTime = (lapNumber - 1) * 6000L,
                lapElapsedTime = 6000L,
                lapEndTime = lapNumber * 6000L
            )
        }
        stopwatchLocalDatasource.saveStopwatchWithLaps(stopwatch, laps)

        // Verify laps exist before deletion
        val beforeDelete = stopwatchLocalDatasource.getStopwatchWithLaps(1)
        Assert.assertEquals(5, beforeDelete!!.laps.size)

        // Act: Delete stopwatch
        stopwatchLocalDatasource.deleteStopwatchWithLaps(1)

        // Assert: All laps are also deleted (cascade delete)
        val afterDelete = stopwatchLocalDatasource.getStopwatchWithLaps(1)
        Assert.assertNull(afterDelete)
    }


    // ================================================================================================
    // Helper Methods
    // ================================================================================================

    private fun createStopwatchEntity(
        id: Int = 1,
        startTime: Long = 0L,
        elapsedTime: Long = 5000L,
        endTime: Long = 5000L,
        isRunning: Boolean = true
    ) = StopWatchEntity(
        id = id,
        startTime = startTime,
        elapsedTime = elapsedTime,
        endTime = endTime,
        isRunning = isRunning
    )
    private fun createSingleLapEntity() = listOf(
        StopWatchLapEntity(1, 1, lapStartTime = 1000L, lapElapsedTime = 3000L, lapEndTime = 3000L)
    )
    private fun createMultipleLapEntity() = listOf(
        StopWatchLapEntity(1, 1, lapStartTime = 1000L, lapElapsedTime = 3000L, lapEndTime = 3000L),
        StopWatchLapEntity(1, 2, lapStartTime = 1000L, lapElapsedTime = 3000L, lapEndTime = 3000L)
    )
}