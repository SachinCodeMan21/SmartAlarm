package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.repository.AlarmRepository
import org.junit.Assert.*
import com.example.smartalarm.core.model.Result
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import org.junit.Before
import org.junit.Test
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.After
import java.time.LocalTime
import com.example.smartalarm.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle

/**
 * Unit tests for the [SaveAlarmUseCaseImpl] class.
 *
 * This test suite ensures that the [SaveAlarmUseCaseImpl] class behaves correctly by testing:
 * - Successful alarm saving.
 * - Handling of errors when saving the alarm fails.
 *
 * It uses [MockK] to mock the dependencies of the [SaveAlarmUseCaseImpl], including:
 * - [AlarmRepository]: The repository responsible for saving alarm data.
 * - [ResourceProvider]: Provides localized strings for error messages.
 *
 * The tests verify interactions with these dependencies and validate the result returned by the [SaveAlarmUseCaseImpl].
 *
 * **Test cases**:
 * - `should save alarm successfully`: Verifies that the alarm is saved successfully and returns the saved alarm ID.
 * - `should return error when saving the alarm fails`: Verifies that an appropriate error message is returned when saving the alarm fails.
 *
 * The tests use [runTest] for coroutines, with proper setup and teardown in the [setUp] and [tearDown] methods.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SaveAlarmUseCaseImplTest {

    // Mocking the required dependencies using annotations
    @MockK
    private lateinit var alarmRepository: AlarmRepository

    @MockK
    private lateinit var resourceProvider: ResourceProvider

    @InjectMockKs
    private lateinit var saveAlarmUseCaseImpl: SaveAlarmUseCaseImpl

    private val alarm = AlarmModel(id = 0, time = LocalTime.of(8, 0), days = emptySet())

    @Before
    fun setUp() {
        // Initialize MockK
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        // Clear mocks to verify no unexpected interactions
        clearAllMocks()
    }

    /**
     * Tests that the alarm is saved successfully.
     * Verifies that a success result is returned with the saved alarm's ID.
     * Ensures that the [AlarmRepository.saveAlarm] function is called.
     */
    @Test
    fun `invoke should return Success when alarm is saved`() = runTest {
        // Arrange: mock the successful saving of the alarm
        val savedAlarmId = 1
        coEvery { alarmRepository.saveAlarm(alarm) } returns Result.Success(savedAlarmId)

        // Act: invoke the use case
        val result = saveAlarmUseCaseImpl.invoke(alarm)
        advanceUntilIdle()

        // Assert: verify the result is a success with the saved alarm's ID
        assertTrue(result is Result.Success)
        assertEquals(savedAlarmId, (result as Result.Success).data)
        coVerify { alarmRepository.saveAlarm(alarm) }
    }

    /**
     * Tests the error scenario where saving the alarm fails.
     * Verifies that an error result is returned when saving the alarm fails,
     * and that the appropriate error message is passed from the [ResourceProvider].
     */
    @Test
    fun `invoke should return error when saving the alarm fails`() = runTest {

        // Arrange: mock the failure of saving the alarm and return an error message
        val localSaveFailureMessage = "Failed to save alarm details. Please try again later."
        coEvery { alarmRepository.saveAlarm(alarm) } returns Result.Error(Exception(localSaveFailureMessage))
        every { resourceProvider.getString(R.string.failed_to_save_the_alarm_details) } returns localSaveFailureMessage

        // Act: invoke the use case
        val result = saveAlarmUseCaseImpl.invoke(alarm)
        advanceUntilIdle()

        // Assert: verify the result is an error with the expected message
        assertTrue(result is Result.Error)
        assertEquals(localSaveFailureMessage, (result as Result.Error).exception.message)
        coVerify { alarmRepository.saveAlarm(alarm) }
        verify { resourceProvider.getString(R.string.failed_to_save_the_alarm_details) }
    }
}

