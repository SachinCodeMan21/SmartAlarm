@file:Suppress("UnusedFlow")

package com.example.smartalarm.feature.timer.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Unit tests for [TimerRepositoryImpl], responsible for ensuring correct interaction with the local data source
 * and the proper mapping between data layers (Entity to Domain Model).
 *
 * The tests mock the [com.example.smartalarm.feature.timer.data.datasource.contract.TimerLocalDataSource] to isolate the repository's logic and ensure that the repository behaves correctly,
 * handling both success and error scenarios. The tests also validate that the repository transforms entities into domain models properly
 * and ensures the repository handles errors as expected when the local data source fails.
 *
 * ## Tests include:
 *
 * 1. **`test getTimerList maps entities to models correctly`**:
 *    - Verifies that the repository correctly maps the list of [TimerEntity] objects returned by the local data source to a list of [TimerModel] objects.
 *
 * 2. **`test saveTimer calls saveTimer on localDataSource and returns Success`**:
 *    - Verifies that when a [TimerModel] is saved, the repository properly calls the [saveTimer] method on the local data source and returns a success result.
 *
 * 3. **`test saveTimer returns Error when localDataSource throws exception`**:
 *    - Simulates an error when saving a [TimerModel] and verifies that the repository correctly returns an error result when the local data source throws an exception.
 *
 * 4. **`test deleteTimerById calls deleteTimerById on localDataSource`**:
 *    - Verifies that when deleting a timer by ID, the repository correctly calls the [deleteTimerById] method on the local data source and returns a success result.
 *
 * 5. **`test deleteTimerById returns Error when localDataSource throws exception`**:
 *    - Simulates an error during the deletion of a timer by ID and ensures that the repository returns an error result when the local data source throws an exception.
 */

@ExperimentalCoroutinesApi
class TimerRepositoryImplTest {

/*    private lateinit var timerRepository: TimerRepository
    private lateinit var timerLocalDataSource: TimerLocalDataSource

    @Before
    fun setup() {
        // Mock the dependencies
        timerLocalDataSource = mockk()
        timerRepository = TimerRepositoryImpl(timerLocalDataSource)
    }

    // ================================================================================================
    // Get Timers Tests
    // ================================================================================================

    @Test
    fun getTimerList_whenNoTimersExist_shouldReturnEmptyList() =
        runTest {

            // Arrange: Mock the local data source to return an empty list
            every { timerLocalDataSource.getTimerList() } returns flowOf(
                emptyList()
            )

            // Act: Retrieve the list of timers from the repository
            val result = timerRepository.getTimerList().first()

            // Assert: The result should be an empty list
            assertTrue(result.isEmpty())
            verify(exactly = 1) { timerLocalDataSource.getTimerList() }
        }

    @Test
    fun getTimerList_whenTimersExist_shouldReturnExistingTimerList() =
        runTest {
            // Arrange: Mock the local data source to return a list of timers
            val timerModel = createTimerModel(timerId = 1)
            val timerEntity = timerModel.toEntity()
            coEvery { timerLocalDataSource.getTimerList() } returns flowOf(
                listOf(timerEntity)
            )

            // Act: Retrieve the list of timers from the repository
            val result = timerRepository.getTimerList().first()

            // Assert: The list should contain the saved timer
            assertTrue(result.isNotEmpty())
            assertEquals(1, result.size)
            assertEquals(timerModel, result[0])
            verify { timerLocalDataSource.getTimerList() }
        }


    // ================================================================================================
    // Save Timer Tests
    // ================================================================================================

    @Test
    fun saveTimer_whenDatabaseErrorOccurs_shouldReturnError() =
        runTest {
            // Arrange: Create a single timer model and mock the local data source to simulate a database error
            val timerModel = createTimerModel(timerId = 1)
            coEvery { timerLocalDataSource.saveTimer(any()) } throws Exception(
                "Database error"
            )

            // Act: Try saving the timer
            val saveResult = timerRepository.persistTimer(timerModel)

            // Assert: Save result should be an error
            assertTrue(saveResult is Result.Error)
            kotlin.test.assertEquals(
                "Database error",
                saveResult.exception.message
            )

            // Verify: Verify that saveTimer was called on the local data source
            coVerify(exactly = 1) { timerLocalDataSource.saveTimer(any()) }
        }

    @Test
    fun saveTimer_whenSavingTimer_shouldSaveCorrectly() =
        runTest {

            // Arrange: Create a single timer model
            val timerModel = createTimerModel(timerId = 1)
            coEvery { timerLocalDataSource.saveTimer(any()) } just runs


            // Act: Save the timer
            val saveResult = timerRepository.persistTimer(timerModel)

            // Assert: Save result should be successful
            assertTrue(saveResult is Result.Success)

            // Verify: Verify that saveTimer was called on the local data source
            coVerify(exactly = 1) { timerLocalDataSource.saveTimer(any()) }

        }

    @Test
    fun saveTimer_whenUpdatingExistingTimer_shouldUpdateCorrectly() =
        runTest {

            // Arrange: Create and save a timer model
            val timerModel1 = createTimerModel(timerId = 1, remainingTime = 10000)
            coEvery { timerLocalDataSource.saveTimer(any()) } just runs
            timerRepository.persistTimer(timerModel1)

            // Act: Update the timer (same timerId, but with a different remainingTime)
            val updatedTimerModel = createTimerModel(timerId = 1, remainingTime = 5000)
            val updateResult = timerRepository.persistTimer(updatedTimerModel)

            // Assert
            assertTrue { updateResult is Result.Success }
            coVerify { timerLocalDataSource.saveTimer(any()) }

        }


    // ================================================================================================
    // Delete Timer Tests
    // ================================================================================================

    @Test
    fun deleteTimerById_whenTimerDoesNotExist_shouldNotAffectTheList() =
        runTest {

            // Arrange: Ensure no timers exist in the database
            val timerModel = createTimerModel(timerId = 1)
            coEvery { timerLocalDataSource.getTimerList() } returns _root_ide_package_.kotlinx.coroutines.flow.flowOf(
                emptyList()
            )
            coEvery { timerLocalDataSource.deleteTimerById(any()) } just runs

            // Act: Try to delete a non-existent timer by ID
            val deleteResult = timerRepository.deleteTimerById(timerModel.timerId)

            // Assert: Deletion result should be successful (no exception)
            assertTrue(deleteResult is Result.Success)

            // Verify: The list should still be empty after deletion
            val result = timerRepository.getTimerList().first()
            assertTrue(result.isEmpty())
            coVerify(exactly = 1) { timerLocalDataSource.deleteTimerById(any()) }
        }

    @Test
    fun deleteTimerById_whenTimerExists_shouldDeleteTheTimer() = runTest {
        // Arrange: Create and save a timer model
        val timerModel = createTimerModel(timerId = 1)
        val timerEntity = timerModel.toEntity()

        coEvery { timerLocalDataSource.saveTimer(timerEntity) } just runs
        coEvery { timerLocalDataSource.deleteTimerById(any()) } just runs

        timerRepository.persistTimer(timerModel)

        // Act: Delete the saved timer by ID
        val deleteResult = timerRepository.deleteTimerById(timerModel.timerId)

        // Assert: Deletion result should be successful
        assertTrue(deleteResult is Result.Success)
        coVerify { timerLocalDataSource.deleteTimerById(timerModel.timerId) }

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