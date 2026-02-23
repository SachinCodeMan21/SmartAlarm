package com.example.smartalarm.feature.stopwatch.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Unit tests for the [com.example.smartalarm.feature.stopwatch.domain.usecase.impl.SaveStopwatchStateUseCaseImpl] class, which handles saving a stopwatch state
 * to the repository.
 *
 * The tests cover the following scenarios:
 * 1. When the repository successfully saves the stopwatch, the use case returns a success result.
 * 2. When the repository fails to save the stopwatch, the use case returns an error result.
 *
 * The tests use [io.mockk.impl.annotations.MockK] for mocking the repository and [kotlinx.coroutines.test.runTest] for coroutine-based testing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SaveStopwatchStateUseCaseImplTest {

/*
    @MockK
    private lateinit var repository: StopWatchRepository

    @InjectMockKs
    private lateinit var saveStopwatchUseCase: SaveStopwatchStateUseCaseImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    //====================================================
    // SaveStopwatchStateUseCase Test Scenarios
    //====================================================
    @Test
    fun invoke_whenRepositorySavesSuccessfully_shouldReturn_success() = runTest {

        // Arrange
        coEvery { repository.saveStopwatch(any()) } returns Result.Success(Unit)

        // Act
        val result = saveStopwatchUseCase.invoke(sampleStopwatch)

        // Assert
        Assert.assertTrue(result is Result.Success)
        coVerify(exactly = 1) { repository.saveStopwatch(any()) }
    }

    @Test
    fun invoke_whenRepositorySaveFails_shouldReturn_error() = runTest {

        // Arrange
        val exception = RuntimeException("Failed to save")
        coEvery { repository.saveStopwatch(any()) } returns Result.Error(exception)

        // Act
        val result = saveStopwatchUseCase.invoke(sampleStopwatch)

        // Assert
        Assert.assertTrue(result is Result.Error)
        Assert.assertEquals(exception, (result as Result.Error).exception)
        coVerify(exactly = 1) { repository.saveStopwatch(any()) }
    }

    //====================================================
    // Helper Method
    //====================================================
    private val sampleStopwatch = StopwatchModel(
        id = 1,
        startTime = 1000L,
        elapsedTime = 2000L,
        isRunning = true,
        lapTimes = emptyList(),
        lapCount = 0,
        endTime = 0L
    )
*/

}