package com.example.smartalarm.integration.timer

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartalarm.feature.timer.data.datasource.contract.TimerLocalDataSource
import com.example.smartalarm.feature.timer.data.local.entity.TimerEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class TimerLocalDataSourceIT {
/*

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Inject
    lateinit var timerLocalDatasource: TimerLocalDataSource


    // ================================================================================================
    // Get Timers Integration Tests
    // ================================================================================================

    @Test
    fun getTimerList_whenNoTimersExist_shouldReturnEmptyList() = runTest {

        // Act: Try to retrieve timers when no data is inserted
        val result = timerLocalDatasource.getTimerList().first()

        // Assert: Should return an empty list
        Assert.assertTrue(result.isEmpty())  // Or assertEquals(result.size, 0)
    }

    @Test
    fun getTimerList_whenTimersExist_shouldReturnExistingTimerList() = runTest {

        // Arrange
        val timerList = listOf(createTimerEntity(timerId = 1))
        timerLocalDatasource.saveTimer(createTimerEntity())

        // Act: Try to retrieve timers when no data is inserted
        val result = timerLocalDatasource.getTimerList().first()

        // Assert: Should return an empty list
        Assert.assertTrue(result.isNotEmpty())
        Assert.assertEquals(timerList, result)
    }

    // ================================================================================================
    // Save Timer Integration Tests
    // ================================================================================================


    @Test
    fun saveTimer_whenSavingSingleTimer_shouldSaveCorrectly() = runTest {
        // Arrange: Create a single timer entity
        val timerEntity = createTimerEntity(timerId = 1)

        // Act: Save the timer
        timerLocalDatasource.saveTimer(timerEntity)

        // Assert: The timer should be saved correctly
        val result = timerLocalDatasource.getTimerList().first()
        Assert.assertTrue(result.isNotEmpty())
        Assert.assertEquals(1, result.size)
        Assert.assertEquals(timerEntity, result[0])
    }

    @Test
    fun saveTimer_whenUpdatingExistingTimer_shouldUpdateTimer() = runTest {
        // Arrange: Create and save a timer entity
        val timerEntity1 = createTimerEntity(timerId = 1, remainingTime = 10000)
        timerLocalDatasource.saveTimer(timerEntity1)

        // Act: Update the timer (same timerId, but with different remainingTime)
        val updatedTimerEntity = createTimerEntity(timerId = 1, remainingTime = 5000)
        timerLocalDatasource.saveTimer(updatedTimerEntity)

        // Assert: The timer should be updated (remainingTime should be 5000)
        val result = timerLocalDatasource.getTimerList().first()
        Assert.assertTrue(result.isNotEmpty())
        Assert.assertEquals(1, result.size)
        Assert.assertEquals(updatedTimerEntity, result[0])
    }

    @Test
    fun saveTimer_whenSavingMultipleTimers_shouldReturnAllTimers() = runTest {
        // Arrange: Create and save multiple timers
        val timer1 = createTimerEntity(timerId = 1)
        val timer2 = createTimerEntity(timerId = 2)
        timerLocalDatasource.saveTimer(timer1)
        timerLocalDatasource.saveTimer(timer2)

        // Act: Retrieve the list of timers
        val result = timerLocalDatasource.getTimerList().first()

        // Assert: The list should contain both timers
        Assert.assertTrue(result.size == 2)
        Assert.assertTrue(result.contains(timer1))
        Assert.assertTrue(result.contains(timer2))
    }


    // ================================================================================================
    // Delete Timer Integration Tests
    // ================================================================================================
    @Test
    fun deleteTimerById_whenTimerExists_shouldDeleteTheTimer() = runTest {
        // Arrange: Create and save a timer
        val timerEntity = createTimerEntity(timerId = 1)
        timerLocalDatasource.saveTimer(timerEntity)

        // Act: Delete the saved timer by ID
        timerLocalDatasource.deleteTimerById(timerEntity.id)

        // Assert: The list of timers should be empty after deletion
        val result = timerLocalDatasource.getTimerList().first()
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun deleteTimerById_whenTimerDoesNotExist_shouldNotAffectTheList() = runTest {
        // Arrange: Ensure no timers exist in the database
        val timerEntity = createTimerEntity(timerId = 1)

        // Act: Try to delete a non-existent timer by ID
        timerLocalDatasource.deleteTimerById(timerEntity.id)

        // Assert: The list should still be empty (no timers in the database)
        val result = timerLocalDatasource.getTimerList().first()
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun getTimerList_afterDeletingAllTimers_shouldReturnEmptyList() = runTest {
        // Arrange: Create and save multiple timers
        val timer1 = createTimerEntity(timerId = 1)
        val timer2 = createTimerEntity(timerId = 2)
        timerLocalDatasource.saveTimer(timer1)
        timerLocalDatasource.saveTimer(timer2)

        // Act: Delete all timers
        timerLocalDatasource.deleteTimerById(timer1.id)
        timerLocalDatasource.deleteTimerById(timer2.id)

        // Assert: The list of timers should be empty after deletion
        val result = timerLocalDatasource.getTimerList().first()
        Assert.assertTrue(result.isEmpty())
    }



    // ================================================================================================
    // Helper Method
    // ================================================================================================
    private fun createTimerEntity(timerId : Int = 0, remainingTime : Long = 60000) : TimerEntity {
        return TimerEntity(
            id = timerId,
            startTimeMillis = 0,
            remainingMillis = remainingTime,
            endTimeMillis = 6000,
            targetDurationMillis = 60000,
            isTimerRunning = false,
            isTimerSnoozed = false,
            snoozedTargetDurationMillis = 0,
            state = "RUNNING"
        )
    }
*/


}