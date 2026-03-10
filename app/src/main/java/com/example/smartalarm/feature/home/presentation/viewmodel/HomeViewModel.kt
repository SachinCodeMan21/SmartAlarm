package com.example.smartalarm.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.R
import com.example.smartalarm.core.framework.notification.model.NotificationIntentData
import com.example.smartalarm.core.framework.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.feature.home.presentation.effect.HomeEffect
import com.example.smartalarm.feature.home.presentation.effect.HomeEffect.*
import com.example.smartalarm.feature.home.presentation.event.HomeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages navigation state and transient UI events for the Home screen.
 * * This ViewModel uses a MVI-lite approach, processing [HomeEvent]s to maintain
 * navigation persistence across app sessions and emitting [HomeEffect]s for
 * one-time UI actions (e.g., animations, specific navigation triggers).
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModel() {

    // Internal flow for one-time events that shouldn't be replayed on configuration change
    private val _uiEffect = MutableSharedFlow<HomeEffect>(0)
    val uiEffect = _uiEffect.asSharedFlow()

    private fun postEffect(homeEffect: HomeEffect){
        viewModelScope.launch { _uiEffect.emit(homeEffect) }
    }



    // --------------------------------------------------------------------------
    // Event Dispatcher
    // --------------------------------------------------------------------------

    /**
     * Entry point for all UI interactions.
     * Centralizes event processing to ensure predictable state transitions.
     */
    fun handleEvent(homeEvent: HomeEvent) {
        when (homeEvent) {
            is HomeEvent.RestoreLastOpenedDestination -> restoreLastOpenedHomeDestination()
            is HomeEvent.NavigateFromNotification -> handleNotificationNavigation(homeEvent.notificationIntentData)
            is HomeEvent.NavigateToChildFragment -> handleNavigationToChildFragment(homeEvent.destinationId)
            is HomeEvent.NavMenuItemSelected -> handleNavMenuItemSelection(homeEvent.selectedDestinationId)
            is HomeEvent.SystemBackPressed -> postEffect(FinishActivity)
        }
    }



    // --------------------------------------------------------------------------
    // Event Handling Methods
    // --------------------------------------------------------------------------

    /**
     * Retrieves the last cached destination from persistent storage to ensure
     * a seamless user experience across app restarts. Defaults to Alarm screen.
     */
    private fun restoreLastOpenedHomeDestination() {
        val savedDestinationId = sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs
        val destinationId = if (savedDestinationId <= -1) R.id.alarmFragment else savedDestinationId
        handleNavigationToChildFragment(destinationId)
    }

    fun handleNotificationNavigation(notificationIntentData: NotificationIntentData) {
        postEffect(HandleNotificationNavigation(notificationIntentData))
    }

    fun handleNavigationToChildFragment(destinationId: Int) {
        postEffect(NavigateToChildFragment(destinationId))
    }

    /**
     * Updates the persistent navigation cache and triggers the selection animation
     * only if the user moves to a new destination.
     */
    private fun handleNavMenuItemSelection(selectedDestinationId: Int) {
        if (selectedDestinationId != sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs) {
            sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs = selectedDestinationId
            postEffect(RotateSelectedNavItemIcon(selectedDestinationId))
        }
    }


}