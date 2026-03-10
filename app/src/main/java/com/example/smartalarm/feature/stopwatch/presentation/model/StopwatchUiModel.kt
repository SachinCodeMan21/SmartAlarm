package com.example.smartalarm.feature.stopwatch.presentation.model

/**
 * Encapsulates the visual state of the stopwatch as a raw data snapshot.
 *
 * Implements the **Passive UI State** pattern. This model carries pre-calculated
 * metrics (like progress percentage) to keep the UI layer logic-light, but avoids
 * data transformation like String formatting. This ensures the model remains
 * flexible across different Locales and UI components.
 */
data class StopwatchUiModel(
    val elapsedMillis: Long = 0L,
    val isRunning: Boolean = false,
    val progress: Int = 0,
    val laps: List<StopwatchLapUiModel> = emptyList()
)