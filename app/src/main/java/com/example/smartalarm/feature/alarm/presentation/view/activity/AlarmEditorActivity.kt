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

 * Hosts the Alarm Editor flow for creating or editing alarms.
 *
 * This activity acts as a navigation container that manages the alarm editing
 * experience using the Navigation Component. It initializes the navigation graph,
 * passes required arguments, and coordinates toolbar and back navigation behavior.
 *
 * Fragments hosted:
 * * [AlarmEditorHomeFragment] – Handles alarm creation, editing, and updates.
 * * [SnoozeAlarmFragment] – Provides a dedicated interface for configuring alarm snooze options.
 *
 * The activity also observes UI effects from [AlarmEditorViewModel] to handle
 * actions such as finishing the editor when the alarm operation completes.
 */
@AndroidEntryPoint
class AlarmEditorActivity : AppCompatActivity()
{

    companion object {

        // Key for passing existing alarm ID when editing
        const val EXISTING_ALARM_ID_KEY = "$PACKAGE.EXISTING_ALARM_ID_KEY"
    }

    private lateinit var binding: ActivityAlarmEditorBinding
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
        binding = ActivityAlarmEditorBinding.inflate(layoutInflater)
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