package com.example.smartalarm.feature.alarm.domain.usecase.impl

import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.repository.AlarmRepository
import org.junit.Assert.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.InjectMockKs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import java.time.LocalTime
import kotlin.test.assertTrue
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.R
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.verify
import org.junit.After

/**
 * Unit tests for [GetAlarmByIdUseCaseImpl], which is responsible for retrieving an alarm by its ID.
 * This class tests the behavior of the `invoke` method for both successful retrieval and failure scenarios.
 *
 * The tests are designed to mock dependencies like [AlarmRepository] and [ResourceProvider] using
 * MockK annotations to ensure that the use case behaves correctly in isolated conditions without relying
 * on real implementations of these dependencies.
 *
 * Test cases:
 * - **`invoke should return Success when alarm is found`**: Verifies that when an alarm is successfully retrieved from the repository,
 *   the result is wrapped in a [Result.Success] containing the [AlarmModel].
 * - **`invoke should return Error when alarm retrieval fails`**: Verifies that if an error occurs while retrieving the alarm,
 *   the result is wrapped in a [Result.Error] containing a localized error message.
 *
 * @see GetAlarmByIdUseCaseImpl for the use case implementation.
 */
@ExperimentalCoroutinesApi
class GetAlarmByIdUseCaseImplTest {
/*
    // Mocking dependencies with MockK annotations
    @MockK
    lateinit var alarmRepository: AlarmRepository

    @MockK
    lateinit var resourceProvider: ResourceProvider

    @InjectMockKs
    lateinit var getAlarmByIdUseCaseImpl: GetAlarmByIdUseCaseImpl

    private lateinit var testDispatcher: TestDispatcher

    *//**
     * Sets up the necessary environment for the tests, including mock initialization and dispatcher setup.
     *//*
    @Before
    fun setUp() {
        // Initialize MockK annotations
        MockKAnnotations.init(this)
        testDispatcher = StandardTestDispatcher() // Set up a test dispatcher for coroutines
    }

    @After
    fun tearDown() {
        // Clear mocks to verify no unexpected interactions
        clearAllMocks()
    }

    *//**
     * Tests the successful retrieval of an alarm from the repository.
     * Verifies that when the alarm is found, the use case returns a [Result.Success]
     * with the expected [AlarmModel].
     *//*
    @Test
    fun `invoke should return Success when alarm is found`() = runTest {

        // Arrange
        val alarmId = 1
        val expectedAlarmModel = AlarmModel(id = alarmId, time = LocalTime.of(7, 0), label = "Wake up")
        coEvery { alarmRepository.getAlarmById(alarmId) } returns Result.Success(expectedAlarmModel)

        // Act
        val result = getAlarmByIdUseCaseImpl.invoke(alarmId)
        advanceUntilIdle()

        // Assert
        assertTrue(result is Result.Success)
        assertEquals(expectedAlarmModel, result.data)
        coVerify { alarmRepository.getAlarmById(alarmId) }
    }

    *//**
     * Tests the scenario where the alarm retrieval fails.
     * Verifies that when an error occurs in the repository, the use case returns a [Result.Error]
     * with the appropriate localized error message from [ResourceProvider].
     *//*
    @Test
    fun `invoke should return Error when alarm retrieval fails`() = runTest {

        // Arrange
        val alarmId = 1
        val errorMessage = "Failed to retrieve the alarm details. Please try again later."
        val expectedError = Exception(errorMessage)

        // Mock repository failure and resource provider error message
        coEvery { alarmRepository.getAlarmById(alarmId) } returns Result.Error(expectedError)
        every { resourceProvider.getString(R.string.failed_to_retrieve_the_alarm_details) } returns errorMessage

        // Act
        val result = getAlarmByIdUseCaseImpl.invoke(alarmId)
        advanceUntilIdle()

        // Assert
        assertTrue(result is Result.Error)
        assertEquals(errorMessage, result.exception.message)
        coVerify { alarmRepository.getAlarmById(alarmId) }
        verify { resourceProvider.getString(R.string.failed_to_retrieve_the_alarm_details) }
    }*/
}
