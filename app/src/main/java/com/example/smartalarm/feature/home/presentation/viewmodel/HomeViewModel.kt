package com.example.smartalarm.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartalarm.R
import com.example.smartalarm.core.notification.model.NotificationIntentData
import com.example.smartalarm.core.utility.sharedPreference.contract.SharedPrefsHelper
import com.example.smartalarm.feature.home.presentation.effect.HomeEffect
import com.example.smartalarm.feature.home.presentation.effect.HomeEffect.*
import com.example.smartalarm.feature.home.presentation.event.HomeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing state and actions related to the Home Activity.
 *
 * This ViewModel serves as the intermediary between the UI and the app's business logic, managing the app’s navigation
 * and UI interactions while maintaining a clean separation of concerns. It ensures that the UI reacts to changes and events
 * in a consistent manner, and that transient UI effects (like navigation or animations) do not interfere with the app's state.
 *
 * Key Responsibilities:
 * - **Handle Events**: The ViewModel centralizes the processing of navigation requests, menu selections, back presses,
 *   and notification interactions, ensuring a cohesive and responsive user experience.
 * - **Emit UI Effects**: It triggers one-time UI effects (e.g., navigation, icon rotations) that need to be handled outside
 *   of the regular UI state, allowing for smoother interactions and better UI responsiveness.
 * - **State Persistence**: The ViewModel manages the persistence of the last opened destination, which ensures that the app
 *   returns users to the appropriate screen when they reopen the app or navigate between screens.
 *
 * This architecture ensures that UI actions like navigating between fragments and handling notification clicks are decoupled
 * from the app's state, making it easier to maintain, test, and extend. By using `MutableSharedFlow`, the ViewModel can trigger
 * transient UI actions that don’t alter the main app state, providing the UI layer with precise control over these one-time effects.
 *
 * Dependencies:
 * - `SharedPrefsHelper`: Responsible for storing and retrieving the last opened destination, helping to restore the app
 *   state when users return to the app.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sharedPrefsHelper: SharedPrefsHelper
) : ViewModel()
{

    /**
     * Holds the mutable shared flow of one-time UI effects that can be emitted by the ViewModel.
     *
     * The private mutable shared flow is used internally within the ViewModel to emit transient effects, such as
     * navigation or UI animations, without affecting the ViewModel's main state. By keeping this flow private,
     * the ViewModel ensures that only the ViewModel itself can trigger effects, while the UI can observe them.
     */
    private val _uiEffect = MutableSharedFlow<HomeEffect>(0)

    /**
     * Publicly exposes the shared flow of UI effects for observation by the UI layer.
     *
     * The UI layer (e.g., Activity or Fragment) collects from this flow to respond to one-time UI events like navigation,
     * icon animations, or activity finishing. The flow is exposed as a read-only shared flow to maintain separation between
     * the ViewModel’s internal logic and the UI’s handling of these effects.
     */
    val uiEffect = _uiEffect.asSharedFlow()



    /**
     * Emits a one-time UI effect to trigger actions like navigation, icon rotation, or finishing the activity.
     */
    private fun postEffect(homeEffect: HomeEffect){
        viewModelScope.launch { _uiEffect.emit(homeEffect) }
    }



    // --------------------------------------------------------------------------
    // Home Event Handler
    // --------------------------------------------------------------------------

    /**
     * Handles different events to manage navigation and UI state in the home activity.
     *
     * This method centralizes event handling for various UI actions, ensuring the app responds correctly
     * to navigation, back presses, and notification interactions, while keeping the logic easy to maintain.
     *
     * - **RestoreLastOpenedDestination**: Restores the last visited screen or defaults to a safe destination.
     * - **NavigateFromNotification**: Handles navigation triggered by a notification click.
     * - **NavigateToChildFragment**: Navigates to a specific fragment in the app.
     * - **NavMenuItemSelected**: Updates the selected navigation item and provides visual feedback.
     * - **SystemBackPressed**: Manages back press events to ensure proper activity termination.
     *
     * @param homeEvent The event triggering the corresponding navigation or UI change.
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
    // Event Handler Methods
    // --------------------------------------------------------------------------

    /**
     * Restores the last opened destination, or defaults to the alarm fragment if none is found.
     *
     * This method ensures that when the app is reopened, the user is taken back to the screen they were last using,
     * enhancing the user experience by maintaining continuity. If no previous destination is stored, it defaults to the alarm
     * fragment to provide a known and useful starting point.
     */
    private fun restoreLastOpenedHomeDestination() {
        val savedDestinationId = sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs
        val destinationId = if (savedDestinationId <= -1) R.id.alarmFragment else savedDestinationId
        handleNavigationToChildFragment(destinationId)
    }

    /**
     * Handles navigation triggered by a notification click.
     *
     * This method ensures that when the user clicks on a notification, they are taken directly to the relevant screen
     * in the app, improving the app's responsiveness to external triggers. By using the notification data, it directs the user
     * to the appropriate destination, ensuring a seamless and context-aware experience.
     *
     * @param notificationIntentData Data containing the destination ID, notification action,
     *                               and optional extra ID, ensuring the correct screen is opened.
     */
    fun handleNotificationNavigation(notificationIntentData: NotificationIntentData) {
        postEffect(HandleNotificationNavigation(notificationIntentData))
    }

    /**
     * Handles navigation to a specific child fragment.
     *
     * The purpose of this method is to centralize navigation logic. By emitting the `NavigateToChildFragment` effect, it ensures
     * that navigation is consistent and uniform, regardless of the fragment being navigated to. This simplifies maintaining
     * the navigation flow and makes it easier to track where the user is in the home activity's navigation graph.
     *
     * @param destinationId The ID of the destination fragment to navigate to.
     */
    fun handleNavigationToChildFragment(destinationId: Int) {
        postEffect(NavigateToChildFragment(destinationId))
    }

    /**
     * Updates the last opened destination and rotates the icon of the selected navigation item.
     *
     * This method ensures the last visited destination is saved, allowing the app to remember where the user left off. It also
     * provides immediate visual feedback by rotating the selected navigation item’s icon, improving the overall user experience
     * by making the navigation interaction feel more responsive and intuitive.
     */
    private fun handleNavMenuItemSelection(selectedDestinationId: Int) {
        if (selectedDestinationId != sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs) {
            sharedPrefsHelper.lastOpenedHomeDestinationIdPrefs = selectedDestinationId
            postEffect(RotateSelectedNavItemIcon(selectedDestinationId))
        }
    }


}