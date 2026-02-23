package com.example.smartalarm.feature.clock.data.datasource.impl

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
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
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceRemoteDataSourceImplTest {

//    @MockK
//    private lateinit var googleApiService: GoogleApiService
//    private lateinit var dataSource: PlaceRemoteDataSourceImpl
//    private val testDispatcher = StandardTestDispatcher()
//
//    @Before
//    fun setUp() {
//        Dispatchers.setMain(testDispatcher)
//        MockKAnnotations.init(this)
//        dataSource = PlaceRemoteDataSourceImpl(googleApiService)
//    }
//
//    @After
//    fun tearDown(){
//        Dispatchers.resetMain()
//        unmockkAll()
//    }
//
//    @Test
//    fun `searchPlaces returns empty list when ZERO_RESULTS`() = runTest {
//
//        // Arrange
//        val predictionResponse = PlacesResponse(predictions = emptyList(), status = "ZERO_RESULTS")
//        coEvery { googleApiService.getPlacePredictions(any()) } returns predictionResponse
//
//        // Act
//        val result = dataSource.searchPlaces("nothing")
//
//        // Assert
//        assertTrue(result.isEmpty())
//    }
//
//    @Test
//    fun `searchPlaces throws when predictions status is not OK`() = runTest {
//        // Arrange
//        val predictionResponse = PlacesResponse(predictions = emptyList(), status = "INVALID_REQUEST")
//        coEvery { googleApiService.getPlacePredictions(any()) } returns predictionResponse
//
//        // Act & Assert
//        val exception = assertFailsWith<Exception> {
//            dataSource.searchPlaces("invalid")
//        }
//
//        assertTrue(exception.message?.contains("Prediction failed") == true)
//    }
//
//    @Test
//    fun `searchPlaces throws when place details status is not OK`() = runTest {
//
//        // Arrange
//        val prediction = Prediction(
//            placeId = "abc123",
//            description = "Some Place",
//            structuredFormatting = StructuredFormatting("Some Place", "Some Country")
//        )
//        val predictionResponse = PlacesResponse(predictions = listOf(prediction), status = "OK")
//        val detailsResponse = PlaceDetailsResponse(
//            status = "ERROR",
//            result = PlaceDetailResult(geometry = Geometry(Location(0.0, 0.0)))
//        )
//
//        coEvery { googleApiService.getPlacePredictions(any()) } returns predictionResponse
//        coEvery { googleApiService.getPlaceDetails(prediction.placeId) } returns detailsResponse
//
//        // Act & Assert
//        val exception = assertFailsWith<Exception> {
//            dataSource.searchPlaces("place")
//        }
//
//        assertTrue(exception.message?.contains("Details failed") == true)
//    }
//
//    @Test
//    fun `searchPlaces throws when timezone status is not OK`() = runTest {
//        // Arrange
//        val prediction = Prediction(
//            placeId = "abc123",
//            description = "Some Place",
//            structuredFormatting = StructuredFormatting("Some Place", "Some Country")
//        )
//        val predictionResponse = PlacesResponse(predictions = listOf(prediction), status = "OK")
//        val location = Location(10.0, 20.0)
//        val detailsResponse = PlaceDetailsResponse(
//            status = "OK",
//            result = PlaceDetailResult(geometry = Geometry(location))
//        )
//        val timeZoneResponse = TimeZoneResponse(
//            dstOffset = 0,
//            rawOffset = 0,
//            status = "ERROR",
//            timeZoneId = "",
//            timeZoneName = ""
//        )
//
//        coEvery { googleApiService.getPlacePredictions(any()) } returns predictionResponse
//        coEvery { googleApiService.getPlaceDetails(prediction.placeId) } returns detailsResponse
//        coEvery { googleApiService.getTimeZone("${location.lat},${location.lng}", any()) } returns timeZoneResponse
//
//        // Act + Assert
//        val exception = assertFailsWith<Exception> {
//            dataSource.searchPlaces("place")
//        }
//
//        assertTrue(exception.message?.contains("Time zone failed") == true)
//    }
//
//    @Test
//    fun `searchPlaces returns PlaceDto when all responses are OK`() = runTest {
//        // Arrange
//        val prediction = Prediction(
//            placeId = "place123",
//            description = "Paris, France",
//            structuredFormatting = StructuredFormatting("Paris", "France")
//        )
//        val predictionResponse = PlacesResponse(predictions = listOf(prediction), status = "OK")
//
//        val location = Location(48.8566, 2.3522)
//        val geometry = Geometry(location)
//        val details = PlaceDetailResult(geometry)
//        val detailsResponse = PlaceDetailsResponse(result = details, status = "OK")
//
//        val timeZoneResponse = TimeZoneResponse(
//            dstOffset = 0,
//            rawOffset = 3600,
//            status = "OK",
//            timeZoneId = "Europe/Paris",
//            timeZoneName = "Central European Standard Time"
//        )
//
//        coEvery { googleApiService.getPlacePredictions(any()) } returns predictionResponse
//        coEvery { googleApiService.getPlaceDetails(prediction.placeId) } returns detailsResponse
//        coEvery {
//            googleApiService.getTimeZone("${location.lat},${location.lng}", any())
//        } returns timeZoneResponse
//
//        // Act
//        val result = dataSource.searchPlaces("paris")
//
//        // Assert
//        assertEquals(1, result.size)
//        val dto = result.first()
//        assertEquals("Paris", dto.primaryName)
//        assertEquals("Europe/Paris", dto.timeZoneId)
//        assertEquals(3600, dto.offsetSeconds)
//        assert(dto.currentTime.isNotBlank())
//    }
//
//    @Test
//    fun `searchPlaces sets currentTime as Incorrect TimeZone when conversion fails`() = runTest {
//        // Given
//        val prediction = Prediction(
//            placeId = "place123",
//            description = "Test City",
//            structuredFormatting = StructuredFormatting("Test City", "Nowhere")
//        )
//        val predictionResponse = PlacesResponse(predictions = listOf(prediction), status = "OK")
//
//        val location = Location(0.0, 0.0)
//        val geometry = Geometry(location)
//        val details = PlaceDetailResult(geometry)
//        val detailsResponse = PlaceDetailsResponse(result = details, status = "OK")
//
//        val timeZoneResponse = TimeZoneResponse(
//            dstOffset = 0,
//            rawOffset = 0,
//            status = "OK",
//            timeZoneId = "Invalid/Zone", // Invalid zone
//            timeZoneName = "Fake Time"
//        )
//
//        coEvery { googleApiService.getPlacePredictions(any()) } returns predictionResponse
//        coEvery { googleApiService.getPlaceDetails(any()) } returns detailsResponse
//        coEvery { googleApiService.getTimeZone(any(), any()) } returns timeZoneResponse
//
//        // When
//        val result = dataSource.searchPlaces("fail-timezone")
//
//        // Then
//        assertEquals(1, result.size)
//        val dto = result.first()
//        assertEquals("Incorrect TimeZone", dto.currentTime)
//    }
}

