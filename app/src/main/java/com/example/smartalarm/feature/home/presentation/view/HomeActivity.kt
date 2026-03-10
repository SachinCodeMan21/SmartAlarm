package com.example.smartalarm.feature.home.presentation.view


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.smartalarm.R
import com.example.smartalarm.core.framework.notification.model.NotificationIntentData
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
 * Main entry point for the Smart Alarm experience.
 *
 * This activity hosts the app's main Navigation Component graph using a NavHostFragment.
 * It implements an adaptive navigation pattern that switches between Bottom Navigation,
 * Navigation Rail, or Navigation Drawer depending on device configuration.
 *
 * Navigation decisions are delegated to HomeViewModel to support:
 * - state restoration
 * - notification deep-link handling
 * - centralized navigation events
 */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    companion object {

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

    private lateinit var binding: ActivityHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var navController: NavController


    // ---------------------------------------------------------------------
    //  Lifecycle Methods
    // ---------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpInsets()
        setupUI()
        setUpHomeBackPressed()
        setUpHomeEffectObserver()
        navigateToInitialDestination()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navigateToInitialDestination()
    }

    // ---------------------------------------------------------------------
    //  Initialization And Setup Methods
    // ---------------------------------------------------------------------

    /**
     * Adjusts layout padding to account for system bars, ensuring content is not
     * obscured by the status bar or navigation gesture areas.
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

    private fun setupUI(){
        initNavController()
        setupHomeToolbar()
        setUpNavigation()
    }

    /**
     * Intercepts system back presses to implement "Island Navigation."
     * Top-level destinations act as entry points where a back press should exit the app
     * rather than navigating backward through the bottom-nav stack.
     */
    private fun setUpHomeBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                homeViewModel.handleEvent(SystemBackPressed)
            }
        })
    }

    private fun setUpHomeEffectObserver() {
        lifecycleScope.launch {
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

    /**
     * Determines navigation state based on Intent extras (Notifications) or
     * saved state (Restore). Extras are cleared after use to prevent
     * redundant navigation during configuration changes (rotations).
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
            homeViewModel.handleEvent(RestoreLastOpenedDestination)
        }

        // Clean up intent to prevent re-triggering on screen rotation
        intent.replaceExtras(Bundle())
        intent.action = ""
    }




    // ---------------------------------------------------------------------
    // UI Setup Methods
    // ---------------------------------------------------------------------

    private fun initNavController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.home_fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
    }

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

    private fun selectNavigationItem(destinationId: Int) {

        if (navController.currentDestination?.id == destinationId) return

        val navView = getCurrentNavigationView()
        when(navView){
            is NavigationBarView -> navView.selectedItemId = destinationId
            is NavigationView -> navView.setCheckedItem(destinationId)
        }
    }

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
     * Rotates the icon of the reselected menu item to provide haptic-like visual feedback.
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_toolbar_menu, menu)
        return true
    }

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
     * Returns the currently active navigation component provided by the binding.
     * Use this to apply logic across different device layouts (Phone vs Tablet).
     */
    private fun getCurrentNavigationView(): View? {
        return binding.bottomNav ?: binding.navigationRail ?: binding.navigationView
    }

}