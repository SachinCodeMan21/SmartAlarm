package com.example.smartalarm.feature.clock.data.datasource.impl

import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceRemoteDataSource
import com.example.smartalarm.feature.clock.data.remote.api.GoogleApiService
import com.example.smartalarm.feature.clock.data.remote.dto.response.PlaceDto
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Implementation of [PlaceRemoteDataSource] that communicates with Google APIs via Retrofit.
 *
 * @property googleApiService Retrofit service to interact with Google Places and Time Zone APIs.
 */
class PlaceRemoteDataSourceImpl @Inject constructor(
    private val googleApiService: GoogleApiService
) : PlaceRemoteDataSource {

    /**
     * Fetches a list of [PlaceDto] by chaining multiple remote calls:
     * - Autocomplete predictions
     * - Place details
     * - Time zone info
     *
     * This method handles all network orchestration and returns structured place data.
     *
     * @param query The search string entered by the user.
     * @return A list of fully built [PlaceDto]s.
     * @throws Exception if any of the remote responses fail.
     */
    override suspend fun searchPlaces(query: String): List<PlaceDto> {
        val predictions = googleApiService.getPlacePredictions(query)

        if (predictions.status == "ZERO_RESULTS") return emptyList()
        if (predictions.status != "OK") throw Exception("Prediction failed: ${predictions.status}")

        return predictions.predictions.map { prediction ->
            val details = googleApiService.getPlaceDetails(prediction.placeId)
            if (details.status != "OK") throw Exception("Details failed: ${details.status}")

            val location = details.result.geometry.location
            val timestamp = System.currentTimeMillis() / 1000

            val timeZone = googleApiService.getTimeZone("${location.lat},${location.lng}", timestamp)
            if (timeZone.status != "OK") throw Exception("Time zone failed: ${timeZone.status}")

            val currentTime = try {
                val zoneId = ZoneId.of(timeZone.timeZoneId)
                ZonedDateTime.now(zoneId).format(DateTimeFormatter.ofPattern("hh:mm a"))
            } catch (e: Exception) {
                "Incorrect TimeZone"
            }

            PlaceDto(
                fullName = prediction.description,
                primaryName = prediction.structuredFormatting.primaryText,
                timeZoneId = timeZone.timeZoneId,
                offsetSeconds = timeZone.rawOffset + timeZone.dstOffset,
                currentTime = currentTime
            )
        }
    }
}

