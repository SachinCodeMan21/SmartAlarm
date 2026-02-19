package com.example.smartalarm.feature.alarm.presentation.model.mission

import androidx.annotation.DrawableRes
import com.example.smartalarm.R

data class MathMissionUiModel(
    val roundText: String = "",
    val question: String = "",
    val instruction: String = "",
    val instructionColor: Int = android.R.color.black,
    val timerProgress: Int = 100,
    val isSubmitEnabled: Boolean = true,
    val isInputEnabled: Boolean = true,
    val clearInput: Boolean = false,
    @field:DrawableRes
    val statusImageRes: Int = R.drawable.start_img
)
