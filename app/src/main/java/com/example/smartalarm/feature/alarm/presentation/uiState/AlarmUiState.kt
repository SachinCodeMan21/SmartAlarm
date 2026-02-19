package com.example.smartalarm.feature.alarm.presentation.uiState

import com.example.smartalarm.feature.alarm.presentation.model.home.AlarmUiModel

sealed class AlarmUiState {

    object Loading : AlarmUiState()
    object Empty : AlarmUiState()

    data class Success(val alarms: List<AlarmUiModel>) : AlarmUiState()

    data class Error(val message: String) : AlarmUiState()
}
