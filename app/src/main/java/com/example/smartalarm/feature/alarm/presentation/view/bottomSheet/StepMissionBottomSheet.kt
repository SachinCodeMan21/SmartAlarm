package com.example.smartalarm.feature.alarm.presentation.view.bottomSheet

import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.smartalarm.R
import com.example.smartalarm.databinding.StepMissionBottomsheetBinding
import com.example.smartalarm.feature.alarm.domain.model.Mission
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.extension.getParcelableCompat
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorUserEvent
import com.example.smartalarm.feature.alarm.presentation.view.fragment.editor.AlarmEditorHomeFragment.Companion.MISSION_ITEM_HOLDER_POSITION_KEY
import com.example.smartalarm.feature.alarm.presentation.viewmodel.editor.AlarmEditorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/**
 * A [BottomSheetDialogFragment] for configuring a step-based [Mission].
 *
 * This bottom sheet allows users to select the number of steps required to complete the mission
 * and demonstrates the expected action using a looping instructional video. Users can adjust
 * the number of steps and preview the mission configuration before confirming or discarding changes.
 *
 * Features:
 * - Edit an existing step mission by initializing the UI with the provided [Mission].
 * - Select the number of steps (rounds) required for the mission using a step-based number picker.
 * - Watch a looping instructional video demonstrating the action required for the mission.
 * - Sends the updated [Mission] and its item position back to the parent fragment via
 *   [androidx.fragment.app.FragmentResultListener].
 * - Safely manages view binding and handles the lifecycle to prevent memory leaks.
 *
 * Usage example:
 * ```kotlin
 * val bottomSheet = StepMissionBottomSheet.newInstance(mission, itemHolderPosition)
 * bottomSheet.show(supportFragmentManager, StepMissionBottomSheet.TAG)
 * ```
 * @see Mission
 * @see BaseMissionBottomSheet
 */

@AndroidEntryPoint
class StepMissionBottomSheet : BaseMissionBottomSheet() {

    companion object {

        private const val TAG = "StepMissionBottomSheet"
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"
        private const val MISSION_ARGS_NULL_ERROR = "$TAG $PASSED_MISSION_ARGS_NULL"


        /**
         * Creates a new instance of [StepMissionBottomSheet] configured with the provided mission
         * and item position.
         *
         * This factory method initializes the bottom sheet to edit the specified [Mission] and
         * keeps track of the item position that initiated the picker. The position is returned
         * along with the result when the user confirms their selection.
         *
         * @param mission The [Mission] to be edited.
         * @param itemHolderPosition The position or index of the item that initiated the bottom sheet.
         * @return A configured [StepMissionBottomSheet] instance ready to be displayed.
         */
        fun newInstance(mission: Mission, itemHolderPosition: Int): StepMissionBottomSheet {
            val fragment = StepMissionBottomSheet()
            val args = Bundle().apply {
                putParcelable(PASSED_MISSION_ARGS_KEY, mission)
                putInt(MISSION_ITEM_HOLDER_POSITION_KEY, itemHolderPosition)
            }
            fragment.arguments = args
            return fragment
        }

    }

    private var _binding: StepMissionBottomsheetBinding? = null
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)
    private val viewModel: AlarmEditorViewModel by activityViewModels()
    private var mediaPlayer: MediaPlayer? = null
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StepMissionBottomsheetBinding.inflate(inflater, container, false)
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
        setUpUI()
        setUpListener()
    }

    /**
     * Restores the state of the fragment's UI components after the view hierarchy
     * has been created. Specifically, it restores the value of the step rounds picker
     * to the value saved during the previous instance of the fragment (e.g., before a
     * configuration change like rotation).
     *
     * @param savedInstanceState The [Bundle] containing saved state values, if any.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            val restoredRounds = it.getInt(ROUNDS_VALUE_KEY, mission.rounds)
            binding.stepRoundsPicker.value = restoredRounds
        }
    }

    /**
     * Called to save the current state of the fragment before it may be destroyed
     * (e.g., during a configuration change like device rotation).
     * This method saves the current value of the step rounds picker so it can be
     * restored later in [onViewStateRestored].
     *
     * @param outState The [Bundle] in which to place saved state values.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ROUNDS_VALUE_KEY, binding.stepRoundsPicker.value)
    }

    /**
     * Cleans up view binding when the view is destroyed to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ---------------------------------------------------------------------
    // SetUp UI Methods
    // ---------------------------------------------------------------------

    /**
     * Sets up the UI components:
     * - Displays the mission type title.
     * - Initializes the step-based NumberPicker for selecting step count.
     * - Loads and starts a looping instructional video.
     * - Disables touch interaction with the video view to avoid interruptions.
     */
    private fun setUpUI(){
        setUpTimesPicker()
        setUpTextureView()
    }

    /**
     * Configures the stepTimesPicker to display step values from 5 to 100 in increments of 5.
     * Sets the default value based on [Mission.rounds], defaulting to 5 if not provided.
     */
    private fun setUpTimesPicker() {
        val step = 5
        val max = 100
        // Format the values array first using toLocalizedString before assigning it to the NumberPicker
        val values = (step..max step step).map { viewModel.getLocalizedNumber(it,false) }.toTypedArray()

        binding.stepRoundsPicker.apply {
            minValue = 0
            maxValue = values.size - 1
            displayedValues = values

            // Set default selection based on mission value or fallback to 5
            val defaultTimes = mission.rounds.takeIf { it > 0 } ?: 5
            value = values.indexOf(defaultTimes.toString()).coerceAtLeast(0)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpTextureView() {

        binding.stepTextureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {

            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                mediaPlayer = MediaPlayer().apply {
                    setSurface(Surface(surface))
                    setDataSource(
                        requireContext(),
                        "android.resource://${requireContext().packageName}/${R.raw.steps_video}".toUri()
                    )
                    isLooping = true
                    setOnPreparedListener { start() }
                    prepareAsync()
                }
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                mediaPlayer?.release()
                mediaPlayer = null
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }


        // Disable touch on the video view to prevent pause/play interruptions
        binding.stepTextureView.setOnTouchListener { _, _ -> true }
    }


    // ---------------------------------------------------------------------
    // SetUp Listener Methods
    // ---------------------------------------------------------------------

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
     *
     */
    private fun setUpListener() = with(binding) {

        completeBtn.setOnClickListener {
            val selectedIndex = stepRoundsPicker.value
            val selectedRounds = stepRoundsPicker.displayedValues[selectedIndex].toInt()
            val updatedMission = mission.copy(rounds = selectedRounds)
            val missionHolderPosition = arguments?.getInt(MISSION_ITEM_HOLDER_POSITION_KEY) ?: 0

            viewModel.handleUserEvent(AlarmEditorUserEvent.MissionEvent.Updated(missionHolderPosition, updatedMission))
            MissionPickerBottomSheet.dismissIfVisible(parentFragmentManager)
            dismiss()
        }

        previewBtn.setOnClickListener {
            val selectedIndex = stepRoundsPicker.value
            val selectedRounds = stepRoundsPicker.displayedValues[selectedIndex].toInt()
            val previewMission = mission.copy( rounds = selectedRounds)
            viewModel.handleUserEvent(AlarmEditorUserEvent.MissionEvent.Preview(previewMission))
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