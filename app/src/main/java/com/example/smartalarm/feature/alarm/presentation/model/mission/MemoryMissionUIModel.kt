package com.example.smartalarm.feature.alarm.presentation.model.mission

import androidx.annotation.ColorRes
import com.example.smartalarm.R

data class MemoryMissionUIModel(
    val totalSquares: Int = 0,
    val totalRounds: Int = 0,
    val currentRound: Int = 0,
    val timerProgress: Int = 100,
    val instruction: String = "",
    val squareColors: List<Int> = emptyList(),
    val isSquaresEnabled: Boolean = false,
    val countdownText: String? = null,
    @field:ColorRes
    val instructionColor: Int = R.color.black,
)
