package com.example.smartalarm.feature.clock.data.datasource.impl

import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceRemoteDataSource
import com.example.smartalarm.feature.clock.data.remote.api.GeoApifyApiService
import com.example.smartalarm.feature.clock.data.remote.dto.PlaceDto
import javax.inject.Inject


/**
 * Implementation of [PlaceRemoteDataSource] that retrieves places and their
 * timezone information from the GeoApify API in a single call.
 *
 * This class exists to provide the user with search results that include:
 * 1. The place details (formatted address, city, etc.).
 * 2. The current time at that place based on its timezone.
 *
 * By combining these pieces of data, the app can show the user not only the
 * place they searched for, but also the local time there, improving UX
 * for features like scheduling, travel planning, or time-sensitive decisions.
 *
 * @property geoApifyApiService The API service used to fetch place predictions.
 */
class PlaceRemoteDataSourceImpl @Inject constructor(
    private val geoApifyApiService: GeoApifyApiService,
    private val systemClockHelper: SystemClockHelper,
) : PlaceRemoteDataSource {

    override suspend fun searchPlaces(query: String): List<PlaceDto> {

        // We let the network exceptions (Timeout, 404, etc.) throw naturally
        val response = geoApifyApiService.getPlacePredictions(query)

        return response.features.map { feature ->

            val prop = feature.properties
            val tz = prop.timezone
            val primaryName = prop.city ?: prop.formatted.split(",").firstOrNull() ?: "Unknown"
            val currentTime = systemClockHelper.formatLocalTime(System.currentTimeMillis(), tz.offsetSeconds)

//            val currentTime = try {
//                val zoneId = ZoneId.of(tz.name)
//                ZonedDateTime.now(zoneId).format(DateTimeFormatter.ofPattern("hh:mm a"))
//            } catch (_: Exception) {
//                "---"
//            }

            PlaceDto(
                fullName = prop.formatted,
                primaryName = primaryName,
                timeZoneId = tz.name,
                offsetSeconds = tz.offsetSeconds,
                currentTime = currentTime
            )
        }
    }
}