package com.example.smartalarm.integration.stopwatch

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartalarm.core.data.database.MyDatabase
import com.example.smartalarm.feature.stopwatch.data.local.dao.StopwatchDao
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchStateEntity
import com.example.smartalarm.feature.stopwatch.data.local.entity.StopwatchLapEntity
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class StopWatchDaoIT {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: MyDatabase
    @Inject
    lateinit var stopwatchDao: StopwatchDao

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun insertStateAndLaps_ReadBackAsCombinedObject() = runTest {

        // 1. Arrange
        val state = StopwatchStateEntity(id = 1, isRunning = true)

        val laps = listOf(
            StopwatchLapEntity(stopwatchId = 1, lapIndex = 1, lapStartTimeMillis = 1000, lapElapsedTimeMillis = 500, lapEndTimeMillis = 1500),
            StopwatchLapEntity(stopwatchId = 1, lapIndex = 2, lapStartTimeMillis = 1500, lapElapsedTimeMillis = 600, lapEndTimeMillis = 2100)
        )

        // 2. Act
        stopwatchDao.upsertStopwatchState(state)
        laps.forEach { stopwatchDao.upsertLap(it) }


        // 3. Assert
        val result = stopwatchDao.getStopwatchWithLaps(1)
        assertThat(result).isNotNull()
        assertThat(result?.stopwatch?.isRunning).isTrue()
        assertThat(result?.laps).hasSize(2)
        assertThat(result?.laps?.get(0)?.lapIndex).isEqualTo(1)
    }

    @Test
    fun deleteState_ShouldCascadeDeleteLaps() = runTest {
        // 1. Setup existing data
        val state = StopwatchStateEntity(id = 1)
        val lap = StopwatchLapEntity(stopwatchId = 1, lapIndex = 1, lapStartTimeMillis = 0, lapElapsedTimeMillis = 0, lapEndTimeMillis = 0)

        stopwatchDao.upsertStopwatchState(state)
        stopwatchDao.upsertLap(lap)

        // 2. Delete the parent state
        stopwatchDao.deleteStopwatchState(1)

        // 3. Verify both are gone
        val result = stopwatchDao.getStopwatchWithLaps(1)
        assertThat(result).isNull()

        // Manual check on laps table to confirm Cascade
        val allLaps = stopwatchDao.observeStopwatchWithLaps(1).first()?.laps ?: emptyList()
        assertThat(allLaps).isEmpty()
    }

    @Test
    fun syncStopwatchSession_ShouldReplaceOldLapsWithNewOnes() = runTest {

        // 1. Setup initial state with 1 lap
        val initialState = StopwatchStateEntity(id = 1, totalLaps = 1)
        val oldLap = StopwatchLapEntity(stopwatchId = 1, lapIndex = 1, lapStartTimeMillis = 10, lapElapsedTimeMillis = 10, lapEndTimeMillis = 20)
        stopwatchDao.syncStopwatchSession(initialState, listOf(oldLap))

        // 2. Perform sync with entirely new lap data
        val newState = StopwatchStateEntity(id = 1, totalLaps = 2)
        val newLaps = listOf(
            StopwatchLapEntity(stopwatchId = 1, lapIndex = 1, lapStartTimeMillis = 100, lapElapsedTimeMillis = 100, lapEndTimeMillis = 200),
            StopwatchLapEntity(stopwatchId = 1, lapIndex = 2, lapStartTimeMillis = 200, lapElapsedTimeMillis = 100, lapEndTimeMillis = 300)
        )

        stopwatchDao.syncStopwatchSession(newState, newLaps)

        // 3. Verify
        val result = stopwatchDao.getStopwatchWithLaps(1)
        assertThat(result?.laps).hasSize(2)
        // Ensure the old lap (10ms) was deleted and replaced by new lap (100ms)
        assertThat(result?.laps?.first()?.lapStartTimeMillis).isEqualTo(100)
    }
}
