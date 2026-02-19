package com.example.smartalarm.feature.clock.data.datasource.contract

import com.example.smartalarm.feature.clock.data.local.entity.ClockEntity
import com.example.smartalarm.feature.clock.data.local.dao.ClockDao


/**
 * Data source interface for accessing local clock-related data.
 *
 * Acts as an abstraction layer over the [ClockDao], allowing the repository layer
 * to interact with local storage without being coupled directly to the database implementation.
 */
interface ClockLocalDataSource {

    /**
     * Retrieves all saved time zones from the local database.
     *
     * @return A list of [ClockEntity] objects.
     */
    suspend fun getAllSavedTimeZones(): List<ClockEntity>

    /**
     * Inserts a time zone into the local database.
     *
     * @param entity The [ClockEntity] to insert.
     */
    suspend fun insertTimeZone(entity: ClockEntity)

    /**
     * Deletes a time zone from the local database by its ID.
     *
     * @param id The ID of the time zone to delete.
     */
    suspend fun deleteTimeZoneById(id: Long)
}

