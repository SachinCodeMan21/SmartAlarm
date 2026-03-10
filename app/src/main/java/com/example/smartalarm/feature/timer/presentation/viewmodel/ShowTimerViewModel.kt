package com.example.smartalarm.feature.timer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.R
import com.example.smartalarm.core.framework.analytics.AnalyticsHelper
import com.example.smartalarm.core.framework.analytics.ErrorLogger
import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.MyResult
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import com.example.smartalarm.feature.timer.data.mapper.TimerMapper
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.presentation.effect.ShowTimerEffect
import com.example.smartalarm.feature.timer.presentation.effect.ShowTimerEffect.*
import com.example.smartalarm.feature.timer.presentation.event.ShowTimerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.smartalarm.feature.timer.domain.usecase.TimerUseCase
import com.example.smartalarm.feature.timer.presentation.model.ShowTimerAnalyticsEvent
import com.example.smartalarm.feature.timer.presentation.model.TimerUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ShowTimerViewModel @Inject constructor(
    private val timerUseCase: TimerUseCase,
    private val resourceProvider: ResourceProvider,
    private val errorLogger: ErrorLogger,
    private val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    val uiState: StateFlow<TimerUiState> = timerUseCase.getAllTimers()
        .onEach { timerList ->
            // Trigger side effect if any timer is running
            if (timerList.any { it.isTimerRunning }) {
                postEffect(StartTimerForegroundNotification)
            }
        }
        .map { timerList ->
            // Pure transformation
            when {
                timerList.isEmpty() -> TimerUiState.Empty
                else -> TimerUiState.Success(timerList.map(TimerMapper::toUiModel))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TimerUiState.Loading
        )

    // For handling UI effects (like showing toasts, notifications)
    private val _uiEffect = MutableSharedFlow<ShowTimerEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    // Post UI effects for side actions like toasts, notifications
    private fun postEffect(effect: ShowTimerEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }

    init {
        analyticsHelper.logEvent(ShowTimerAnalyticsEvent.SHOW_SCREEN_VIEWED.eventName)
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
        }
    }

    // ------------------------------
    // Timer Operations (Fire and Forget)
    // ------------------------------

    private fun startTimer(timer: TimerModel) = viewModelScope.launch {
        if (timer.isTimerRunning) return@launch

        val result = timerUseCase.startTimer(timer)
        handleResult(
            result,
            successAction = {
                analyticsHelper.logEvent(
                    ShowTimerAnalyticsEvent.SHOW_START_TIMER.eventName,
                    ShowTimerAnalyticsEvent.Params.TIMER_ID to timer.timerId,
                    ShowTimerAnalyticsEvent.Params.REMAINING_TIME to timer.remainingTime,
                    ShowTimerAnalyticsEvent.Params.TARGET_TIME to timer.targetTime
                )
                postEffect(StartTimerForegroundNotification)
            },
            errorMessageResId = R.string.timer_start_failed
        )
    }

    private fun pauseTimer(timer: TimerModel) = viewModelScope.launch {
        val result = timerUseCase.pauseTimer(timer)
        handleResult(
            result,
            successAction = {
                analyticsHelper.logEvent(
                    ShowTimerAnalyticsEvent.SHOW_PAUSE_TIMER.eventName,
                    ShowTimerAnalyticsEvent.Params.TIMER_ID to timer.timerId,
                    ShowTimerAnalyticsEvent.Params.REMAINING_TIME to timer.remainingTime
                )
            },
            errorMessageResId = R.string.timer_restart_failed
        )
    }

    private fun restartTimer(timer: TimerModel) = viewModelScope.launch {
        val result = timerUseCase.restartTimer(timer)
        handleResult(
            result,
            successAction = {
                analyticsHelper.logEvent(
                    ShowTimerAnalyticsEvent.SHOW_RESTART_TIMER.eventName,
                    ShowTimerAnalyticsEvent.Params.TIMER_ID to timer.timerId,
                    ShowTimerAnalyticsEvent.Params.REMAINING_TIME to timer.remainingTime
                )
            },
            errorMessageResId = R.string.timer_restart_failed
        )
    }

    private fun snoozeTimer(timer: TimerModel) = viewModelScope.launch {
        val result = timerUseCase.snoozeTimer(timer)
        handleResult(
            result,
            successAction = {
                analyticsHelper.logEvent(
                    ShowTimerAnalyticsEvent.SHOW_SNOOZE_TIMER.eventName,
                        ShowTimerAnalyticsEvent.Params.TIMER_ID to timer.timerId,
                        ShowTimerAnalyticsEvent.Params.REMAINING_TIME to timer.remainingTime,
                        ShowTimerAnalyticsEvent.Params.TARGET_TIME to timer.snoozedTargetTime
                )
            },
            errorMessageResId = R.string.timer_snooze_failed
        )
    }

    private fun stopTimer(timer: TimerModel) = viewModelScope.launch {
        val result = timerUseCase.deleteTimer(timer)
        handleResult(
            result,
            successAction = {
                analyticsHelper.logEvent(
                    ShowTimerAnalyticsEvent.SHOW_STOP_TIMER.eventName,
                    ShowTimerAnalyticsEvent.Params.TIMER_ID to timer.timerId,
                    ShowTimerAnalyticsEvent.Params.REMAINING_TIME to timer.remainingTime
                )
            },
            errorMessageResId = R.string.timer_stop_failed
        )
    }


    private fun handleResult(
        result: MyResult<Unit, DataError>,
        successAction: (() -> Unit)? = null, // Optional lambda to execute on success
        errorMessageResId: Int? = null       // Optional error message resource ID
    ) {
        when (result) {
            is MyResult.Success -> {
                // Execute optional success logic (e.g., logging analytics)
                successAction?.invoke()
            }
            is MyResult.Error -> {
                val message = errorMessageResId?.let { resourceProvider.getString(it) } ?: "Unknown error"
                val throwable = if (result.error is DataError.Unexpected) {
                    result.error.throwable
                } else {
                    Exception("ShowTimer_Action_Error: $message (${result.error})")
                }
                errorLogger.recordException(throwable)
                postEffect(ShowError(message))
            }
        }
    }

}