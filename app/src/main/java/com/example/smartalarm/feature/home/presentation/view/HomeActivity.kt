package com.example.smartalarm.feature.home.presentation.view


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.smartalarm.R
import com.example.smartalarm.core.notification.model.NotificationIntentData
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.Constants.PACKAGE
import com.example.smartalarm.databinding.ActivityHomeBinding
import com.example.smartalarm.feature.home.presentation.effect.HomeEffect
import com.example.smartalarm.feature.home.presentation.event.HomeEvent.*
import com.example.smartalarm.feature.home.presentation.viewmodel.HomeViewModel
import com.example.smartalarm.feature.setting.activity.HelpActivity
import com.example.smartalarm.feature.setting.activity.SettingActivity
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerKeys
import com.example.smartalarm.feature.timer.presentation.view.activity.ShowTimerActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * HomeActivity serves as the entry point for the home screen and manages navigation between various fragments.
 *
 * It leverages a `NavController` for handling fragment-based navigation and adapts navigation patterns
 * based on the screen size, providing a flexible experience across devices:
 * 1. **Bottom Navigation**: Used on phones for quick navigation between key fragments.
 * 2. **Navigation Rail**: Used on tablets or medium-sized devices to enhance navigation accessibility.
 * 3. **Navigation Drawer**: Provides a more expansive navigation experience on larger screens.
 *
 * This activity's role is to simplify UI management, streamline fragment navigation, and ensure smooth user interaction
 * by managing the navigation, toolbar, back presses, and UI effects.
 */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    companion object {

        // Tag used for logging within HomeActivity
        private const val TAG = "HomeActivity"

        // Error message for null view binding references (for debugging)
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"

        // Full rotation for bottom navigation item icon in degrees (360 degrees)
        private const val ICON_ROTATION_DEGREES = 360f

        // Duration for rotating bottom navigation item icon (in milliseconds)
        private const val ICON_ROTATION_DURATION_MS = 500L



        // Notification Extras
        const val EXTRA_NOTIFICATION_ACTION = "$PACKAGE.EXTRA_NOTIFICATION_ACTION"
        const val EXTRA_START_DESTINATION = "$PACKAGE.EXTRA_START_DESTINATION"
        const val EXTRA_DESTINATION_ID = "$PACKAGE.EXTRA_DESTINATION_ID"


        // Notification Action Types
        const val ACTION_TIMER_ACTIVE = "action_timer_running"
        const val ACTION_TIMER_COMPLETED = "action_timer_completed"
        const val ACTION_TIMER_MISSED = "action_timer_missed"
        const val ACTION_ACTIVE_STOPWATCH = "action_active_stopwatch"


    }

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var navController: NavController


    // ---------------------------------------------------------------------
    //  Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes the activity, including setting up the edge-to-edge display, inflating the layout,
     * and configuring UI elements for a seamless experience across different screen sizes.
     *
     * This method also sets up window insets for a more refined UI and manages the back press behavior.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpInsets()

        setupUI()
        setUpHomeBackPressed()
        setUpHomeEffectObserver()
        navigateToInitialDestination()
    }

    /**
     * Handles new intents received by the Activity.
     *
     * - Updates the intent for the Activity using [setIntent(intent)].
     * - Calls [navigateToInitialDestination] to navigate to the appropriate screen
     *   based on the new intent's data (e.g., passed from a notification click).
     *
     * This ensures that the Activity reacts to new intent data and navigates accordingly.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navigateToInitialDestination()
    }


    /**
     * Clears the view binding to avoid potential memory leaks when the activity is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }




    // ---------------------------------------------------------------------
    //  Initialization And Setup Methods
    // ---------------------------------------------------------------------

    /**
     * Configures the system UI to respect window insets, ensuring that the content does not overlap
     * with system bars (e.g., status bar, navigation bar).
     *
     * This method allows for proper padding adjustments based on the device’s UI layout and screen size.
     */
    private fun setUpInsets() {

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->

            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply top padding to the toolbar so it clears the status bar
            binding.homeToolbarLayout.root.setPadding(0, bars.top, 0, 0)

            // Apply padding to the navigation container based on its type
            val navView = getCurrentNavigationView()
            when (navView) {
                is BottomNavigationView -> navView.updatePadding(left = bars.bottom)
                is NavigationRailView -> navView.updatePadding(left = bars.left,  top = bars.top, bottom = bars.bottom)
                is NavigationView -> navView.updatePadding(left = bars.left, top = bars.top, bottom = bars.bottom)
            }
            insets
        }
    }


    /**
     * Sets up the core UI components for the activity, ensuring that the navigation flow is properly configured.
     *
     * The goal is to ensure that the navigation components adapt to different screen sizes, with appropriate
     * navigation views for mobile, tablet, and large devices, providing an optimized experience.
     */
    private fun setupUI(){
        initNavController()
        setupHomeToolbar()
        setUpNavigation()
    }


    /**
     * Handles system back presses by delegating the event to the ViewModel.
     *
     * - The back press event is passed to the ViewModel, which decides whether to finish the Activity
     *   (if the user is on a top-level fragment) or do nothing based on the current state.
     * - This follows the **island navigation** pattern, typical for utility apps, where top-level screens
     *   act as self-contained "islands" and back presses exit the app directly.
     *
     * - **Interception of Navigation Logic**: This replaces the default back navigation and "Up" button
     *   behavior, centralizing navigation logic in the ViewModel for consistency and maintainability.
     */
    private fun setUpHomeBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                homeViewModel.handleEvent(SystemBackPressed)
            }
        })
    }


    /**
     * Observes and handles UI effects emitted by the [homeViewModel].
     *
     * This method listens for UI effect events and performs the appropriate action based on the type of effect:
     * - **NavigateToChildFragment**: Navigates to a specific child fragment.
     * - **HandleNotificationNavigation**: Handles navigation triggered by a notification click.
     * - **RotateSelectedNavItemIcon**: Animates the rotation of the selected navigation item's icon.
     * - **FinishActivity**: Finishes the current activity, typically when a back press or exit action occurs.
     *
     * The collection of effects is lifecycle-aware, using [repeatOnLifecycle(Lifecycle.State.STARTED)],
     * to ensure that the UI only responds to effects when the `Activity` is in a valid state (i.e., STARTED or RESUMED).
     * This prevents unnecessary operations or memory leaks when the activity is paused or destroyed.
     */
    private fun setUpHomeEffectObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uiEffect.collect { effect ->
                    when (effect) {
                        is HomeEffect.NavigateToChildFragment -> selectNavigationItem(effect.destinationId)
                        is HomeEffect.HandleNotificationNavigation -> handleNotificationNavigation(effect.notificationIntentData)
                        is HomeEffect.RotateSelectedNavItemIcon -> rotateSelectedNavItemIcon(effect.bottomNavItemId)
                        is HomeEffect.FinishActivity -> finish()
                    }
                }
            }
        }
    }



    /**
     * Determines the initial destination to navigate to when the app is launched or resumed.
     *
     * This method is responsible for handling two key use cases:
     * 1. **Navigation from a Notification**: If the app is launched from a notification, it ensures
     *    that the correct destination is opened based on the data passed through the notification.
     *    This allows the app to provide a seamless experience, directly landing the user on the
     *    relevant screen without unnecessary intermediate steps.
     *
     * 2. **Regular App Launch**: If the app is launched normally (not from a notification), the
     *    method restores the last opened destination. This ensures a consistent experience by
     *    returning the user to the exact screen they were on before exiting the app, minimizing
     *    interruptions to their flow.
     *
     * This method also handles intent cleanup by resetting the intent's extras. This step is important
     * to prevent the same intent from triggering navigation actions repeatedly when the screen is rotated,
     * which can lead to unintended behaviors.
     *
     * In both cases, the user experience is optimized by ensuring the app opens directly to the right screen,
     * whether they're resuming from a previous session or launching from a notification.
     */
    private fun navigateToInitialDestination() {

        val notificationAction = intent.getStringExtra(EXTRA_NOTIFICATION_ACTION)
        val destinationId = intent.getIntExtra(EXTRA_START_DESTINATION, -1)
        val dataId = intent.getIntExtra(EXTRA_DESTINATION_ID, -1)

        // Decision Logic
        if (destinationId != -1 && notificationAction != null) {
            val notificationIntentData = NotificationIntentData(destinationId, notificationAction, dataId)
            homeViewModel.handleEvent(NavigateFromNotification(notificationIntentData))
        }
        else {
            // Regular app launch: Restore last fragment
            homeViewModel.handleEvent(RestoreLastOpenedDestination(0))
        }

        // Clean up intent to prevent re-triggering on screen rotation
        intent.replaceExtras(Bundle())
        intent.action = ""
    }




    // ---------------------------------------------------------------------
    // UI Setup Methods
    // ---------------------------------------------------------------------


    /**
     * Obtains the NavController early in the activity lifecycle so that all navigation-related
     * components (toolbar, bottom navigation, drawer) can share a single navigation source of truth.
     *
     * Centralizing navigation through one controller prevents inconsistent back stack behavior
     * and keeps navigation state predictable across different device layouts.
     */

    private fun initNavController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.home_fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
    }


    /**
     * Sets up the navigation components (e.g., BottomNavigationView, NavigationRailView, NavigationView) with
     * the NavController to handle navigation actions based on user interaction.
     *
     * This method ensures consistent navigation across different screen layouts by binding navigation views
     * to the NavController and setting up item selection listeners.
     */
    private fun setUpNavigation() {

        val navView = getCurrentNavigationView()

        when (navView) {
            is NavigationBarView -> { // Handles BottomNav and Rail
                NavigationUI.setupWithNavController(navView, navController)
                navView.setOnItemSelectedListener { item ->
                    val handled = NavigationUI.onNavDestinationSelected(item, navController)
                    if (handled) homeViewModel.handleEvent(NavMenuItemSelected(item.itemId))
                    handled
                }
            }
            is NavigationView -> { // Handles Permanent Drawer
                NavigationUI.setupWithNavController(navView, navController)
                navView.setNavigationItemSelectedListener { item ->
                    val handled = NavigationUI.onNavDestinationSelected(item, navController)
                    if (handled) homeViewModel.handleEvent(NavMenuItemSelected(item.itemId))
                    handled
                }
            }
        }
    }



    /**
     * Sets up the MaterialToolbar for the activity, enabling top-level navigation through the NavController.
     */
    private fun setupHomeToolbar() {

        setSupportActionBar(binding.homeToolbarLayout.homeToolbar)

        val topLevelDestinations = setOf(
            R.id.alarmFragment,
            R.id.clockFragment,
            R.id.timerFragment,
            R.id.stopwatchFragment
        )

        val appBarConfiguration =  AppBarConfiguration(topLevelDestinations)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }




    // ---------------------------------------------------------------------
    // UI Effect Handlers
    // ---------------------------------------------------------------------

    /**
     * Synchronizes the visible navigation component with the NavController state.
     *
     * This exists to ensure the UI selection always reflects the current navigation destination,
     * especially when navigation is triggered programmatically (e.g., from a notification or
     * ViewModel effect).
     *
     * The early return prevents unnecessary reselection, which avoids redundant navigation
     * events and preserves back stack stability.
     *
     * @param destinationId The destination that should be visually marked as active.
     */
    private fun selectNavigationItem(destinationId: Int) {

        if (navController.currentDestination?.id == destinationId) return

        val navView = getCurrentNavigationView()
        when(navView){
            is NavigationBarView -> navView.selectedItemId = destinationId
            is NavigationView -> navView.setCheckedItem(destinationId)
        }
    }


    /**
     * Handles navigation triggered by a notification click, directing the user to the appropriate screen.
     *
     * This method uses the `notificationAction` from the notification data to determine which screen to navigate to:
     * - **ACTION_TIMER_ACTIVE, ACTION_TIMER_COMPLETED, ACTION_TIMER_MISSED**: Navigates to the timer-related activity and passes relevant data.
     * - For other actions, it simply updates the destination based on the notification's destination ID.
     *
     * @param notificationIntentData The data passed from the notification containing destination information and additional data.
     */
    private fun handleNotificationNavigation(notificationIntentData: NotificationIntentData){
        when (notificationIntentData.notificationAction) {
            ACTION_TIMER_ACTIVE,
            ACTION_TIMER_COMPLETED,
            ACTION_TIMER_MISSED -> {
                homeViewModel.handleEvent(NavigateToChildFragment(notificationIntentData.destinationId))
                val showTimerIntent = Intent(this, ShowTimerActivity::class.java).apply {
                    putExtra(TimerKeys.TIMER_ID, notificationIntentData.extraId)
                    putExtra(EXTRA_NOTIFICATION_ACTION,action)
                }
                startActivity(showTimerIntent)
            }
            else -> {
                homeViewModel.handleEvent(NavigateToChildFragment(notificationIntentData.destinationId))
            }
        }
    }


    /**
     * Provides visual feedback when a navigation item is reselected.
     *
     * This animation reinforces user interaction and makes repeated selections feel intentional,
     * improving perceived responsiveness of the UI.
     *
     * Rotation is limited to NavigationBarView-based components because drawer items
     * do not support animated icon transformations.
     *
     * @param itemId The navigation item that triggered the reselection effect.
     */
    private fun rotateSelectedNavItemIcon(itemId: Int) {

        val navView = getCurrentNavigationView()

        if (navView is NavigationBarView) {
            navView.findViewById<View>(itemId)
                ?.findViewById<ImageView>(com.google.android.material.R.id.navigation_bar_item_icon_view)
                ?.animate()?.rotationBy(ICON_ROTATION_DEGREES)?.setDuration(ICON_ROTATION_DURATION_MS)
                ?.start()
        }

    }




    // ---------------------------------------------------------------------
    // Handle Toolbar Actions Handling
    // ---------------------------------------------------------------------

    /**
     * Inflates the toolbar menu to expose secondary actions that are not part of
     * primary navigation.
     *
     * Keeping these actions in the options menu prevents cluttering the main
     * navigation structure while still making global features (e.g., settings, help)
     * easily accessible.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_toolbar_menu, menu)
        return true
    }


    /**
     * Handles global toolbar actions that are outside the fragment navigation graph.
     *
     * These actions launch independent flows (such as settings or help)
     * that should not affect the current fragment back stack.
     *
     * Returning true signals that the event was intentionally consumed.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Open the settings activity or fragment
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_help -> {
                // Open the help activity or fragment
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    // ---------------------------------------------------------------------
    // Helper Method
    // ---------------------------------------------------------------------

    /**
     * Resolves the active navigation component based on the current layout configuration.
     *
     * This abstraction allows the activity to support multiple navigation patterns
     * (bottom navigation, rail, or drawer) without duplicating logic elsewhere.
     *
     * Centralizing this lookup keeps navigation handling device-agnostic and
     * simplifies future layout adaptations.
     */
    private fun getCurrentNavigationView(): FrameLayout? {
        return findViewById<BottomNavigationView>(R.id.bottom_nav)
            ?: findViewById<NavigationRailView>(R.id.navigation_rail)
            ?: findViewById<NavigationView>(R.id.navigation_view)
    }

}