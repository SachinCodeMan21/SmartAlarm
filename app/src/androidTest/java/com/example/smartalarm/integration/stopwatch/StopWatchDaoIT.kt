package com.example.smartalarm.integration.stopwatch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartalarm.feature.stopwatch.data.local.dao.StopWatchDao
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopWatchLapEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class StopWatchDaoIT {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var stopWatchDao: StopWatchDao

    @Before
    fun setUp(){
        hiltRule.inject()
    }

    // Test upsertStopwatch and upsertStopwatchLap
    @Test
    fun upsertStopwatchAndLap_whenStopwatchAndLapsInserted_thenStopwatchAndLapsShouldBeFetchedCorrectly() =
        runTest {
            val stopwatch = StopWatchEntity(
                id = 1,
                startTime = System.currentTimeMillis(),
                elapsedTime = 0L,
                endTime = 0L,
                isRunning = false,
                lapCount = 0
            )
            val lap1 = StopWatchLapEntity(
                stopwatchId = 1,
                lapIndexId = 1,
                lapStartTime = System.currentTimeMillis(),
                lapElapsedTime = 1000L,
                lapEndTime = System.currentTimeMillis() + 1000
            )
            val lap2 = StopWatchLapEntity(
                stopwatchId = 1,
                lapIndexId = 2,
                lapStartTime = System.currentTimeMillis(),
                lapElapsedTime = 2000L,
                lapEndTime = System.currentTimeMillis() + 2000
            )

            // Insert stopwatch and laps
            stopWatchDao.upsertStopwatch(stopwatch)
            stopWatchDao.upsertStopwatchLap(lap1)
            stopWatchDao.upsertStopwatchLap(lap2)

            // Fetch the stopwatch along with its laps
            val stopwatchWithLaps = stopWatchDao.getStopwatchWithLaps(1)

            // Verify that the stopwatch and laps were inserted correctly
            assertEquals(stopwatchWithLaps?.stopwatch?.id, 1)
            assertEquals(stopwatchWithLaps?.laps?.size, 2)
            assertEquals(stopwatchWithLaps?.laps?.get(0)?.lapElapsedTime, 1000L)
            assertEquals(stopwatchWithLaps?.laps?.get(1)?.lapElapsedTime, 2000L)
        }

    // Test transaction: upsertStopwatchWithLaps
    @Test
    fun upsertStopwatchWithLaps_whenStopwatchAndLapsUpserted_thenBothShouldBeInserted() = runTest {
        val stopwatch = StopWatchEntity(
            id = 2,
            startTime = System.currentTimeMillis(),
            elapsedTime = 0L,
            endTime = 0L,
            isRunning = false,
            lapCount = 0
        )
        val lap1 = StopWatchLapEntity(
            stopwatchId = 2,
            lapIndexId = 1,
            lapStartTime = System.currentTimeMillis(),
            lapElapsedTime = 1500L,
            lapEndTime = System.currentTimeMillis() + 1500
        )
        val lap2 = StopWatchLapEntity(
            stopwatchId = 2,
            lapIndexId = 2,
            lapStartTime = System.currentTimeMillis(),
            lapElapsedTime = 2500L,
            lapEndTime = System.currentTimeMillis() + 2500
        )

        // Upsert both stopwatch and laps in a single transaction
        stopWatchDao.upsertStopwatchWithLaps(stopwatch, listOf(lap1, lap2))

        // Fetch the stopwatch along with its laps
        val stopwatchWithLaps = stopWatchDao.getStopwatchWithLaps(2)

        // Verify that the stopwatch and laps were inserted correctly
        assertEquals(stopwatchWithLaps?.stopwatch?.id, 2)
        assertEquals(stopwatchWithLaps?.laps?.size, 2)
        assertEquals(stopwatchWithLaps?.laps?.get(0)?.lapElapsedTime, 1500L)
        assertEquals(stopwatchWithLaps?.laps?.get(1)?.lapElapsedTime, 2500L)
    }

    // Test deleteStopwatchWithLaps
    @Test
    fun deleteStopwatchWithLaps_whenStopwatchDeleted_thenStopwatchAndLapsShouldBeDeleted() = runTest {
        val stopwatch = StopWatchEntity(
            id = 3,
            startTime = System.currentTimeMillis(),
            elapsedTime = 0L,
            endTime = 0L,
            isRunning = false,
            lapCount = 0
        )
        val lap1 = StopWatchLapEntity(
            stopwatchId = 3,
            lapIndexId = 1,
            lapStartTime = System.currentTimeMillis(),
            lapElapsedTime = 1000L,
            lapEndTime = System.currentTimeMillis() + 1000
        )
        val lap2 = StopWatchLapEntity(
            stopwatchId = 3,
            lapIndexId = 2,
            lapStartTime = System.currentTimeMillis(),
            lapElapsedTime = 2000L,
            lapEndTime = System.currentTimeMillis() + 2000
        )

        // Insert stopwatch and laps
        stopWatchDao.upsertStopwatch(stopwatch)
        stopWatchDao.upsertStopwatchLap(lap1)
        stopWatchDao.upsertStopwatchLap(lap2)

        // Delete stopwatch and laps
        stopWatchDao.deleteStopwatchWithLaps(3)

        // Try to fetch the deleted stopwatch with laps
        val stopwatchWithLaps = stopWatchDao.getStopwatchWithLaps(3)

        // Assert that the stopwatch and its laps are deleted (should return null)
        assertNull(stopwatchWithLaps)
    }

    // Test upsert of stopwatch and laps when a stopwatch already exists
    @Test
    fun upsertStopwatchWithLaps_whenStopwatchAlreadyExists_thenStopwatchAndLapsShouldBeUpdated() = runTest {
        val stopwatch = StopWatchEntity(
            id = 4,
            startTime = System.currentTimeMillis(),
            elapsedTime = 0L,
            endTime = 0L,
            isRunning = false,
            lapCount = 0
        )
        val lap1 = StopWatchLapEntity(
            stopwatchId = 4,
            lapIndexId = 1,
            lapStartTime = System.currentTimeMillis(),
            lapElapsedTime = 1100L,
            lapEndTime = System.currentTimeMillis() + 1100
        )
        val lap2 = StopWatchLapEntity(
            stopwatchId = 4,
            lapIndexId = 2,
            lapStartTime = System.currentTimeMillis(),
            lapElapsedTime = 2100L,
            lapEndTime = System.currentTimeMillis() + 2100
        )

        // Insert stopwatch and laps
        stopWatchDao.upsertStopwatch(stopwatch)
        stopWatchDao.upsertStopwatchLap(lap1)
        stopWatchDao.upsertStopwatchLap(lap2)

        // Modify stopwatch and laps
        val updatedStopwatch = StopWatchEntity(
            id = 4,
            startTime = System.currentTimeMillis(),
            elapsedTime = 0L,
            endTime = 0L,
            isRunning = false,
            lapCount = 0
        )
        val updatedLap1 = StopWatchLapEntity(
            stopwatchId = 4,
            lapIndexId = 1,
            lapStartTime = System.currentTimeMillis(),
            lapElapsedTime = 1200L,
            lapEndTime = System.currentTimeMillis() + 1200
        )
        val updatedLap2 = StopWatchLapEntity(
            stopwatchId = 4,
            lapIndexId = 2,
            lapStartTime = System.currentTimeMillis(),
            lapElapsedTime = 2200L,
            lapEndTime = System.currentTimeMillis() + 2200
        )

        // Upsert updated stopwatch and laps
        stopWatchDao.upsertStopwatchWithLaps(updatedStopwatch, listOf(updatedLap1, updatedLap2))

        // Fetch the updated stopwatch with laps
        val stopwatchWithLaps = stopWatchDao.getStopwatchWithLaps(4)

        // Verify that the stopwatch and laps were updated correctly
        assertEquals(stopwatchWithLaps?.stopwatch?.id, 4)
        assertEquals(stopwatchWithLaps?.laps?.get(0)?.lapElapsedTime, 1200L)
        assertEquals(stopwatchWithLaps?.laps?.get(1)?.lapElapsedTime, 2200L)
    }
}
