package com.example.smartalarm.feature.stopwatch.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.feature.stopwatch.framework.jobmanager.contract.BlinkEffectJobManager
import com.example.smartalarm.feature.stopwatch.presentation.effect.StopwatchEffect
import com.example.smartalarm.feature.stopwatch.presentation.event.StopwatchEvent
import com.example.smartalarm.feature.stopwatch.presentation.mapper.StopwatchUiMapper
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchUiModel
import com.example.smartalarm.core.utility.provider.resource.contract.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import com.example.smartalarm.core.di.annotations.DefaultDispatcher
import com.example.smartalarm.core.utility.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.feature.stopwatch.domain.usecase.StopwatchUseCases
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing stopwatch state and coordinating UI behavior.
 *
 * Acts as the **single source of truth** for the stopwatch feature in an MVVM architecture.
 * It processes user-driven [StopwatchEvent]s, manages background jobs, and exposes
 * lifecycle-safe streams of UI state and one-off UI effects.
 *
 * Responsibilities include:
 * - Managing stopwatch lifecycle (start, pause, reset, restore).
 * - Recording lap times and updating elapsed time.
 * - Coordinating ticker and blinking background jobs.
 * - Controlling foreground service behavior when the UI is not visible.
 * - Handling notification permission flow.
 *
 * @property stopwatchUsecase Encapsulates stopwatch domain operations.
 * @property stopWatchUiMapper Maps domain models to UI-friendly models.
 * @property resourceProvider Provides localized string resources.
 * @property blinkEffectJobManager Controls blinking visual effects.
 * @property defaultDispatcher Dispatcher used for background operations.
 */
@HiltViewModel
class StopWatchViewModel @Inject constructor(
    private val stopwatchUsecase: StopwatchUseCases,
    private val stopWatchUiMapper: StopwatchUiMapper,
    private val resourceProvider: ResourceProvider,
    private val blinkEffectJobManager: BlinkEffectJobManager,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    val uiState: StateFlow<StopwatchUiModel> = stopwatchUsecase.getStopwatch()
        .map { model ->

            if (!model.isRunning && model.elapsedTime>0){
                startBlinkingJob()
            }else{
                stopBlinkingJob()
            }
            stopWatchUiMapper.mapToUiModel(model)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StopwatchUiModel()
        )

    private val _uiEffect = MutableSharedFlow<StopwatchEffect>(0)
    val uiEffect: Flow<StopwatchEffect> = _uiEffect.asSharedFlow()


    // Update State Methods
    private fun postEffect(effect: StopwatchEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }
    }


    fun handleEvent(event: StopwatchEvent) {
        when (event) {
            StopwatchEvent.ToggleRunState -> toggleRunState()
            StopwatchEvent.ResetStopwatch -> resetStopwatch()
            StopwatchEvent.RecordStopwatchLap -> recordStopwatchLap()
            StopwatchEvent.MoveToBackground -> stopBlinkingJob()
            StopwatchEvent.StartForegroundService -> postEffect(StopwatchEffect.StartForegroundService)
            StopwatchEvent.StopwatchForegroundService -> postEffect(StopwatchEffect.StopForegroundService)
        }
    }


    private fun toggleRunState() {
        val state = stopwatchUsecase.getCurrentStopwatch()
        if (state.isRunning) pauseStopwatch() else startStopwatch()
    }
    private fun startStopwatch() = viewModelScope.launch {
        //stopBlinkingJob()
        stopwatchUsecase.startStopwatch()
        postEffect(StopwatchEffect.StartForegroundService)
    }
    private fun pauseStopwatch() = viewModelScope.launch {
        stopwatchUsecase.pauseStopwatch()
    }
    private fun recordStopwatchLap() = viewModelScope.launch {
        stopwatchUsecase.lapStopwatch()
    }
    private fun resetStopwatch() = viewModelScope.launch {
        //stopBlinkingJob()
        stopwatchUsecase.deleteStopwatch()
        postEffect(StopwatchEffect.StopForegroundService)
    }


    // Job Update Methods
    private fun startBlinkingJob() = viewModelScope.launch(defaultDispatcher) {
        blinkEffectJobManager.startBlinking(
            scope = this,
            onVisibilityChanged = { postEffect(StopwatchEffect.BlinkVisibilityChanged(it)) }
        )
    }
    private fun stopBlinkingJob() {
        blinkEffectJobManager.stopBlinking()
        postEffect(StopwatchEffect.BlinkVisibilityChanged(true))
    }

}