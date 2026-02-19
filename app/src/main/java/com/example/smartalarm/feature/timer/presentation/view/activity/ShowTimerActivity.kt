package com.example.smartalarm.feature.timer.presentation.view.activity

import android.app.KeyguardManager
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarm.databinding.ActivityShowTimerBinding
import com.example.smartalarm.feature.timer.framework.broadcast.constant.TimerBroadCastAction
import com.example.smartalarm.feature.timer.framework.broadcast.receiver.TimerReceiver
import com.example.smartalarm.feature.timer.framework.service.ShowTimerService
import com.example.smartalarm.feature.timer.presentation.adapter.ShowTimerAdapter
import com.example.smartalarm.feature.timer.presentation.effect.ShowTimerEffect
import com.example.smartalarm.feature.timer.presentation.event.ShowTimerEvent
import com.example.smartalarm.feature.timer.presentation.model.TimerUiState
import com.example.smartalarm.feature.timer.presentation.viewmodel.ShowTimerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue


/**
 * [ShowTimerActivity] is an activity that displays a list of timers and provides
 * functionality to manage these timers through various UI actions. It interacts with the
 * [ShowTimerViewModel] to handle timer-related operations and reflects state changes
 * in the UI. This activity includes a RecyclerView to show the list of timers, a
 * toolbar for navigation, and a button to add new timers.
 *
 * Responsibilities:
 * - Initializes the activity's UI, including setting up the toolbar, RecyclerView,
 *   and click listeners.
 * - Observes the ViewModel's UI state to update the UI accordingly.
 * - Manages foreground notifications related to the timer service.
 * - Handles lifecycle events such as starting and stopping timer services.
 *
 * Lifecycle:
 * - [onCreate]: Sets up the layout, UI elements, observers, and event handlers.
 * - [onStart]: Stops the foreground notification and restores timer state from storage.
 * - [onStop]: Starts the foreground notification and stops timer updates.
 * - [onDestroy]: Clears the binding to avoid memory leaks.
 *
 * UI Operations:
 * - Sets up the toolbar with navigation to the "Add Timer" screen.
 * - Configures a RecyclerView to display a list of timers using a [ShowTimerAdapter].
 * - Manages click events for adding a new timer, navigating to the "Add Timer" screen.
 *
 * Timer Service Management:
 * - Sends broadcasts to start and stop the timer service's foreground notification.
 * - Updates the UI based on timer state (loading, success, empty state).
 *
 * Observers:
 * - Observes the UI state via the ViewModel to handle changes in the timer list.
 * - Observes one-time effects such as navigation and showing toast messages.
 *
 * Dependencies:
 * - [ShowTimerViewModel]: ViewModel that manages the state and logic for timers.
 * - [ShowTimerAdapter]: Adapter that manages the timer list in the RecyclerView.
 * - [TimerReceiver]: BroadcastReceiver responsible for handling timer service actions.
 * - [TimerBroadCastAction]: Defines the actions for starting and stopping the timer service.
 *
 * @see ShowTimerViewModel
 * @see ShowTimerAdapter
 * @see TimerReceiver
 * @see TimerBroadCastAction
 */

@AndroidEntryPoint
class ShowTimerActivity : AppCompatActivity() {

    private var _binding: ActivityShowTimerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShowTimerViewModel by viewModels()
    private lateinit var showTimerAdapter: ShowTimerAdapter

    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityShowTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle system UI insets for padding (like status bar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.d("TAG", "ShowTimerActivity onCreate Executed")

        turnScreenOnAndKeyguardOff()

        setUpToolbar()
        setupTimerRecyclerView()
        setUpAddTimerBtnClick()
        setUpUIStateObserver()
        setUpUIEffectObserver()
    }

    override fun onStop() {
        super.onStop()
        // Stop any ongoing timer updates when the activity stops
        viewModel.handleEvent(ShowTimerEvent.StopTimerUiUpdates)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG", "ShowTimerActivity onDestroy Executed")
        _binding = null
    }

    // ---------------------------------------------------------------------
    // UI Setup Methods
    // ---------------------------------------------------------------------

    private fun setUpToolbar() {
        binding.showTimerToolbar.setNavigationOnClickListener {
            viewModel.handleEvent(ShowTimerEvent.HandleToolbarBackPressed)
        }
    }

    private fun setupTimerRecyclerView() {
        val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val lm = if (isPortrait) LinearLayoutManager(this) else GridLayoutManager(this, 2)
        showTimerAdapter = ShowTimerAdapter(viewModel::handleEvent)
        binding.showTimerRv.apply {
            layoutManager = lm
            setHasFixedSize(true)
            adapter = showTimerAdapter
        }
    }

    private fun setUpAddTimerBtnClick() {
        binding.addNewTimerBtn.setOnClickListener {
            viewModel.handleEvent(ShowTimerEvent.AddNewTimer)
        }
    }

    // ---------------------------------------------------------------------
    // UI State & Effect Observer Handler Methods
    // ---------------------------------------------------------------------

    private fun setUpUIStateObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is TimerUiState.Loading -> {
                            binding.showTimerRv.visibility = View.GONE
                            binding.showTimerProgressBar.visibility = View.VISIBLE
                            //binding.emptyLayout.visibility = View.GONE
                        }
                        is TimerUiState.Empty -> {
                            binding.showTimerRv.visibility = View.GONE
                            binding.showTimerProgressBar.visibility = View.GONE
                            finish()
                            //binding.emptyLayout.visibility = View.VISIBLE
                        }
                        is TimerUiState.Success -> {
                            binding.showTimerRv.visibility = View.VISIBLE
                            binding.showTimerProgressBar.visibility = View.GONE
                            //binding.emptyLayout.visibility = View.GONE

                            showTimerAdapter.submitList(state.timers)
                        }
                    }
                }
            }
        }
    }

    private fun setUpUIEffectObserver() {
        lifecycleScope.launch {
            // Collect UI Effects like Toasts and Notifications
            viewModel.uiEffect.collectLatest { effect ->
                when (effect) {
                    is ShowTimerEffect.FinishActivity -> finish()
                    is ShowTimerEffect.ShowToast -> showToast(effect.message)
                    ShowTimerEffect.StartTimerForegroundNotification -> startTimerForegroundNotification()
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    // Helper Methods for Actions
    // ---------------------------------------------------------------------

    private fun startTimerForegroundNotification() {
        val startTimerServiceIntent = Intent(this, ShowTimerService::class.java).apply {
            action = TimerBroadCastAction.ACTION_START
        }
        ContextCompat.startForegroundService(this, startTimerServiceIntent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun turnScreenOnAndKeyguardOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        // Wake up the device if the screen is off
        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this, null)
    }

}