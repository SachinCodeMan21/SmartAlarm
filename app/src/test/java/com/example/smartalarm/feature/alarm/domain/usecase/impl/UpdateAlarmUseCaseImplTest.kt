package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.repository.AlarmRepository
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import kotlin.test.Test
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.R
import io.mockk.verify
import java.time.LocalTime

/**
 * Unit tests for the [UpdateAlarmUseCaseImpl] class.
 *
 * This test suite ensures that the [UpdateAlarmUseCaseImpl] class behaves correctly by testing:
 * - Successful alarm updating.
 * - Handling of errors when updating the alarm fails.
 *
 * It uses [MockK] to mock the dependencies of the [UpdateAlarmUseCaseImpl], including:
 * - [AlarmRepository]: The repository responsible for updating alarm data.
 * - [ResourceProvider]: Provides localized strings for error messages.
 *
 * The tests verify interactions with these dependencies and validate the result returned by the [UpdateAlarmUseCaseImpl].
 *
 * **Test cases**:
 * - `should update alarm successfully`: Verifies that the alarm is updated successfully and no errors occur.
 * - `should return error when updating the alarm fails`: Verifies that an appropriate error message is returned when updating the alarm fails.
 *
 * The tests use [runTest] for coroutines, with proper setup and teardown in the [setUp] and [tearDown] methods.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UpdateAlarmUseCaseImplTest {
/*
    // Mocking the required dependencies using annotations
    @MockK
    private lateinit var mockAlarmRepository: AlarmRepository

    @MockK
    private lateinit var mockResourceProvider: ResourceProvider

    @InjectMockKs
    private lateinit var updateAlarmUseCaseImpl: UpdateAlarmUseCaseImpl

    private val alarm = AlarmModel(id = 1, time = LocalTime.of(8, 0), days = setOf(DayOfWeek.MON))


    @Before
    fun setUp() {
        // Initialize MockK for mocks and inject dependencies
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
        // Clear mocks to verify no unexpected interactions
        clearAllMocks()
    }

    *//**
     * Tests that the alarm is updated successfully.
     * Verifies that a success result is returned after updating the alarm and that no error occurs.
     * Ensures that the [AlarmRepository.updateAlarm] function is called.
     *//*
    @Test
    fun `invoke should update alarm successfully`() = runTest {

        // Arrange: mock the successful update of the alarm
        coEvery { mockAlarmRepository.updateAlarm(alarm) } returns Result.Success(Unit)

        // Act: invoke the use case
        val result = updateAlarmUseCaseImpl.invoke(alarm)
        advanceUntilIdle()

        // Assert: verify the result is a success and the repository method was called
        assertTrue(result is Result.Success)
        assertEquals(Result.Success(Unit), result)
        coVerify { mockAlarmRepository.updateAlarm(alarm) }
    }

    *//**
     * Tests the scenario where updating the alarm fails.
     * Verifies that an error result is returned when updating the alarm fails,
     * and that the appropriate error message is passed from the [ResourceProvider].
     *//*
    @Test
    fun `invoke should return error when updating the alarm fails`() = runTest {

        // Arrange: mock the failure of the alarm update and return an error message
        val localUpdateAlarmFailureMessage = "Failed to update alarm details."
        coEvery { mockAlarmRepository.updateAlarm(alarm) } returns Result.Error(Exception(localUpdateAlarmFailureMessage))
        every { mockResourceProvider.getString(R.string.failed_to_update_the_alarm_details) } returns localUpdateAlarmFailureMessage

        // Act: invoke the use case
        val result = updateAlarmUseCaseImpl.invoke(alarm)
        advanceUntilIdle()

        // Assert: verify the result is an error with the expected message
        assertTrue(result is Result.Error)
        assertEquals(localUpdateAlarmFailureMessage, (result as Result.Error).exception.message)
        coVerify { mockAlarmRepository.updateAlarm(alarm) }
        verify { mockResourceProvider.getString(R.string.failed_to_update_the_alarm_details) }
    }*/
}

