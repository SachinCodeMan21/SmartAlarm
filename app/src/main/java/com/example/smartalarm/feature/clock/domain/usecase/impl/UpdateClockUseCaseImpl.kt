package com.example.smartalarm.feature.clock.domain.usecase.impl

import com.example.smartalarm.core.utility.systemClock.contract.SystemClockHelper
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.domain.usecase.contract.UpdateClockUseCase
import javax.inject.Inject


class UpdateClockUseCaseImpl @Inject constructor(
    private val clockProvider: SystemClockHelper
) : UpdateClockUseCase {
    /**
     * Calculates local times for a list of saved places.
     */
    override operator fun invoke(savedPlaces: List<PlaceModel>): List<PlaceModel> {
        val now = clockProvider.getCurrentTime()
        return savedPlaces.map { place ->
            place.copy(currentTime = clockProvider.formatLocalTime(now, place.offsetSeconds))
        }
    }
}