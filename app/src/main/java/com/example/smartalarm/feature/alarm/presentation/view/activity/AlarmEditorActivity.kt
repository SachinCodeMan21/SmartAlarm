package com.example.smartalarm.feature.alarm.presentation.view.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.Constants.PACKAGE
import com.example.smartalarm.databinding.ActivityAlarmEditorBinding
import com.example.smartalarm.feature.alarm.presentation.effect.editor.AlarmEditorEffect
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorUserEvent
import com.example.smartalarm.feature.alarm.presentation.view.fragment.editor.AlarmEditorHomeFragment
import com.example.smartalarm.feature.alarm.presentation.view.fragment.editor.AlarmEditorHomeFragmentArgs
import com.example.smartalarm.feature.alarm.presentation.view.fragment.editor.SnoozeAlarmFragment
import com.example.smartalarm.feature.alarm.presentation.viewmodel.editor.AlarmEditorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * Activity responsible for editing or creating alarms within the app.
 *
 * This activity handles the setup of the navigation graph, toolbar, UI effects, and back press behavior
 * for the Alarm Editor feature. It supports both creating new alarms and editing existing ones, based on
 * the alarm ID passed via the intent.
 *
 * Key functionalities:
 * 1. **Navigation**: Initializes the navigation controller and sets up the appropriate navigation graph
 *    with arguments passed to the fragments.
 * 2. **Toolbar Setup**: Configures the toolbar for navigation, including handling the custom back navigation action.
 * 3. **UI Effects**: Observes UI effects from the `AlarmEditorViewModel`, such as finishing the activity when needed.
 * 4. **Back Press Handling**: Customizes the back navigation behavior, delegating it to the ViewModel or specific fragments.
 */
@AndroidEntryPoint
class AlarmEditorActivity : AppCompatActivity()
{

    companion object {

        // Tag for logging within AlarmEditorActivity
        private const val TAG = "AlarmEditorActivity"

        // Error message for null view binding reference
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"

        // Key for passing existing alarm ID when editing
        const val EXISTING_ALARM_ID_KEY = "$PACKAGE.EXISTING_ALARM_ID_KEY"
    }

    private var _binding: ActivityAlarmEditorBinding? = null
    private val binding get() = _binding?: error(BINDING_NULL_ERROR)
    private val alarmEditorViewModel: AlarmEditorViewModel by viewModels()
    private lateinit var navController: NavController



    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Sets up the alarm editor UI, navigation graph, toolbar, UI effect observers,
     * and custom back press handling.
     *
     * @param savedInstanceState Saved state for configuration changes.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityAlarmEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeNavController()
        setNavGraphWithArgs()
        setUpAlarmEditorToolbar()
        setUpUIEffectObserver()
        setUpBackPressedCallback()
    }

    /**
     * - Cleans up references to the binding to avoid memory leaks.
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }




    // ---------------------------------------------------------------------
    // Initialization SetUp Methods
    // ---------------------------------------------------------------------

    /** Initializing the NavController for fragment navigation */
    private fun initializeNavController() {
        val navHost = supportFragmentManager.findFragmentById(R.id.alarm_editor_host_fragment_container) as NavHost
        navController = navHost.navController
    }


    /**
     * Configures the alarm editor navigation graph with arguments based on intent data.
     *
     * Determines whether we're editing an existing alarm or creating a new one,
     * then passes the appropriate title and ID to the starting [AlarmEditorHomeFragment].
     */
    private fun setNavGraphWithArgs() {

        // Retrieve the existing alarm ID from the intent
        val alarmId = intent.getIntExtra(EXISTING_ALARM_ID_KEY, -1)

        // Determine the title based on whether the alarm exists
        val editAlarmScreenTitle = if (alarmId == -1) getString(R.string.create_new_alarm) else getString(R.string.update_alarm)

        // Prepare the arguments to pass to the fragment
        val startArgs = AlarmEditorHomeFragmentArgs(
            title = editAlarmScreenTitle,
            existingAlarmId = if (alarmId == -1) 0 else alarmId
        )

        // Inflate the nav graph and pass the arguments to the navigation controller
        navController.setGraph(
            navController.navInflater.inflate(R.navigation.alarm_editor_nav_graph),
            startArgs.toBundle()  // Pass arguments as a bundle to the fragment
        )

    }


    /**
     * Sets up the editor activity toolbar as the support action bar
     * and configures it to work with the NavController for navigation UI handling.
     */
    private fun setUpAlarmEditorToolbar() {
        setSupportActionBar(binding.alarmEditorToolbar)
        setupActionBarWithNavController(navController, AppBarConfiguration(emptySet()))
    }


    /**
     * Observes UI effects from the [alarmEditorViewModel] and handles them accordingly.
     *
     * This function listens for the `FinishEditorActivity` effect. When this effect is emitted,
     * the activity is finished and closed. The observer is lifecycle-aware and only collects effects
     * when the activity is in the `STARTED` state or beyond.
     */
    private fun setUpUIEffectObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                alarmEditorViewModel.uiEffect.collectLatest { effect ->
                    if (effect is AlarmEditorEffect.FinishEditorActivity) { finish() }
                }
            }
        }
    }


    /**
     * Registers a activity back press callback to handle editor activity system back navigation events.
     * Delegates the back navigation event to the [onSupportNavigateUp].
     */
    private fun setUpBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onSupportNavigateUp()
            }
        })
    }


    // ---------------------------------------------------------------------
    // Navigation Host Toolbar BackPress Handler Methods
    // ---------------------------------------------------------------------

    /**
     * Overrides default toolbar up navigation to delegate to ViewModel or fragments depending on current fragment.
     *
     * This ensures custom back behavior (e.g., saving unsaved snooze changes , finishing activity)
     * when the user presses the back toolbar arrow.
     */
    override fun onSupportNavigateUp(): Boolean {

        val currentFragment = supportFragmentManager.findFragmentById(R.id.alarm_editor_host_fragment_container)
            ?.childFragmentManager?.fragments?.firstOrNull()

        when (currentFragment) {
            is AlarmEditorHomeFragment -> alarmEditorViewModel.handleUserEvent(AlarmEditorUserEvent.NavigationEvent.HandleCustomBackNavigation)
            is SnoozeAlarmFragment -> currentFragment.onToolbarBackPressed()
        }

        return navController.navigateUp() || super.onSupportNavigateUp()

    }

}