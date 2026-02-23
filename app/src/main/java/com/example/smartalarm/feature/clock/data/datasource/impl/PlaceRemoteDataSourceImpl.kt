package com.example.smartalarm.feature.clock.data.datasource.impl

import com.example.smartalarm.core.exception.DataError
import com.example.smartalarm.core.exception.GeneralErrorMapper
import com.example.smartalarm.core.exception.MyResult
import com.example.smartalarm.feature.clock.data.datasource.contract.PlaceRemoteDataSource
import com.example.smartalarm.feature.clock.data.remote.api.GeoApifyApiService
import com.example.smartalarm.feature.clock.data.remote.dto.PlaceDto
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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
    private val geoApifyApiService: GeoApifyApiService
) : PlaceRemoteDataSource {

    /**
     * Searches for places and maps network exceptions to DataError.Network.
     */
    override suspend fun searchPlaces(query: String): MyResult<List<PlaceDto>, DataError> {
        return try {
            val response = geoApifyApiService.getPlacePredictions(query)

            val dtos = response.features.map { feature ->
                val prop = feature.properties
                val tz = prop.timezone

                val primaryName = prop.city ?: prop.formatted.split(",").firstOrNull() ?: "Unknown"

                // Time calculation is local logic, so we keep a small internal try-catch
                // just for the ZoneId to prevent a bad API string from crashing the whole list.
                val currentTime = try {
                    val zoneId = ZoneId.of(tz.name)
                    ZonedDateTime.now(zoneId).format(DateTimeFormatter.ofPattern("hh:mm a"))
                } catch (_: Exception) {
                    "---"
                }

                PlaceDto(
                    fullName = prop.formatted,
                    primaryName = primaryName,
                    timeZoneId = tz.name,
                    offsetSeconds = tz.offsetSeconds.toLong(),
                    currentTime = currentTime
                )
            }

            MyResult.Success(dtos)

        } catch (e: Exception) {
            // Map Retrofit/OkHttp exceptions (Timeout, No Internet, 401, etc.)
            // to our DataError.Network or DataError.Unexpected
            MyResult.Error(GeneralErrorMapper.mapNetworkException(e))
        }
    }
}


/*
class PlaceRemoteDataSourceImpl @Inject constructor(
    private val geoApifyApiService: GeoApifyApiService
) : PlaceRemoteDataSource {

    */
/**
     * Searches for places matching the provided [query] string and enriches
     * them with timezone-based current time.
     *
     * @param query The user's search keyword or phrase.
     * @return A list of [PlaceDto] objects containing both place info and
     *         the current local time.
     *
     * @throws Exception If the API call fails or the response cannot be parsed.
     *//*

    override suspend fun searchPlaces(query: String): List<PlaceDto> {
        val response = geoApifyApiService.getPlacePredictions(query)

        return response.features.map { feature ->
            val prop = feature.properties
            val tz = prop.timezone

            val primaryName = prop.city ?: prop.formatted.split(",").firstOrNull() ?: "Unknown"

            val currentTime = try {
                val zoneId = ZoneId.of(tz.name)
                ZonedDateTime.now(zoneId).format(DateTimeFormatter.ofPattern("hh:mm a"))
            } catch (_: Exception) {
                "---"
            }

            PlaceDto(
                fullName = prop.formatted,
                primaryName = primaryName,
                timeZoneId = tz.name,
                offsetSeconds = tz.offsetSeconds.toLong(),
                currentTime = currentTime
            )
        }
    }
}*/
