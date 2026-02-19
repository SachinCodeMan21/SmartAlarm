package com.example.smartalarm.feature.clock.presentation.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.extension.enableSwipeToDelete
import com.example.smartalarm.core.utility.extension.showToast
import com.example.smartalarm.core.utility.extension.showUndoTimeZoneSnackBar
import com.example.smartalarm.databinding.FragmentClockBinding
import com.example.smartalarm.feature.clock.domain.model.ClockModel
import com.example.smartalarm.feature.clock.domain.model.PlaceModel
import com.example.smartalarm.feature.clock.presentation.adapter.TimeZoneAdapter
import com.example.smartalarm.feature.clock.presentation.effect.ClockEffect
import com.example.smartalarm.feature.clock.presentation.event.ClockEvent
import com.example.smartalarm.feature.clock.presentation.view.activity.SearchTimeZoneActivity
import com.example.smartalarm.feature.clock.presentation.viewmodel.ClockViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class ClockFragment : Fragment() {

    companion object {
        private const val TAG = "ClockFragment"
        private const val BINDING_NULL_ERROR = "$TAG binding is null"
    }

    private var _binding: FragmentClockBinding? = null
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)
    private val viewModel: ClockViewModel by viewModels()
    private lateinit var clockAdapter: TimeZoneAdapter
    private lateinit var searchTimeZoneLauncher: ActivityResultLauncher<Intent>


    // ---------------------------------------------------------------------
    //  # Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Called when the fragment's view is created.
     *
     * Responsibilities:
     * 1. Inflate the fragment's layout using ViewBinding.
     * 2. Initialize the binding property with the inflated layout.
     * 3. Return the root view for display.
     *
     * @param inflater LayoutInflater to inflate the view.
     * @param container Parent view group that the fragment's UI will be attached to.
     * @param savedInstanceState Bundle containing saved state if available.
     * @return The root View of the fragment's layout.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentClockBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called after the view is created. Sets up UI and loads time zones.
     *
     * Responsibilities:
     * 1. Setting up the RecyclerView.
     * 2. Observing ViewModel state and effects.
     * 3. Handling add time zone FAB click.
     * 4. Triggering the loading of time zones.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPlaceSearchActivityLauncher()
        setupRecyclerView()
        setupAddTimeZoneClick()
        setUpUIStateObserver()
        setUpUIEffectObserver()
        viewModel.onEvent(ClockEvent.LoadSelectedTimeZones)
    }


    /**
     * Called when the fragment is no longer visible to the user.

     * Responsibilities:
     *
     * - Triggers stopping of the clock UI updates to conserve resources.
     */
    override fun onStop() {
        super.onStop()
        viewModel.onEvent(ClockEvent.StopClockUiUpdates)
    }

    /**
     * Called when the fragment's view is being destroyed.
     *
     * Responsibilities:
     * - Clears the binding reference to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    // ---------------------------------------------------------------------
    //  # UI Setup Methods
    // ---------------------------------------------------------------------

    /**
     * Registers an activity result launcher for starting a timezone search activity.
     *
     * When the launched activity returns with RESULT_OK, triggers loading of selected time zones
     * via the ViewModel event.
     */
    private fun setUpPlaceSearchActivityLauncher() {
        searchTimeZoneLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.onEvent(ClockEvent.LoadSelectedTimeZones)
            }
        }
    }


    /**
     * Sets up the time zone RecyclerView.
     */
    private fun setupRecyclerView() {

        clockAdapter = TimeZoneAdapter()
        binding.globalTimeZoneRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = clockAdapter

            enableSwipeToDelete(
                context = requireContext(),
                onSwipe = { position ->
                    val deletedItem = clockAdapter.currentList[position]
                    viewModel.onEvent(ClockEvent.DeleteTimeZone(deletedItem))
                },
                getItem = { clockAdapter.currentList[it] },
                deleteIcon = R.drawable.ic_delete,
                backgroundColor = R.color.swipe_background,
                cornerRadius = 16f
            )
        }

    }


    /**
     * Sets up click listener on the "Add New Time Zone" button.
     *
     * Launches the SearchTimeZoneActivity using the registered activity launcher
     * when the button is clicked.
     */
    private fun setupAddTimeZoneClick() {
        binding.addNewTimeZoneBtn.setOnClickListener {
            viewModel.onEvent(ClockEvent.AddNewTimeZone)
        }
    }


    /**
     * Updates UI with latest clock data from ViewModel.
     */
    private fun setUpUIStateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiModel.collectLatest { model ->
                    updateUI(model)
                }
            }
        }
    }

    /**
     * Handles one-time UI effects like toast messages.
     */
    private fun setUpUIEffectObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEffect.collect { effect ->
                    when (effect) {
                        is ClockEffect.NavigateToAddTimeZoneActivity -> {
                            val intent = Intent(requireContext(), SearchTimeZoneActivity::class.java)
                            searchTimeZoneLauncher.launch(intent)
                        }
                        is ClockEffect.DeleteTimeZone -> showUndoTimeZoneSnackBar(effect.deletedTimeZone)
                        is ClockEffect.ShowToast -> requireContext().showToast(effect.message)
                    }
                }
            }
        }
    }



    // ---------------------------------------------------------------------
    //  # UI State Update & Effects Handler Methods
    // ---------------------------------------------------------------------

    /**
     * Applies the UI model to the view.
     *
     * 1. Updates formatted time and date.
     * 2. Updates RecyclerView with saved places.
     * 3. Hides RecyclerView if the list is empty.
     */
    private fun updateUI(data: ClockModel) {
        binding.apply {
            clockTimeTv.text = data.formattedTime
            clockDayTv.text = data.formattedDate
            globalTimeZoneRv.isVisible = data.savedPlaces.isNotEmpty()
            clockAdapter.submitList(data.savedPlaces)
        }
    }

    /**
     * Displays a SnackBar to allow undoing a recently deleted time zone.
     *
     * If the user taps "Undo", a [ClockEvent.UndoDeletedTimeZone] is sent to the ViewModel.
     *
     * @param deletedTimeZone The [PlaceModel] that was just deleted.
     */
    private fun showUndoTimeZoneSnackBar(deletedTimeZone : PlaceModel){
        binding.root.showUndoTimeZoneSnackBar(
            message = "${deletedTimeZone.primaryName} Timezone Deleted",
            undoTextRes = R.string.undo,
            onUndo = { viewModel.onEvent(ClockEvent.UndoDeletedTimeZone(deletedTimeZone)) }
        )
    }

}