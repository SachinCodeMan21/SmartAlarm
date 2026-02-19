package com.example.smartalarm.feature.alarm.presentation.view.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.extension.getParcelableCompat
import com.example.smartalarm.databinding.TypingMissionBottomsheetBinding
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorUserEvent
import com.example.smartalarm.feature.alarm.presentation.view.fragment.editor.AlarmEditorHomeFragment.Companion.MISSION_ITEM_HOLDER_POSITION_KEY
import com.example.smartalarm.feature.alarm.presentation.viewmodel.editor.AlarmEditorViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/**
 * A [BottomSheetDialogFragment] for configuring a typing-based [Mission].
 *
 * This bottom sheet allows users to select the number of times they must complete a typing task
 * and view the mission type. Users can modify the mission's settings, preview changes,
 * and confirm or discard updates.
 *
 * Features:
 * - Edit an existing typing mission by initializing the UI with the provided [Mission].
 * - Select the number of rounds or repetitions for the mission using a number picker.
 * - Preview the typing mission before confirming changes.
 * - Returns the updated [Mission] and its item position to the parent fragment via
 *   [androidx.fragment.app.FragmentResultListener].
 * - Safely manages view binding to prevent memory leaks.
 *
 * Usage example:
 * ```kotlin
 * val bottomSheet = TypingMissionBottomSheet.newInstance(mission, itemHolderPosition)
 * bottomSheet.show(supportFragmentManager, TypingMissionBottomSheet.TAG)
 * ```
 *
 * @see Mission
 * @see BaseMissionBottomSheet
 */

@AndroidEntryPoint
class TypingMissionBottomSheet : BaseMissionBottomSheet() {

    companion object {
        private const val TAG = "TypingMissionBottomSheet"
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"
        private const val MISSION_ARGS_NULL_ERROR = "$TAG $PASSED_MISSION_ARGS_NULL"

        /**
         * Creates a new instance of [TypingMissionBottomSheet] configured with the provided mission
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
        fun newInstance(mission: Mission, itemHolderPosition: Int): TypingMissionBottomSheet {
            val fragment = TypingMissionBottomSheet()
            val args = Bundle().apply {
                putParcelable(PASSED_MISSION_ARGS_KEY, mission)
                putInt(MISSION_ITEM_HOLDER_POSITION_KEY, itemHolderPosition)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: TypingMissionBottomsheetBinding? = null
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
        mission = requireArguments().getParcelableCompat(PASSED_MISSION_ARGS_KEY) ?: throw IllegalArgumentException(MISSION_ARGS_NULL_ERROR)
    }


    /**
     * Inflates the fragment's layout and returns the root view for the bottom sheet.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TypingMissionBottomsheetBinding.inflate(inflater, container, false)
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
        setupUI()
        setUpListeners()
    }

    /**
     * Restores the state of the fragment's UI components after the view hierarchy
     * has been created. Specifically, it restores the value of the typing rounds picker
     * to the value saved during the previous instance of the fragment (e.g., before a
     * configuration change like rotation).
     *
     * @param savedInstanceState The [Bundle] containing saved state values, if any.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            val restoredRounds = it.getInt(ROUNDS_VALUE_KEY, mission.rounds)
            binding.typingRoundsPicker.value = restoredRounds
        }
    }

    /**
     * Called to save the current state of the fragment before it may be destroyed
     * (e.g., during a configuration change like device rotation).
     * This method saves the current value of the typing rounds picker so it can be
     * restored later in [onViewStateRestored].
     *
     * @param outState The [Bundle] in which to place saved state values.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ROUNDS_VALUE_KEY, binding.typingRoundsPicker.value)
    }

    /**
     * Cleans up the view binding when the view is destroyed to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ---------------------------------------------------------------------
    // SetUp UI & Listeners Methods
    // ---------------------------------------------------------------------

    /**
     * Sets up the initial UI state:
     * - Displays the mission type label.
     * - Sets the number picker to the current `mission.times` value.
     */
    private fun setupUI() = with(binding) {
        typingRoundsPicker.setFormatter{ viewModel.getLocalizedNumber(it,false) }
        typingRoundsPicker.value = mission.rounds
    }


    /**
     * Configures listeners for user interactions within the typing mission bottom sheet UI.
     *
     * Handles updates to the mission configuration, preview actions, and navigation events:
     * - **Complete button** — saves the updated mission data (e.g., selected rounds), notifies the [viewModel] with the updated mission
     *   and its item index, dismisses the bottom sheet, and closes the mission picker if visible.
     * - **Preview button** — starts a preview of the typing mission using the currently selected rounds, dismisses the sheet,
     *   and closes the mission picker if visible.
     * - **Close button** — dismisses the bottom sheet and closes the mission picker if visible.
     * - **Previous button** — dismisses the bottom sheet without saving any changes.
     */
    private fun setUpListeners() = with(binding) {

        completeBtn.setOnClickListener {

            val updatedMission = mission.copy(rounds = typingRoundsPicker.value)
            val missionHolderPosition = arguments?.getInt(MISSION_ITEM_HOLDER_POSITION_KEY) ?: 0

            viewModel.handleUserEvent(AlarmEditorUserEvent.UpdateAlarmMission(missionHolderPosition, updatedMission))
            MissionPickerBottomSheet.dismissIfVisible(parentFragmentManager)
            dismiss()
        }

        previewBtn.setOnClickListener {
            val previewMission = mission.copy( rounds = typingRoundsPicker.value)
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