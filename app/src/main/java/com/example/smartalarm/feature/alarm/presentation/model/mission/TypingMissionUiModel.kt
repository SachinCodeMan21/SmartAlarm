package com.example.smartalarm.feature.alarm.presentation.model.mission

import android.text.Spannable
import android.text.SpannableString

data class TypingMissionUiModel(
    val roundText: String = "",
    val currentParagraph: String = "",
    val inputText: String = "",
    val overlaySpannable: Spannable = SpannableString(""),
    val feedback: String = "",
    val timerProgress : Int = 0,
    val isInputEnabled: Boolean = true,
    val isSubmitEnabled: Boolean = true,
)