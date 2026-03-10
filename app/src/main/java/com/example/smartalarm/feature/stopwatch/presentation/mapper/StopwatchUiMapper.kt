package com.example.smartalarm.feature.stopwatch.presentation.mapper

import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchLapModel
import com.example.smartalarm.feature.stopwatch.domain.model.StopwatchModel
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchLapUiModel
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchUiModel


 /**
 * UI Projection Extensions for the Stopwatch Domain.
 *
 * These extension functions bridge the Domain and Presentation layers by projecting
 * raw business models into UI-optimized state holders.
 *
 * ### Architectural Principle:
 * Adheres to the **Raw Data Projection** pattern. These functions are strictly
 * "String Agnostic"—they provide the numerical truth (ms, indices, percentages)
 * but delegate all visual formatting and localization to the View layer.
 */


/**
 * Projects a [StopwatchModel] into a discrete [StopwatchUiModel] snapshot.
 *
 * Extracts aggregate session metrics and triggers domain-level calculations
 * to prepare a passive state for UI consumption.
 */
fun StopwatchModel.toUiModel(): StopwatchUiModel {
    return StopwatchUiModel(
        elapsedMillis = elapsedTime,
        isRunning = isRunning,
        progress = getIndicatorProgress,
        laps = lapTimes.map { it.toUiModel() }
    )
}

/**
 * Transforms a [StopwatchLapModel] into a [StopwatchLapUiModel] for list rendering.
 *
 * Isolates the UI layer from domain-specific interval logic, ensuring stable
 * data structures for optimized RecyclerView or Compose updates.
 */
fun StopwatchLapModel.toUiModel(): StopwatchLapUiModel {
    return StopwatchLapUiModel(
        lapIndex = lapIndex,
        lapStartTimeMillis = lapStartTimeMillis,
        lapElapsedMillis = lapElapsedTimeMillis,
        lapEndTimeMillis = lapEndTimeMillis
    )
}