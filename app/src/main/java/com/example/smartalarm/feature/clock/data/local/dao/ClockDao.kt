package com.example.smartalarm.feature.clock.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.smartalarm.feature.clock.data.local.entity.ClockEntity

/**
 * Data Access Object (DAO) responsible for managing
 * clock and time zone data stored in the local Room database.
 *
 * Provides methods for retrieving, inserting, updating,
 * and deleting saved time zone entries.
 */
@Dao
interface ClockDao {

    /**
     * Retrieves all saved time zones from the database.
     *
     * @return A list of [ClockEntity] representing
     * all persisted clock entries.
     */
    @Query("SELECT * FROM clock_table")
    suspend fun getAllSavedTimeZones(): List<ClockEntity>

    /**
     * Inserts the given [ClockEntity] into the database.
     *
     * If a record with the same primary key already exists,
     * it will be updated.
     *
     * @param clockEntity The entity to insert or update.
     */
    @Upsert
    suspend fun insertTimeZone(clockEntity: ClockEntity)

    /**
     * Deletes a saved time zone entry by its unique identifier.
     *
     * @param placeId The primary key of the record to remove.
     */
    @Query("DELETE FROM clock_table WHERE id = :placeId")
    suspend fun deleteTimeZoneById(placeId: Long)

}
