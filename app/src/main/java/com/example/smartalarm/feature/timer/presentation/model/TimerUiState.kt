package com.example.smartalarm.feature.timer.presentation.model

sealed class TimerUiState {
    object Loading : TimerUiState()
    object Empty : TimerUiState()
    data class Success(val timers: List<ShowTimerUiModel>) : TimerUiState()
}