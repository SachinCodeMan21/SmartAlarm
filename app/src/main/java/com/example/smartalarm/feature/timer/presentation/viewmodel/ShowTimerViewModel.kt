package com.example.smartalarm.feature.timer.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.core.di.annotations.DefaultDispatcher
import com.example.smartalarm.feature.timer.data.mapper.TimerMapper
import com.example.smartalarm.feature.timer.domain.facade.contract.TimerUseCasesFacade
import com.example.smartalarm.feature.timer.domain.model.TimerModel
import com.example.smartalarm.feature.timer.presentation.effect.ShowTimerEffect
import com.example.smartalarm.feature.timer.presentation.effect.ShowTimerEffect.*
import com.example.smartalarm.feature.timer.presentation.event.ShowTimerEvent
import com.example.smartalarm.feature.timer.presentation.model.ShowTimerUiModel
import com.example.smartalarm.feature.timer.presentation.uistate.ShowTimerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.smartalarm.core.model.Result
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
        if (result is Result.Error) {
            postEffect(ShowToast("Failed to start: ${result.exception.localizedMessage}"))
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


///**
// * The [ShowTimerViewModel] is responsible for managing the state and operations related to timers in the app.
// *
// * This [ViewModel] is responsible for orchestrating the interaction between the UI, the timer state, and background
// * operations like starting, stopping, or snoozing timers. It interacts with various use cases and components to manage
// * timers in a thread-safe, reactive manner and updates the UI based on user actions or timer state changes.
// *
// * Key Responsibilities:
// * - Manage the UI state (`_uiState`) of the timer screen, including the list of timers and their statuses.
// * - Handle events like starting, stopping, and snoozing timers, and update the timer state accordingly.
// * - Orchestrate the start and stop of timer jobs using [jobManager].
// * - Play or stop timer ringtones based on timer state.
// * - Interact with a foreground service to manage ongoing timer notifications.
// *
// * @param timerUseCase A facade for use cases related to timer operations (e.g., start, pause, stop).
// * @param timerStateManager Manages the in-memory state of timers using [StateFlow] and provides methods for updating
// *                          and restoring timers.
// * @param jobManager Handles starting and stopping background jobs for timers.
// * @param timerRingtoneHelper Plays and stops the ringtone for timers when they reach the end.
// * @param defaultDispatcher The default [CoroutineDispatcher] to be used for background tasks.
// */
//
//@HiltViewModel
//class ShowTimerViewModel @Inject constructor(
//    private val timerUseCase: TimerUseCasesFacade,
//    private val timerStateManager: ShowTimerStateManager,
//    private val jobManager: ShowTimerJobManager,
//    private val timerRingtoneHelper: TimerRingtonePlayer,
//    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
//) : ViewModel()
//{
//
//    private val _uiState = MutableStateFlow<ShowTimerUiState<List<ShowTimerUiModel>>>(ShowTimerUiState.Loading)
//    val uiState: StateFlow<ShowTimerUiState<List<ShowTimerUiModel>>> = _uiState
//
//
//    private val _uiEffect = MutableSharedFlow<ShowTimerEffect>(0)
//    val uiEffect = _uiEffect.asSharedFlow()
//
//    init {
//        // Start observing the Manager immediately.
//        // As soon as the Manager hydrates from the DB, the UI will show Success.
//        observeTimerState()
//    }
//
//    private fun observeTimerState() {
//
//        viewModelScope.launch {
//            timerStateManager.getTimersFlow().collect { timers ->
//                updateState(timers)
//            }
//        }
//        // 2. The "Cold Start" check - REFACTORED
///*        viewModelScope.launch {
//            // We wait for the first emission that isn't empty (or wait for the DB to speak)
//            val firstEmission = timerStateManager.getTimersFlow().first()
//
//            if (firstEmission.any { it.isTimerRunning }) {
//                Log.d("TAG", "Cold start: Running timers found, starting service")
//                postEffect(StartTimerForegroundNotification)
//            }
//        }*/
//    }
//
//    private fun updateState(timers: List<TimerModel>) {
//        val uiModels = timers.map(TimerMapper::toUiModel)
//        _uiState.value = ShowTimerUiState.Success(uiModels)
//    }
//
//    // ------------------------------
//    // Event Dispatcher
//    // ------------------------------
//
//    fun handleEvent(event: ShowTimerEvent) {
//        when (event) {
//            is ShowTimerEvent.AddNewTimer,
//            is ShowTimerEvent.HandleEmptyTimerList,
//            is ShowTimerEvent.HandleToolbarBackPressed -> postEffect(FinishActivity)
//
//            is ShowTimerEvent.StartTimer -> startTimer(event.timer)
//            is ShowTimerEvent.PauseTimer -> pauseTimer(event.timer)
//            is ShowTimerEvent.RestartTimer -> restartTimer(event.timer)
//            is ShowTimerEvent.SnoozeTimer -> snoozeTimer(event.timer)
//            is ShowTimerEvent.StopTimer -> stopTimer(event.timer)
//
//            is ShowTimerEvent.StopTimerUiUpdates ->{} //stopTimerUiUpdates()
//            else -> {}
//        }
//    }
//
//    // ------------------------------
//    // Timer Operations (Now "Fire and Forget" to DB)
//    // ------------------------------
//
//    private fun startTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {
//        if (timer.isTimerRunning) return@launch
//        val result = timerUseCase.startTimer(timer)
//        if (result is Result.Error) {
//            postEffect(ShowToast("Failed to start: ${result.exception.localizedMessage}"))
//        }else{
//            postEffect(StartTimerForegroundNotification)
//        }
//    }
//
//    private fun pauseTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {
//        timerUseCase.pauseTimer(timer)
//    }
//
//    private fun restartTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {
//        timerUseCase.restartTimer(timer)
//    }
//
//    private fun snoozeTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {
//        timerUseCase.snoozeTimer(timer)
//    }
//
//    private fun stopTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {
//        timerUseCase.deleteTimerById(timer)
//    }
//
//    private fun postEffect(effect: ShowTimerEffect) {
//        viewModelScope.launch { _uiEffect.emit(effect) }
//    }
//
//}




/*@HiltViewModel
class ShowTimerViewModel @Inject constructor(
    private val timerUseCase: TimerUseCasesFacade,
    private val timerStateManager: ShowTimerStateManager,
    private val jobManager: ShowTimerJobManager,
    private val timerRingtoneHelper: TimerRingtonePlayer,
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel()
{

    *//**
     * Mutable state representing the UI state of the timer list, which can either be in a loading state or display
     * a list of [ShowTimerUiModel] timers.
     *//*
    private val _uiState = MutableStateFlow<ShowTimerUiState<List<ShowTimerUiModel>>>(ShowTimerUiState.Loading)

    *//**
     * Public immutable state of the UI that observers can collect to react to changes in the timer state.
     *//*
    val uiState: StateFlow<ShowTimerUiState<List<ShowTimerUiModel>>> = _uiState

    *//**
     * Mutable shared flow for emitting UI effects (such as showing a toast or finishing the activity).
     *//*
    private val _uiEffect = MutableSharedFlow<ShowTimerEffect>(0)

    *//**
     * Public immutable shared flow for emitting UI effects.
     *//*
    val uiEffect = _uiEffect.asSharedFlow()

    private var isStateInitialized = false




    // --------------------------------
    // State Management
    // --------------------------------

    *//**
     * Updates the UI state based on the current list of timers.
     *
     * If there are no timers, the state is set to [ShowTimerUiState.Success] with an empty list.
     * Otherwise, the timers are mapped to [ShowTimerUiModel] objects for display.
     *
     * @param timers The list of timers to update the UI state with.
     *//*
    private fun updateState(timers: List<TimerModel> = timerStateManager.getTimers()) {
        //Log.d("TAG","updateState called with timerList size = ${timerStateManager.getTimers().size}")
        if (timers.isEmpty()) {
            _uiState.value = ShowTimerUiState.Success(emptyList())
        } else {
            val uiModels = timers.map(TimerMapper::toUiModel)
            _uiState.value = ShowTimerUiState.Success(uiModels)
        }
    }

    *//**
     * Posts a new effect to the UI, such as showing a toast or navigating to another screen.
     *
     * @param effect The effect to post.
     *//*
    private fun postEffect(effect: ShowTimerEffect) {
        viewModelScope.launch { _uiEffect.emit(effect) }

    }



    // ------------------------------
    // Event Dispatcher
    // ------------------------------

    *//**
     * Handles a timer-related event and dispatches the appropriate actions.
     *
     * This method listens for events such as starting, pausing, or stopping timers, and invokes the corresponding
     * methods for managing timers or UI effects.
     *
     * @param event The event to handle.
     *//*
    fun handleEvent(event: ShowTimerEvent) {
        when (event) {

            // Handle events that finish the activity
            is ShowTimerEvent.AddNewTimer,
            is ShowTimerEvent.HandleEmptyTimerList,
            is ShowTimerEvent.HandleToolbarBackPressed -> postEffect(FinishActivity)

            // Handle restoring timer state from storage
            is ShowTimerEvent.RestoreTimerState -> restoreTimers()

            // Handle individual timer operations
            is ShowTimerEvent.StartTimer -> startTimer(event.timer)
            is ShowTimerEvent.PauseTimer -> pauseTimer(event.timer)
            is ShowTimerEvent.RestartTimer -> restartTimer(event.timer)
            is ShowTimerEvent.SnoozeTimer -> snoozeTimer(event.timer)
            is ShowTimerEvent.StopTimer -> stopTimer(event.timer)
            is ShowTimerEvent.StopTimerUiUpdates -> stopTimerUiUpdates()

            // Handle foreground service events
            is ShowTimerEvent.StartTimerForegroundService -> startForegroundService()
            is ShowTimerEvent.StopTimerForegroundService -> stopForegroundService()
        }
    }

    // ------------------------------
    // Timer Operations
    // ------------------------------

    *//**
     * Restores timers from the use case and updates the timer state.
     *
     * This method retrieves all the user's timers, restores them, and starts the associated background jobs for
     * any running timers.
     *//*
    private fun restoreTimers() = viewModelScope.launch {

        if (!isStateInitialized){

            val allUserTimers = timerUseCase.getAllTimers()
                .catch { e -> postEffect(ShowToast("Failed to restore timers: ${e.localizedMessage}")) }
                .firstOrNull().orEmpty()

            // Restore timers in the state manager
            timerStateManager.restoreTimers(allUserTimers)

            isStateInitialized = true

        }

        // Start background jobs for running timers
        timerStateManager.getTimers().forEach { timer ->
            if (timer.isTimerRunning) startTimerJob()
        }

        // Start the reactive collection for state updates after restore is complete
        timerStateManager.getTimersFlow().collect { timers ->
            updateState(timers)
            updateRingtone()
        }
    }

    *//**
     * Starts a timer and begins the associated background job.
     *
     * If the timer is already running, no action is taken. If successful, the timer's state is updated and a job
     * is started.
     *
     * @param timer The timer to start.
     *//*
    private fun startTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {

        if (timer.isTimerRunning) return@launch

        when (val result = timerUseCase.startTimer(timer)) {
            is Result.Success -> {
                val updatedTimer = result.data
                timerStateManager.updateTimer(updatedTimer)
                startTimerJob()
            }
            is Result.Error -> postEffect(ShowToast("Failed to start timer: ${result.exception.localizedMessage}"))
        }
    }

    *//**
     * Pauses the specified timer and updates its state in the timer state manager.
     *
     * This method calls the [TimerUseCasesFacade.pauseTimer] function to pause the timer. If the operation is successful,
     * the timer's state is updated in the [timerStateManager]. If there's an error, a toast message is posted to notify
     * the user of the failure.
     *
     * @param timer The timer to pause.
     *//*
    private fun pauseTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {
        handleOperationAndSave(timer, "pause", timerUseCase::pauseTimer)
    }

    *//**
     * Restarts the specified timer and updates its state in the timer state manager.
     *
     * This method calls the [TimerUseCasesFacade.restartTimer] function to restart the timer. Upon success, the timer's state
     * is updated in the [timerStateManager]. Any errors encountered during the operation are reported via a toast message.
     *
     * @param timer The timer to restart.
     *//*
    private fun restartTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {
        handleOperationAndSave(timer, "restart", timerUseCase::restartTimer)
    }

    *//**
     * Snoozes the specified timer and updates its state in the timer state manager.
     *
     * This method invokes the [TimerUseCasesFacade.snoozeTimer] function to snooze the timer. If successful, the timer's state
     * is updated in the [timerStateManager]. In case of an error, a toast message is shown to notify the user of the failure.
     *
     * @param timer The timer to snooze.
     *//*
    private fun snoozeTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {
        when (val result = timerUseCase.snoozeTimer(timer)) {
            is Result.Success -> {
                timerStateManager.updateTimer(result.data)
            }
            is Result.Error -> {
                postEffect(ShowToast("Failed to Snooze timer: ${result.exception.localizedMessage}"))
            }
        }
    }

    *//**
     * Stops the specified timer and removes it from the state and job manager.
     *
     * This method stops the background job associated with the timer and deletes it using the
     * [TimerUseCasesFacade.deleteTimerById]. If successful, the timer is removed from the [timerStateManager].
     * If there’s an error, a toast message is shown to the user.
     *
     * @param timer The timer to stop and remove.
     *//*
    private fun stopTimer(timer: TimerModel) = viewModelScope.launch(defaultDispatcher) {
        when (val result = timerUseCase.deleteTimerById(timer)) {
            is Result.Success -> timerStateManager.removeTimer(timer.timerId)
            is Result.Error -> postEffect(ShowToast("Failed to delete timer: ${result.exception.localizedMessage}"))
        }
    }


    // ------------------------------
    // Job Management
    // ------------------------------

    *//**
     * Starts a background job for the specified timer.
     *
     * The job periodically updates the timer state, ticking the timer every interval.
     *
     *//*
    private fun startTimerJob() = viewModelScope.launch(defaultDispatcher) {
        jobManager.startTimerTickerJob(
            scope = viewModelScope,
            shouldContinue = { timerStateManager.hasRunningActiveTimers() || timerStateManager.hasRunningCompletedTimers() },
            onTick = { timerStateManager.tickAllRunningTimers() }
        )
    }

    *//**
     * Stops all active jobs and clears the timer state.
     *//*
    private fun stopTimerUiUpdates() {
        //jobManager.clearJobs()
        jobManager.stopTimerTickerJob()
        viewModelScope.launch(defaultDispatcher) {
            timerStateManager.clearTimers()
        }
    }


    // ------------------------------
    // Foreground Service Effects
    // ------------------------------

    *//**
     * Starts a foreground service to notify the user about an ongoing timer.
     *//*
    private fun startForegroundService() {
//        if (timerStateManager.getTimers().any{ it.isTimerRunning }) {
//            postEffect(StartTimerForegroundNotification)
//        }
        if (timerStateManager.hasActiveTimers() || timerStateManager.hasCompletedTimers()){
            postEffect(StartTimerForegroundNotification)
        }
    }

    *//**
     * Stops the foreground service notification.
     *//*
    private fun stopForegroundService() {
        postEffect(StopTimerForegroundNotification)
    }



    // ------------------------------
    // Ringtone Management
    // ------------------------------

    *//**
     * Updates the ringtone based on the current state of the timers.
     *
     * If any timer is running and its time has elapsed, the default ringtone will play. If no timer is running
     * or all timers have time remaining, the ringtone will stop.
     *//*
    private fun updateRingtone() {
        val shouldPlay = timerStateManager.getTimers()
            .any { it.remainingTime <= 0  && it.isTimerRunning }
        if (shouldPlay) {
            timerRingtoneHelper.playDefaultTimer()
        } else {
            timerRingtoneHelper.stop()
        }
    }


    // ------------------------------
    // Utility Functions
    // ------------------------------

    *//**
     * Handles operations like pause, restart, and snooze for a timer, and updates the timer state.
     *
     * This method stops any active background job, performs the operation, and updates the timer in the state manager.
     *
     * @param timer The timer to operate on.
     * @param operationName The name of the operation (for logging or error handling).
     * @param operation The operation to execute on the timer (pause, restart, etc.).
     *//*
    private suspend fun handleOperationAndSave(
        timer: TimerModel,
        operationName: String,
        operation: suspend (TimerModel) -> Result<TimerModel>
    ) {
        when (val result = operation(timer)) {
            is Result.Success -> {
                val updated = result.data
                timerStateManager.updateTimer(updated)
            }
            is Result.Error -> {
                postEffect(ShowToast("Failed to $operationName timer: ${result.exception.localizedMessage}"))
            }
        }
    }

}*/
