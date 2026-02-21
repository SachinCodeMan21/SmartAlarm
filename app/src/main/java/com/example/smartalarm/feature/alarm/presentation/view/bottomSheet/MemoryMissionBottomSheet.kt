package com.example.smartalarm.feature.alarm.presentation.view.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.extension.getParcelableCompat
import com.example.smartalarm.databinding.MemoryMissionBottomsheetBinding
import com.example.smartalarm.feature.alarm.domain.enums.Difficulty
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorUserEvent
import com.example.smartalarm.feature.alarm.presentation.view.bottomSheet.BaseMissionBottomSheet.Companion.PASSED_MISSION_ARGS_KEY
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.smartalarm.feature.alarm.presentation.view.fragment.editor.AlarmEditorHomeFragment.Companion.MISSION_ITEM_HOLDER_POSITION_KEY
import com.example.smartalarm.feature.alarm.presentation.viewmodel.editor.AlarmEditorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import kotlin.math.roundToInt

/**
 * A [BottomSheetDialogFragment] for configuring a memory-based [Mission].
 *
 * This bottom sheet lets users adjust mission difficulty, select the number of rounds,
 * and view a live preview of the memory grid based on the chosen difficulty.
 *
 * **Features**
 * - Initializes fields from the provided [Mission] when editing an existing one.
 * - Allows interactive adjustment of difficulty and round count.
 * - Updates the memory grid preview in real time.
 * - Returns the updated mission and its item position to the parent fragment using
 *   the standard result keys defined in [BaseMissionBottomSheet].
 *
 * **Usage**
 * ```kotlin
 * MemoryMissionBottomSheet
 *     .newInstance(mission, itemHolderPosition)
 *     .show(supportFragmentManager, MemoryMissionBottomSheet.TAG)
 * ```
 *
 * @see Mission
 * @see Difficulty
 * @see BaseMissionBottomSheet
 */

@AndroidEntryPoint
class MemoryMissionBottomSheet : BaseMissionBottomSheet() {


    companion object {

        private const val TAG = "MemoryMissionBottomSheet"
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"
        private const val MISSION_ARGS_NULL_ERROR = "$TAG $PASSED_MISSION_ARGS_NULL"

        /**
         * Creates a new instance of [MemoryMissionBottomSheet] configured with the provided mission
         * and item position.
         *
         * This factory method initializes the bottom sheet to edit the specified [Mission] and
         * keeps track of the item position that initiated the picker. The position is returned
         * along with the result when the user confirms their selection.
         *
         * @param mission The [Mission] to be edited.
         * @param itemHolderPosition The position or index of the item that initiated the bottom sheet.
         * @return A configured [MemoryMissionBottomSheet] instance ready to be displayed.
         */
        fun newInstance(mission: Mission, itemHolderPosition: Int): MemoryMissionBottomSheet {
            val fragment = MemoryMissionBottomSheet()
            val args = Bundle().apply {
                putParcelable(PASSED_MISSION_ARGS_KEY, mission)
                putInt(MISSION_ITEM_HOLDER_POSITION_KEY, itemHolderPosition)
            }
            fragment.arguments = args
            return fragment
        }

    }

    private var _binding: MemoryMissionBottomsheetBinding? = null
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)
    private val viewModel: AlarmEditorViewModel by activityViewModels()
    private lateinit var mission: Mission


    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes the mission from fragment arguments.
     * Throws if mission is missing.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mission = requireArguments().getParcelableCompat(PASSED_MISSION_ARGS_KEY)
            ?: throw IllegalArgumentException(MISSION_ARGS_NULL_ERROR)
    }


    /**
     * Inflates the fragment's layout and returns the root view for the bottom sheet.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = MemoryMissionBottomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }


    /**
     * Called immediately after the fragment's view has been created.
     *
     * This method initializes the UI components and sets up event listeners
     * for user interactions. It ensures that all view-related logic is
     * configured after the view hierarchy is ready.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyBottomSystemInset(binding.root)
        setupUI()
        setupListeners()
    }


    /**
     * Restores the state of the fragment's UI components after the view hierarchy
     * has been created. Specifically, it restores the value of the memory rounds picker
     * to the value saved during the previous instance of the fragment (e.g., before a
     * configuration change like rotation).
     *
     * @param savedInstanceState The [Bundle] containing saved state values, if any.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            val restoredRounds = it.getInt(ROUNDS_VALUE_KEY, mission.rounds)
            binding.memoryRoundsPicker.value = restoredRounds
        }
    }


    /**
     * Called to save the current state of the fragment before it may be destroyed
     * (e.g., during a configuration change like device rotation).
     * This method saves the current value of the memory rounds picker so it can be
     * restored later in [onViewStateRestored].
     *
     * @param outState The [Bundle] in which to place saved state values.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ROUNDS_VALUE_KEY, binding.memoryRoundsPicker.value)
    }


    /**
     * Clears the view binding reference to prevent memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    // ---------------------------------------------------------------------
    //  Setup UI & Listener Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes and updates the UI using data from the current [mission] instance,
     * which is populated from the fragment's [PASSED_MISSION_ARGS_KEY].
     *
     * Updates the title, difficulty indicators, round picker, and memory grid preview
     * to reflect the mission configuration.
     */
    private fun setupUI() = with(binding) {
        levelTv.text = getString(mission.difficulty.labelResId)
        levelSlider.value = mission.difficulty.sliderValue
        memoryRoundsPicker.setFormatter{ viewModel.getLocalizedNumber(it,false) }
        memoryRoundsPicker.value = mission.rounds
        updateMemoryGrid(mission.difficulty)
    }


    /**
     * Sets up listeners for all interactive elements in the mission bottom sheet.
     *
     * Handles:
     * - **Difficulty slider** — updates the difficulty label and memory grid preview.
     * - **Preview button** — generates a preview mission, sends the event to the ViewModel,
     *   closes the picker (if open), and dismisses the sheet.
     * - **Complete button** — saves the updated mission, notifies the ViewModel, closes the picker
     *   (if open), and dismisses the sheet.
     * - **Close button** — dismisses the sheet and closes the picker (if open).
     * - **Previous button** — dismisses the sheet without saving.
     */

    private fun setupListeners() = with(binding) {

        levelSlider.addOnChangeListener { _, value, _ ->
            val difficulty = Difficulty.fromSliderValue(value)
            levelTv.text = getString(difficulty.labelResId)
            updateMemoryGrid(difficulty)
        }

        previewBtn.setOnClickListener {
            val previewMission = mission.copy(difficulty = Difficulty.fromSliderValue(levelSlider.value), rounds = memoryRoundsPicker.value)
            viewModel.handleUserEvent(AlarmEditorUserEvent.MissionEvent.Preview(previewMission))
            MissionPickerBottomSheet.dismissIfVisible(parentFragmentManager)
            dismiss()
        }

        completeBtn.setOnClickListener {

            val newDifficulty = Difficulty.fromSliderValue(levelSlider.value)
            val updatedMission = mission.copy(difficulty = newDifficulty, rounds = memoryRoundsPicker.value)
            val missionHolderPosition = arguments?.getInt(MISSION_ITEM_HOLDER_POSITION_KEY) ?: 0

            viewModel.handleUserEvent(AlarmEditorUserEvent.MissionEvent.Updated(missionHolderPosition, updatedMission))
            MissionPickerBottomSheet.dismissIfVisible(parentFragmentManager)
            dismiss()
        }

        closeBottomSheet.setOnClickListener {
            MissionPickerBottomSheet.dismissIfVisible(parentFragmentManager)
            dismiss()
        }

        previousBottomSheetBtn.setOnClickListener {
            dismiss()
        }

    }


    // ---------------------------------------------------------------------
    //  Helper Method
    // ---------------------------------------------------------------------

    /**
     * Updates the memory challenge grid preview based on the selected [difficulty].
     *
     * The grid size corresponds to the difficulty:
     * - EASY: 3x3
     * - NORMAL: 4x4
     * - HARD: 5x5
     * - EXPERT: 6x6
     *
     * Approximately one-third of squares are highlighted to simulate a memory pattern.
     *
     * @param difficulty The difficulty level determining grid size and highlights.
     */
    private fun updateMemoryGrid(difficulty: Difficulty) = with(binding.memoryGrid) {

        removeAllViews()

        val size = when (difficulty) {
            Difficulty.EASY -> 3
            Difficulty.NORMAL -> 4
            Difficulty.HARD -> 5
            Difficulty.EXPERT -> 6
        }

        rowCount = size
        columnCount = size

        val total = size * size
        val yellowSquares = (total / 3f).roundToInt()
        val yellowIndices = (0 until total).shuffled().take(yellowSquares).toSet()

        repeat(total) { i ->
            val square = View(context).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 50
                    height = 50
                    setMargins(8, 8, 8, 8)
                }
                setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        if (i in yellowIndices) android.R.color.holo_orange_light else android.R.color.darker_gray
                    )
                )
            }
            addView(square)
        }
    }


}
