package com.example.smartalarm.feature.timer.presentation.uistate

sealed class ShowTimerUiState<out T>{
    object Loading : ShowTimerUiState<Nothing>() // Optional
    data class Success<T>(val data: T) : ShowTimerUiState<T>()
}
