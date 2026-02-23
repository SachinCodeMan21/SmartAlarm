package com.example.smartalarm.feature.clock.data.repository

import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceLocalDataSource
import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceRemoteDataSource
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import io.mockk.coEvery
import io.mockk.coVerify
import com.example.smartalarm.core.model.Result
import com.example.smartalarm.feature.clock.data.local.entity.PlaceEntity
import com.example.smartalarm.feature.clock.data.mapper.PlaceMapper.toEntity
import com.example.smartalarm.feature.clock.data.remote.dto.PlaceDto
import io.mockk.MockKAnnotations
import io.mockk.coVerifySequence
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import java.io.IOException
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceRepositoryImplTest {

/*    @RelaxedMockK
    private lateinit var localDataSource: PlaceLocalDataSource

    @RelaxedMockK
    private lateinit var remoteDataSource: PlaceRemoteDataSource

    private lateinit var repository: PlaceRepositoryImpl

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockKAnnotations.init(this)
        repository = PlaceRepositoryImpl(localDataSource, remoteDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }


    @Test
    fun `savePlace returns success when local insert succeeds`() = runTest(testDispatcher) {

        // Arrange
        val place =   PlaceModel(
            id = 1,
            fullName = "Asia/Tokyo",
            primaryName = "Tokyo",
            timeZoneId = "Asia/Tokyo",
            offsetSeconds = -14400,
            currentTime = "12:00 AM"
        )

        // Act
        val result = repository.savePlace(place)

        // Assert
        assertTrue(result is Result.Success)
        coVerify { localDataSource.insertPlace(place.toEntity()) }
    }

    @Test
    fun `savePlace returns error when local insert fails`() = runTest(testDispatcher) {

        // Arrange
        val place =   PlaceModel(
            id = 1,
            fullName = "Asia/Tokyo",
            primaryName = "Tokyo",
            timeZoneId = "Asia/Tokyo",
            offsetSeconds = -14400,
            currentTime = "12:00 AM"
        )

        coEvery { localDataSource.insertPlace(any()) } throws RuntimeException("DB Error")

        // Act
        val result = repository.savePlace(place)

        //Assert
        assertTrue(result is Result.Error)
        assertEquals("DB Error", (result as Result.Error).exception.message)
    }

    @Test
    fun `searchPlaces returns local data if available`() = runTest(testDispatcher) {
        // Arrange
        val query = "New York"

        val localEntities = listOf(PlaceEntity(
            id = 1,
            fullName = "America/New_York",
            primaryName = "New York",
            timeZoneId = "America/New_York",
            offsetSeconds = -14400,
            currentTime = "12:00 AM"
        ))

        coEvery { localDataSource.searchPlace("%$query%") } returns localEntities

        // Act
        val result = repository.searchPlaces(query)

        // Assert
        assertTrue(result is Result.Success)
        assertEquals("New York", (result as Result.Success).data.firstOrNull()?.primaryName)
        coVerify(exactly = 1) { localDataSource.searchPlace("%$query%") }
        coVerify(exactly = 0) { remoteDataSource.searchPlaces(any()) }
    }

    @Test
    fun `searchPlaces fetches from remote when local is empty and inserts remote data`() = runTest(testDispatcher) {

        //Arrange
        val query = "Paris"
        val remoteDtos = listOf(PlaceDto(
            fullName = "Europe/Paris",
            primaryName = "Paris",
            timeZoneId = "Europe/Paris",
            offsetSeconds = -14400,
            currentTime = "12:00 AM")
        )
        val remoteEntities = remoteDtos.map { it.toEntity() }
        val updatedLocal = remoteEntities

        coEvery { localDataSource.searchPlace("%$query%") } returnsMany listOf(emptyList(), updatedLocal)
        coEvery { remoteDataSource.searchPlaces(query) } returns remoteDtos

        // Act
        val result = repository.searchPlaces(query)

        // Assert
        assertTrue(result is Result.Success)
        assertEquals("Paris", (result as Result.Success).data.firstOrNull()?.primaryName)

        coVerifySequence {
            localDataSource.searchPlace("%$query%")
            remoteDataSource.searchPlaces(query)
            localDataSource.insertAllPlaces(remoteEntities)
            localDataSource.searchPlace("%$query%")
        }
    }

    @Test
    fun `searchPlaces returns error when remote fetch fails`() = runTest(testDispatcher) {
        val query = "London"

        coEvery { localDataSource.searchPlace("%$query%") } returns emptyList()
        coEvery { remoteDataSource.searchPlaces(query) } throws IOException("Network down")

        val result = repository.searchPlaces(query)

        assertTrue(result is Result.Error)
        assertEquals("Network down", (result as Result.Error).exception.message)
    }*/
}
