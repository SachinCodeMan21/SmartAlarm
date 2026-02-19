package com.example.smartalarm.feature.clock.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.smartalarm.feature.clock.data.local.entity.ClockEntity

/**
 * Data Access Object (DAO) for managing clock-related database operations.
 */
@Dao
interface ClockDao {

    /**
     * Retrieves all saved clock places from the database.
     *
     * @return A list of [ClockEntity] representing all saved places.
     */
    @Query("Select * from clock_table")
    suspend fun getAllSavedTimeZones(): List<ClockEntity>

    /**
     * Inserts or updates a place in the database.
     *
     * If the entity already exists, it will be updated; otherwise, inserted.
     *
     * @param clockEntity The [ClockEntity] to be inserted or updated.
     */
    @Upsert
    suspend fun insertTimeZone(clockEntity: ClockEntity)

    /**
     * Deletes a place from the database by its ID.
     *
     * @param placeId The ID of the place to delete.
     */
    @Query("Delete From clock_table Where id = :placeId")
    suspend fun deleteTimeZoneById(placeId: Long)

}
