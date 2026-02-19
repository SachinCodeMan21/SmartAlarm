package com.example.smartalarm.integration.stopwatch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchLapEntity
import com.example.smartalarm.feature.stopwatch.data.mapper.StopWatchMapper.toDomainModel
import com.example.smartalarm.feature.stopwatch.data.repository.StopWatchRepositoryImpl
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
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
class StopWatchRepositoryIT {

/*    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    // Only inject the repository; Hilt provides the datasource automatically
    @Inject
    lateinit var repository: StopWatchRepositoryImpl

    @Before
    fun setup() {
        hiltRule.inject() // Performs injection
    }

    // ================================================================================================
    // GET Stopwatch Tests
    // ================================================================================================

    @Test
    fun getStopwatchById_whenStopwatchDoesNotExist_returnsSuccessWithNull() = runTest {

        // Arrange

        // Act
        val result = repository.getStopwatchById(999)

        // Assert
        Assert.assertTrue(result is Result.Success)
        Assert.assertNull((result as Result.Success).data)
    }

    @Test
    fun getStopwatchById_whenStopwatchExistsWithoutLaps_returnsStopwatchWithEmptyLaps() = runTest {

        // Arrange
        val entity = createStopwatchEntity(id = 2, elapsedTime = 3000L, isRunning = false)
        val expectedModel = entity.toDomainModel(emptyList())
        repository.saveStopwatch(expectedModel)

        // Act
        val result = repository.getStopwatchById(2)

        // Assert
        Assert.assertTrue(result is Result.Success)
        Assert.assertEquals(expectedModel, (result as Result.Success).data)
    }

    @Test
    fun getStopwatchById_whenStopwatchHasLaps_returnsStopwatchWithLaps() = runTest {

        // Arrange
        val entity = createStopwatchEntity(id = 1, elapsedTime = 10000L)
        val laps = createSingleLapEntity()
        val expectedModel = entity.toDomainModel(laps)
        repository.saveStopwatch(expectedModel)

        // Act
        val result = repository.getStopwatchById(1)

        // Assert
        Assert.assertTrue(result is Result.Success)
        Assert.assertEquals(expectedModel, (result as Result.Success).data)
    }


    // ================================================================================================
    // SAVE Stopwatch Tests
    // ================================================================================================

    @Test
    fun saveStopwatch_whenNewStopwatch_persistsStopwatchAndLaps() = runTest {

        // Arrange
        val model = StopwatchModel(
            id = 1,
            elapsedTime = 15000L,
            isRunning = true,
            lapTimes = createSingleLapEntity().map { it.toDomainModel() }
        )

        // Act
        val saveResult = repository.saveStopwatch(model)

        // Assert
        Assert.assertTrue(saveResult is Result.Success)
        val saved = repository.getStopwatchById(1)
        Assert.assertTrue(saved is Result.Success)
        Assert.assertEquals(model, (saved as Result.Success).data)
    }

    @Test
    fun saveStopwatch_whenStopwatchExists_updatesStopwatchAndLaps() = runTest {

        // Arrange
        val initial = StopwatchModel(id = 1, elapsedTime = 5000L, isRunning = true)
        val updated = StopwatchModel(
            id = 1,
            elapsedTime = 12000L,
            isRunning = false,
            lapTimes = createSingleLapEntity().map { it.toDomainModel() }
        )
        repository.saveStopwatch(initial)

        // Act
        val saveResult = repository.saveStopwatch(updated)

        // Assert
        val saved = repository.getStopwatchById(1)
        Assert.assertTrue(saveResult is Result.Success)
        Assert.assertTrue(saved is Result.Success)
        Assert.assertEquals(updated, (saved as Result.Success).data)
    }


    // ================================================================================================
    // DELETE Stopwatch Tests
    // ================================================================================================
    @Test
    fun deleteStopwatchById_whenStopwatchExists_removesStopwatchAndLaps() = runTest {

        // Arrange
        val model = StopwatchModel(
            id = 1,
            elapsedTime = 10000L,
            lapTimes = createSingleLapEntity().map { it.toDomainModel() }
        )
        repository.saveStopwatch(model)

        val beforeDelete = repository.getStopwatchById(1)
        Assert.assertTrue(beforeDelete is Result.Success)
        Assert.assertNotNull((beforeDelete as Result.Success).data)

        // Act
        val deleteResult = repository.deleteStopwatch(1)
        Assert.assertTrue(deleteResult is Result.Success)

        // Assert
        val afterDelete = repository.getStopwatchById(1)
        Assert.assertTrue(afterDelete is Result.Success)
        Assert.assertNull((afterDelete as Result.Success).data)
    }

    @Test
    fun deleteStopwatchById_whenStopwatchDoesNotExist_returnsSuccess() = runTest {
        // Arrange

        // Act
        val deleteResult = repository.deleteStopwatch(999)

        // Assert
        Assert.assertTrue(deleteResult is Result.Success)
    }

    // ================================================================================================
    // HELPER METHODS
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
        isRunning = isRunning,
        lapCount = 0
    )

    private fun createSingleLapEntity() = listOf(
        StopWatchLapEntity(1, 1, lapStartTime = 1000L, lapElapsedTime = 3000L, lapEndTime = 3000L)
    )*/
}