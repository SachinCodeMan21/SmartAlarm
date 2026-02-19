package com.example.smartalarm.feature.clock.presentation.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarm.databinding.ActivitySearchTimeZoneBinding
import com.example.smartalarm.feature.clock.presentation.adapter.PlaceSearchAdapter
import com.example.smartalarm.feature.clock.presentation.effect.PlaceSearchEffect
import com.example.smartalarm.feature.clock.presentation.event.PlaceSearchEvent
import com.example.smartalarm.feature.clock.presentation.uiState.PlaceSearchUiState
import com.example.smartalarm.feature.clock.presentation.viewmodel.PlaceSearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class SearchTimeZoneActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SearchTimeZoneActivity"
        private const val BINDING_NULL_ERROR = "$TAG binding is null"
    }

    private var _binding: ActivitySearchTimeZoneBinding? = null
    private val binding get() = _binding?: error(BINDING_NULL_ERROR)
    private val viewModel: PlaceSearchViewModel by viewModels()
    private lateinit var placeSearchAdapter: PlaceSearchAdapter

    // ---------------------------------------------------------------------
    //  #  Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Called when the activity is starting. This is where you should initialize your UI components,
     * observers, and perform any setup required for the screen.
     *
     * Responsibilities:
     * - Inflates the view binding.
     * - Sets the content view.
     * - Applies edge-to-edge system window insets.
     * - Initializes UI components via [setupUI].
     * - Begins observing UI state via [observeState].
     * - Begins observing one-time UI effects via [observeEffects].
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down
     * then this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchTimeZoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        observeState()
        observeEffects()
    }

    /**
     * Called when the activity is about to be destroyed.
     *
     * Responsibilities:
     * - Cleans up the view binding reference to prevent memory leaks.
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



    // ---------------------------------------------------------------------
    //  # Setup UI
    // ---------------------------------------------------------------------

    /**
     * Initializes the UI components and sets up user interaction listeners.
     *
     * Responsibilities:
     * - Configures the navigation click listener for the toolbar to close the activity.
     * - Initializes the [PlaceSearchAdapter] and sets it on the RecyclerView.
     * - Sets up a [LinearLayoutManager] for the RecyclerView.
     * - Adds a TextWatcher to the search input field to listen for query changes
     *   and dispatch them to the [PlaceSearchViewModel].
     */
    private fun setupUI() = with(binding){

        searchToolbar.setNavigationOnClickListener { viewModel.handleEvent(PlaceSearchEvent.NavigateBack)}

        placeSearchAdapter = PlaceSearchAdapter { viewModel.handleEvent(PlaceSearchEvent.PlaceSelected(it)) }

        searchedPlacesRv.apply {
            layoutManager = LinearLayoutManager(this@SearchTimeZoneActivity)
            setHasFixedSize(true)
            adapter = placeSearchAdapter
        }

        searchEt.addTextChangedListener { editable ->
            viewModel.handleEvent(PlaceSearchEvent.QueryChanged(editable?.toString().orEmpty().trim()))
        }

    }


    // ---------------------------------------------------------------------
    //  # Setup Observe UI State
    // ---------------------------------------------------------------------
    @SuppressLint("SetTextI18n")
    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    with(binding) {

                        val query = searchEt.text?.toString()?.trim().orEmpty()

                        // Reset visibility
                        progressBar.isVisible = false
                        searchedPlacesRv.isVisible = false
                        emptyStateTv.isVisible = false

                        when (state) {
                            is PlaceSearchUiState.Initial -> {
                                emptyStateTv.isVisible = true
                                emptyStateTv.text = "Start typing to search for a place"
                            }
                            is PlaceSearchUiState.Loading -> { progressBar.isVisible = true  }
                            is PlaceSearchUiState.Success -> {
                                if (query.isNotEmpty() && state.places.isEmpty()) {
                                    emptyStateTv.isVisible = true
                                } else {
                                    searchedPlacesRv.isVisible = true
                                    placeSearchAdapter.submitList(state.places)
                                }
                            }
                            is PlaceSearchUiState.Error -> {}
                        }
                    }
                }
            }
        }
    }


    // ---------------------------------------------------------------------
    //  # Setup Observe UI Effect
    // ---------------------------------------------------------------------
    /**
     * Collects one-time UI effects emitted by [PlaceSearchViewModel].
     *
     * These effects represent actions that should happen only once, such as:
     * - Navigating back to the previous screen
     * - Displaying a snackBar message
     *
     * The collection runs only while the activity is in the STARTED state,
     * ensuring proper lifecycle awareness.
     */
    private fun observeEffects() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEffect.collect { effect ->
                    when (effect) {

                        is PlaceSearchEffect.Finish -> finish()

                        is PlaceSearchEffect.NavigateToHome -> {
                            setResult(RESULT_OK)
                            finish()
                        }
                        is PlaceSearchEffect.ShowSnackBarMessage -> {
                            Snackbar.make(binding.root, effect.message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

}


