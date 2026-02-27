package com.example.smartalarm.feature.timer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.core.framework.di.annotations.DefaultDispatcher
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.feature.timer.data.mapper.TimerMapper
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.presentation.effect.ShowTimerEffect
import com.example.smartalarm.feature.timer.presentation.effect.ShowTimerEffect.*
import com.example.smartalarm.feature.timer.presentation.event.ShowTimerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.smartalarm.feature.timer.domain.usecase.TimerUseCase
import com.example.smartalarm.feature.timer.presentation.model.TimerUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ShowTimerViewModel @Inject constructor(
    private val timerUseCase: TimerUseCase,  // Use case for timer operations
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    val uiState: StateFlow<TimerUiState> = timerUseCase.getAllTimers()
        .map { timerList ->
            if (timerList.isEmpty()) {
                TimerUiState.Empty
            } else {
                // Start the foreground service if there are active timers
                val isAnyTimerRunning = timerList.any { it.isTimerRunning }
                if (isAnyTimerRunning) {
                    postEffect(StartTimerForegroundNotification)
                }
                TimerUiState.Success(timerList.map(TimerMapper::toUiModel))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TimerUiState.Loading // Start with Loading!
        )


    // For handling UI effects (like showing toasts, notifications)
    private val _uiEffect = MutableSharedFlow<ShowTimerEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    // Post UI effects for side actions like toasts, notifications
    private fun postEffect(effect: ShowTimerEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }


    // ------------------------------
    // Event Dispatcher
    // ------------------------------

    fun handleEvent(event: ShowTimerEvent) {
        when (event) {
            is ShowTimerEvent.AddNewTimer,
            is ShowTimerEvent.HandleEmptyTimerList,
            is ShowTimerEvent.HandleToolbarBackPressed -> postEffect(FinishActivity)

            is ShowTimerEvent.StartTimer -> startTimer(event.timer)
            is ShowTimerEvent.PauseTimer -> pauseTimer(event.timer)
            is ShowTimerEvent.RestartTimer -> restartTimer(event.timer)
            is ShowTimerEvent.SnoozeTimer -> snoozeTimer(event.timer)
            is ShowTimerEvent.StopTimer -> stopTimer(event.timer)
            else -> {}
        }
    }

    // ------------------------------
    // Timer Operations (Fire and Forget)
    // ------------------------------

    private fun startTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {
        if (timer.isTimerRunning) return@launch
        val result = timerUseCase.startTimer(timer)
        if (result is MyResult.Error) {
            postEffect(ShowError(result.error))
        } else {
            postEffect(StartTimerForegroundNotification)
        }
    }

    private fun pauseTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {
        timerUseCase.pauseTimer(timer)
    }

    private fun restartTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {
        timerUseCase.restartTimer(timer)
    }

    private fun snoozeTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {
        timerUseCase.snoozeTimer(timer)
    }

    private fun stopTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {
        timerUseCase.deleteTimer(timer)
    }

}