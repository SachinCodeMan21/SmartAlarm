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
import com.example.smartalarm.core.utility.exception.asUiText
import com.example.smartalarm.core.utility.extension.showSnackBar
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchTimeZoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setUpInsets()
        setupUI()
        observeUIState()
        observeUIEffects()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



    // ---------------------------------------------------------------------
    //  # Setup UI
    // ---------------------------------------------------------------------

    private fun setUpInsets(){
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupUI() = with(binding){

        searchToolbar.setNavigationOnClickListener {
            viewModel.handleEvent(PlaceSearchEvent.NavigateBack)
        }

        placeSearchAdapter = PlaceSearchAdapter {
            viewModel.handleEvent(PlaceSearchEvent.PlaceSelected(it))
        }

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
    private fun observeUIState() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->

                    with(binding) {

                        // 1. Hide everything by default
                        progressBar.isVisible = false
                        searchedPlacesRv.isVisible = false
                        emptyStateTv.isVisible = false

                        when (state) {

                            is PlaceSearchUiState.Initial -> {
                                emptyStateTv.isVisible = true
                                emptyStateTv.text = "Start typing to search for a place"
                            }

                            is PlaceSearchUiState.Loading -> {
                                progressBar.isVisible = true
                            }

                            is PlaceSearchUiState.Success -> {
                                if (state.places.isEmpty()) {
                                    // This is the "No Result Found" state
                                    emptyStateTv.isVisible = true
                                    emptyStateTv.text = "No such place found"
                                } else {
                                    searchedPlacesRv.isVisible = true
                                    placeSearchAdapter.submitList(state.places)
                                }
                            }

                            is PlaceSearchUiState.Error -> {
                                emptyStateTv.isVisible = true
                                emptyStateTv.text = "Something went wrong"
                            }

                        }
                    }
                }
            }
        }
    }


    // ---------------------------------------------------------------------
    //  # Setup Observe UI Effect
    // ---------------------------------------------------------------------
    private fun observeUIEffects() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEffect.collect { effect ->
                    when (effect) {
                        is PlaceSearchEffect.Finish -> finish()
                        is PlaceSearchEffect.NavigateToHome -> {
                            setResult(RESULT_OK)
                            finish()
                        }
                        is PlaceSearchEffect.ShowError -> {
                            val message = effect.error.asUiText().asString(this@SearchTimeZoneActivity)
                            binding.root.showSnackBar(message, Snackbar.LENGTH_SHORT)
                        }
                    }
                }
            }
        }
    }

}


