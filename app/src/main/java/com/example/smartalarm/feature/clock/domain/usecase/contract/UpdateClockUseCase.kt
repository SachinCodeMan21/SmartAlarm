package com.example.smartalarm.feature.clock.domain.usecase.contract

import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper

/**
 * Use case responsible for updating local time for a list of saved places.
 *
 * The implementation calculates the current local time for each place based on
 * its UTC offset using [SystemClockHelper].
 */
interface UpdateClockUseCase {

    /**
     * Calculates and returns a list of updated [PlaceModel]s with their
     * `currentTime` fields set to the formatted local time.
     *
     * @param savedPlaces List of [PlaceModel]s to update.
     * @return List of [PlaceModel]s with updated local time.
     */
    operator fun invoke(savedPlaces: List<PlaceModel>): List<PlaceModel>

}