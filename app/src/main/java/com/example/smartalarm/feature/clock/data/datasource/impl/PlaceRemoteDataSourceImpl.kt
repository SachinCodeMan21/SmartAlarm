package com.example.smartalarm.feature.clock.data.datasource.impl

import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceRemoteDataSource
import com.example.smartalarm.feature.clock.data.remote.api.GeoApifyApiService
import com.example.smartalarm.feature.clock.data.remote.dto.GeoTimezone
import com.example.smartalarm.feature.clock.data.remote.dto.PlaceDto
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
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
) : PlaceRemoteDataSource {

    override suspend fun searchPlaces(query: String): List<PlaceDto> {

        // We let the network exceptions (Timeout, 404, etc.) throw naturally
        val response = geoApifyApiService.getPlacePredictions(query)

        return response.features.map { feature ->

            val prop = feature.properties
            val tz = prop.timezone
            val primaryName = prop.city ?: prop.formatted.split(",").firstOrNull() ?: "Unknown"

            // Determine if the location is in Daylight Saving Time (DST) or Standard Time
            val currentDate = LocalDateTime.now()
            val offsetSeconds = getOffsetSeconds(tz, currentDate)

            PlaceDto(
                fullName = prop.formatted,
                primaryName = primaryName,
                timeZoneId = tz.name,
                offsetSeconds = offsetSeconds,
            )

        }
    }
    private fun getOffsetSeconds(tz: GeoTimezone, currentDate: LocalDateTime): Int {
        return if (isInDST(currentDate, tz.name)) {
            // Use DST offset if the current date is during DST
            tz.offsetDstSeconds ?: tz.offsetStdSeconds  // Fallback to standard offset if DST offset is not available
        } else {
            // Use Standard offset if not in DST
            tz.offsetStdSeconds
        }
    }
    private fun isInDST(currentDate: LocalDateTime, timezoneId: String): Boolean {
        val zoneId = ZoneId.of(timezoneId)
        val zonedDateTime = ZonedDateTime.of(currentDate, zoneId)

        // Check if the current offset is different from the standard time offset
        val standardOffset = zonedDateTime.zone.rules.getOffset(zonedDateTime.toInstant()).totalSeconds
        val dstOffset = zonedDateTime.zone.rules.getOffset(zonedDateTime.toInstant().plusSeconds(86400)).totalSeconds

        return dstOffset != standardOffset
    }

}