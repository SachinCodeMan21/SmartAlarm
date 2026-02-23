package com.example.smartalarm.feature.stopwatch.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Unit tests for the [com.example.smartalarm.feature.stopwatch.domain.usecase.impl.DeleteStopwatchUseCaseImpl] class, which handles the deletion of a stopwatch
 * from the repository by its ID.
 *
 * The tests cover the following scenarios:
 * 1. When the repository successfully deletes a stopwatch, the use case returns a success result.
 * 2. When the repository fails to delete the stopwatch, the use case returns an error result.
 *
 * The tests use [io.mockk.impl.annotations.MockK] for mocking the repository and [kotlinx.coroutines.test.runTest] for coroutine-based testing.
 */

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteStopwatchUseCaseImplTest {

/*    @MockK
    private lateinit var repository: StopWatchRepository

    @InjectMockKs
    private lateinit var deleteStopwatchUseCase: DeleteStopwatchUseCaseImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    //====================================================
    // DeleteStopwatchUseCase Test Scenarios
    //====================================================

    @Test
    fun invoke_whenRepositoryDeleteSucceeds_shouldReturn_success() = runTest {

        // Arrange
        val stopwatch =
            StopwatchModel(id = 1, startTime = 0L, elapsedTime = 5000L, isRunning = false)
        coEvery { repository.deleteStopwatch(any()) } returns Result.Success(Unit)

        // Act
        val result = deleteStopwatchUseCase(stopwatch)

        // Assert
        Assert.assertEquals(Result.Success(Unit), result)
        coVerify(exactly = 1) { repository.deleteStopwatch(any()) }
    }

    @Test
    fun invoke_whenRepositoryDeleteFails_shouldReturn_error() = runTest {

        // Arrange
        val stopwatch = StopwatchModel(id = 1)
        val exception = RuntimeException("Delete failed")
        coEvery { repository.deleteStopwatch(any()) } returns Result.Error(exception)

        // Act
        val result = deleteStopwatchUseCase(stopwatch)

        // Assert
        Assert.assertEquals(Result.Error(exception), result)
        coVerify(exactly = 1) { repository.deleteStopwatch(any()) }
    }*/
}