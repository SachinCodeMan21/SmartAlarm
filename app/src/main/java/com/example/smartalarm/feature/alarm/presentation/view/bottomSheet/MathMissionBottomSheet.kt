package com.example.smartalarm.feature.alarm.presentation.view.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.extension.getParcelableCompat
import com.example.smartalarm.databinding.MathMissionBottomsheetBinding
import com.example.smartalarm.feature.alarm.domain.enums.Difficulty
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorUserEvent
import com.example.smartalarm.feature.alarm.presentation.view.fragment.editor.AlarmEditorHomeFragment.Companion.MISSION_ITEM_HOLDER_POSITION_KEY
import com.example.smartalarm.feature.alarm.presentation.viewmodel.editor.AlarmEditorViewModel
import com.example.smartalarm.feature.alarm.utility.ArithmeticQuestionGenerator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.getValue

/**
 * A [BottomSheetDialogFragment] for configuring a math-based [Mission].
 *
 * This sheet allows users to adjust the mission difficulty, select the number of rounds,
 * and preview a generated math expression that reflects the selected difficulty level.
 *
 * **Features**
 * - Initializes UI fields from the provided [Mission].
 * - Updates difficulty, rounds, and example math expressions interactively.
 * - Returns the updated mission and its item index to the parent fragment using
 *   the standard result keys defined in [BaseMissionBottomSheet].
 * - Safely manages ViewBinding to avoid memory leaks.
 *
 * **Usage**
 * ```kotlin
 * MathMissionBottomSheet
 *     .newInstance(mission, itemHolderPosition)
 *     .show(parentFragmentManager, MathMissionBottomSheet.TAG)
 * ```
 *
 * @see Mission
 * @see Difficulty
 * @see BaseMissionBottomSheet
 */

@AndroidEntryPoint
class MathMissionBottomSheet : BaseMissionBottomSheet() {

    companion object {

        private const val TAG = "MathMissionBottomSheet"
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"
        private const val MISSION_ARGS_NULL_ERROR = "$TAG $PASSED_MISSION_ARGS_NULL"


        /**
         * Creates a new instance of [MathMissionBottomSheet] configured with the provided mission
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
        fun newInstance(mission: Mission, itemHolderPosition: Int): MathMissionBottomSheet {
            val fragment = MathMissionBottomSheet()
            val args = Bundle().apply {
                putParcelable(PASSED_MISSION_ARGS_KEY, mission)
                putInt(MISSION_ITEM_HOLDER_POSITION_KEY, itemHolderPosition)
            }
            fragment.arguments = args
            return fragment
        }

    }

    private var _binding: MathMissionBottomsheetBinding? = null
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)
    private val viewModel: AlarmEditorViewModel by activityViewModels()
    private lateinit var mission: Mission
    private lateinit var selectedDifficulty: Difficulty

    @Inject lateinit var arithmeticQuestionGenerator: ArithmeticQuestionGenerator

    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes the mission from fragment arguments.
     * Throws if mission is missing.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mission = requireArguments().getParcelableCompat(PASSED_MISSION_ARGS_KEY) ?: throw IllegalArgumentException(MISSION_ARGS_NULL_ERROR)
        selectedDifficulty = mission.difficulty
    }

    /**
     * Inflates the layout and sets up UI and listeners.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MathMissionBottomsheetBinding.inflate(inflater, container, false)
        setupUI()
        setupListeners()
        return binding.root
    }

    /**
     * Restores the state of the fragment's UI components after the view hierarchy
     * has been created. Specifically, it restores the value of the math rounds picker
     * to the value saved during the previous instance of the fragment (e.g., before a
     * configuration change like rotation).
     *
     * @param savedInstanceState The [Bundle] containing saved state values, if any.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            val restoredRounds = it.getInt(ROUNDS_VALUE_KEY, mission.rounds)
            binding.mathRoundsPicker.value = restoredRounds
        }
    }

    /**
     * Called to save the current state of the fragment before it may be destroyed
     * (e.g., during a configuration change like device rotation).
     * This method saves the current value of the math rounds picker so it can be
     * restored later in [onViewStateRestored].
     *
     * @param outState The [Bundle] in which to place saved state values.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ROUNDS_VALUE_KEY, binding.mathRoundsPicker.value)
    }

    /**
     * Cleans up view binding when the view is destroyed to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // ---------------------------------------------------------------------
    // SetUp UI & Listeners Methods
    // ---------------------------------------------------------------------

    /**
     * Populates the UI with the current mission's configuration.
     *
     * - Sets the difficulty slider, label, and times picker.
     * - Displays an example math question for the selected difficulty.
     */
    private fun setupUI() = with(binding) {
        mathRoundsPicker.setFormatter{ viewModel.getLocalizedNumber(it,false) }
        levelSlider.value = mission.difficulty.sliderValue
        mathRoundsPicker.value = mission.rounds
        levelTv.text = getString(selectedDifficulty.labelResId)
        mathExampleTv.text = arithmeticQuestionGenerator.generateQuestion(mission.difficulty).first
    }


    /**
     * Sets up listeners for all interactive elements in the mission bottom sheet.
     *
     * Handles:
     * - **Difficulty slider** — updates the difficulty label and math example preview.
     * - **Complete button** — saves the updated mission, notifies the ViewModel, closes the picker
     *   (if open), and dismisses the sheet.
     * - **Preview button** — starts a mission preview, closes the picker (if open), and dismisses the sheet.
     * - **Close button** — dismisses the sheet and closes the picker (if open).
     * - **Previous button** — dismisses the sheet without saving.
     */

    private fun setupListeners() = with(binding) {

        levelSlider.addOnChangeListener { _, value, _ ->
            val difficulty = Difficulty.fromSliderValue(value)
            levelTv.text = getString(difficulty.labelResId)
            mathExampleTv.text = arithmeticQuestionGenerator.generateQuestion(difficulty).first
        }

        completeBtn.setOnClickListener {
            val updatedMission = mission.copy(difficulty = Difficulty.fromSliderValue(levelSlider.value), rounds = mathRoundsPicker.value)
            val missionHolderPosition = arguments?.getInt(MISSION_ITEM_HOLDER_POSITION_KEY) ?: 0
            viewModel.handleUserEvent(AlarmEditorUserEvent.UpdateAlarmMission(missionHolderPosition, updatedMission))
            MissionPickerBottomSheet.dismissIfVisible(parentFragmentManager)
            dismiss()
        }

        previewBtn.setOnClickListener {
            val previewMission = mission.copy(difficulty = Difficulty.fromSliderValue(levelSlider.value), rounds = mathRoundsPicker.value)
            viewModel.handleUserEvent(AlarmEditorUserEvent.StartAlarmMissionPreview(previewMission))
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

}
