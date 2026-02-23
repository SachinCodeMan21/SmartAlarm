package com.example.smartalarm.integration.timer

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class TimerRepositoryIT {

/*    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var timerRepository: TimerRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }



    // ================================================================================================
    // Get Timers Integration Tests
    // ================================================================================================
    @Test
    fun getTimerList_whenNoTimersExist_shouldReturnEmptyList() = runTest {
        // Act: Try to retrieve timers when no data is inserted
        val result = timerRepository.getTimerList().first()

        // Assert: Should return an empty list
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun getTimerList_whenTimersExist_shouldReturnExistingTimerList() = runTest {
        // Arrange: Create and save a timer
        val timerModel = createTimerModel(timerId = 1)
        timerRepository.persistTimer(timerModel)

        // Act: Retrieve the list of timers
        val result = timerRepository.getTimerList().first()

        // Assert: The list should contain the saved timer
        Assert.assertTrue(result.isNotEmpty())
        Assert.assertEquals(1, result.size)
        Assert.assertEquals(timerModel, result[0])
    }


    // ================================================================================================
    // Save Timer Integration Tests
    // ================================================================================================

    @Test
    fun saveTimer_whenSavingTimer_shouldSaveCorrectly() = runTest {

        // Arrange: Create a single timer model
        val timerModel = createTimerModel(timerId = 1)

        // Act: Save the timer
        val saveResult = timerRepository.persistTimer(timerModel)

        // Assert: Save result should be successful
        Assert.assertTrue(saveResult is Result.Success)
        val result = timerRepository.getTimerList().first()
        Assert.assertTrue(result.isNotEmpty())
        Assert.assertEquals(1, result.size)
        Assert.assertEquals(timerModel, result[0])
    }

    @Test
    fun saveTimer_whenUpdatingExistingTimer_shouldUpdateTimer() = runTest {
        // Arrange: Create and save a timer model
        val timerModel1 = createTimerModel(timerId = 1, remainingTime = 10000)
        timerRepository.persistTimer(timerModel1)

        // Act: Update the timer (same timerId, but with a different remainingTime)
        val updatedTimerModel = createTimerModel(timerId = 1, remainingTime = 5000)
        timerRepository.persistTimer(updatedTimerModel)

        // Assert: The timer should be updated (remainingTime should be 5000)
        val result = timerRepository.getTimerList().first()
        Assert.assertTrue(result.isNotEmpty())
        Assert.assertEquals(1, result.size)
        Assert.assertEquals(updatedTimerModel, result[0])
    }


    // ================================================================================================
    // Delete Timer Integration Tests
    // ================================================================================================

    @Test
    fun deleteTimerById_whenTimerDoesNotExist_shouldNotAffectTheList() = runTest {
        // Arrange: Ensure no timers exist in the database
        val timerModel = createTimerModel(timerId = 1)

        // Act: Try to delete a non-existent timer by ID
        val deleteResult = timerRepository.deleteTimerById(timerModel.timerId)

        // Assert: Deletion result should be successful (no exception)
        Assert.assertTrue(deleteResult is Result.Success)

        // Verify: The list should still be empty
        val result = timerRepository.getTimerList().first()
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    fun deleteTimerById_whenTimerExists_shouldDeleteTheTimer() = runTest {
        // Arrange: Create and save a timer model
        val timerModel = createTimerModel(timerId = 1)
        timerRepository.persistTimer(timerModel)

        // Act: Delete the saved timer by ID
        val deleteResult = timerRepository.deleteTimerById(timerModel.timerId)

        // Assert: Deletion result should be successful
        Assert.assertTrue(deleteResult is Result.Success)

        // Verify: The list of timers should be empty after deletion
        val result = timerRepository.getTimerList().first()
        Assert.assertTrue(result.isEmpty())
    }


    // ================================================================================================
    // Helper Method
    // ================================================================================================
    private fun createTimerModel(timerId: Int = 0, remainingTime: Long = 60000): TimerModel {
        return TimerModel(
            timerId = timerId,
            startTime = System.currentTimeMillis(),
            remainingTime = remainingTime,
            endTime = System.currentTimeMillis() + remainingTime,
            targetTime = System.currentTimeMillis() + remainingTime,
            isTimerRunning = false,
            isTimerSnoozed = false,
            snoozedTargetTime = 0,
            state = TimerState.RUNNING
        )
    }*/
}