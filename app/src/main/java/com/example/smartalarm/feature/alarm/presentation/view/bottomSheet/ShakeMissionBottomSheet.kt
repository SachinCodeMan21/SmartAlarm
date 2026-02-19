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
import androidx.core.net.toUri
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.smartalarm.R
import android.view.TextureView.SurfaceTextureListener
import androidx.fragment.app.activityViewModels
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.extension.getParcelableCompat
import com.example.smartalarm.databinding.ShakeMissionBottomsheetBinding
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorUserEvent
import com.example.smartalarm.feature.alarm.presentation.view.fragment.editor.AlarmEditorHomeFragment.Companion.MISSION_ITEM_HOLDER_POSITION_KEY
import com.example.smartalarm.feature.alarm.presentation.viewmodel.editor.AlarmEditorViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/**
 * A [BottomSheetDialogFragment] for configuring a shake-based [Mission].
 *
 * This sheet allows users to select the required number of shakes to complete the mission
 * and includes a looping instructional video to visually demonstrate the action.
 *
 * **Features**
 * - Initializes UI components based on the provided [Mission].
 * - Lets users choose shake rounds using a step-based NumberPicker.
 * - Plays a looping instructional video using a [TextureView] and [MediaPlayer].
 * - Returns the updated mission and its item index to the parent fragment using
 *   the standard result keys defined in [BaseMissionBottomSheet].
 * - Safely manages ViewBinding and media resources to avoid leaks.
 *
 * **Usage**
 * ```kotlin
 * ShakeMissionBottomSheet
 *     .newInstance(mission, itemHolderPosition)
 *     .show(parentFragmentManager, ShakeMissionBottomSheet.TAG)
 * ```
 *
 * @see Mission
 * @see BaseMissionBottomSheet
 */

@AndroidEntryPoint
class ShakeMissionBottomSheet : BaseMissionBottomSheet() {

    companion object {

        private const val TAG = "ShakeMissionBottomSheet"
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"
        private const val MISSION_ARGS_NULL_ERROR = "$TAG $PASSED_MISSION_ARGS_NULL"


        /**
         * Creates a new instance of [ShakeMissionBottomSheet] configured with the provided mission
         * and item position.
         *
         * This factory method initializes the bottom sheet to edit the specified [Mission] and
         * keeps track of the item position that initiated the picker. The position is returned
         * along with the result when the user confirms their selection.
         *
         * @param mission The [Mission] to be edited.
         * @param itemHolderPosition The position or index of the item that initiated the bottom sheet.
         * @return A configured [ShakeMissionBottomSheet] instance ready to be displayed.
         */
        fun newInstance(mission: Mission, itemHolderPosition: Int): ShakeMissionBottomSheet {
            val fragment = ShakeMissionBottomSheet()
            val args = Bundle().apply {
                putParcelable(PASSED_MISSION_ARGS_KEY, mission)
                putInt(MISSION_ITEM_HOLDER_POSITION_KEY, itemHolderPosition)
            }
            fragment.arguments = args
            return fragment
        }

    }

    private var _binding: ShakeMissionBottomsheetBinding? = null
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
        _binding = ShakeMissionBottomsheetBinding.inflate(inflater, container, false)
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
        setUpUI()
        setUpListener()
    }

    /**
     * Restores the state of the fragment's UI components after the view hierarchy
     * has been created. Specifically, it restores the value of the shake rounds picker
     * to the value saved during the previous instance of the fragment (e.g., before a
     * configuration change like rotation).
     *
     * @param savedInstanceState The [Bundle] containing saved state values, if any.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            val restoredRounds = it.getInt(ROUNDS_VALUE_KEY, mission.rounds)
            binding.shakeRoundsPicker.value = restoredRounds
        }
    }

    /**
     * Called to save the current state of the fragment before it may be destroyed
     * (e.g., during a configuration change like device rotation).
     * This method saves the current value of the shake rounds picker so it can be
     * restored later in [onViewStateRestored].
     *
     * @param outState The [Bundle] in which to place saved state values.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ROUNDS_VALUE_KEY, binding.shakeRoundsPicker.value)
    }

    /**
     * Clears the view binding reference to prevent memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // ---------------------------------------------------------------------
    // SetUp UI Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes and updates the shake mission UI using the current [mission] data.
     *
     * This method performs the following:
     * - Sets the mission title based on the mission type.
     * - Sets the shake rounds picker to the mission's current rounds.
     * - Configures the step-based NumberPicker for selecting shake rounds.
     * - Sets up the TextureView for instructional video playback.
     */
    private fun setUpUI() = with(binding) {
        shakeRoundsPicker.value = mission.rounds
        setUpRoundsPicker()
        setUpTextureView()
    }

    /**
     * Configures the shakeRoundPicker to display only multiples of 5 (e.g., 5, 10, ..., 100).
     * Sets the selected value based on [Mission.rounds], defaulting to 5 if invalid or zero.
     */
    private fun setUpRoundsPicker() {
        val step = 5
        val max = 100

        // Pre-format the values for the picker
        val values = (step..max step step).map { viewModel.getLocalizedNumber(it,false) }.toTypedArray()

        binding.shakeRoundsPicker.apply {
            minValue = 0
            maxValue = values.size - 1
            displayedValues = values

            // Set default selection based on mission value or fallback to 5
            val defaultTimes = mission.rounds.takeIf { it > 0 } ?: 5
            value = values.indexOf(defaultTimes.toString()).coerceAtLeast(0)

        }
    }

    /**
     * Configures the [TextureView] to display the shake mission instructional video.
     *
     * This method sets up a [SurfaceTextureListener] to:
     * - Initialize a looping [MediaPlayer] when the surface is available.
     * - Load the shake video from app resources.
     * - Start playback automatically once prepared.
     * - Release the [MediaPlayer] when the surface is destroyed.
     *
     * Additionally, touch interaction on the [TextureView] is disabled
     * to prevent pausing or interfering with video playback.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setUpTextureView() {

        binding.shakeTextureView.surfaceTextureListener = object : SurfaceTextureListener {

            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                mediaPlayer = MediaPlayer().apply {
                    setSurface(Surface(surface))
                    setDataSource(
                        requireContext(),
                        "android.resource://${requireContext().packageName}/${R.raw.shake_video}".toUri()
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
        binding.shakeTextureView.setOnTouchListener { _, _ -> true }
    }


    // ---------------------------------------------------------------------
    // SetUp Listener Method
    // ---------------------------------------------------------------------

    /**
     * Configures listeners for user interactions within the shake mission bottom sheet UI.
     *
     * Handles updates to the mission configuration, preview actions, and navigation events:
     * - **Complete button** — saves the updated mission data (e.g., selected rounds), notifies the [viewModel] with the updated mission
     *   and its item index, dismisses the bottom sheet, and closes the mission picker if visible.
     * - **Preview button** — starts a preview of the shake mission using the currently selected rounds, dismisses the sheet,
     *   and closes the mission picker if visible.
     * - **Close button** — dismisses the bottom sheet and closes the mission picker if visible.
     * - **Previous button** — dismisses the bottom sheet without saving any changes.
     *
     */
    private fun setUpListener() = with(binding) {

        completeBtn.setOnClickListener {
            val selectedIndex = shakeRoundsPicker.value
            val selectedRounds = shakeRoundsPicker.displayedValues[selectedIndex].toInt()
            val updatedMission = mission.copy(rounds = selectedRounds)
            val missionHolderPosition = arguments?.getInt(MISSION_ITEM_HOLDER_POSITION_KEY) ?: 0

            viewModel.handleUserEvent(AlarmEditorUserEvent.UpdateAlarmMission(missionHolderPosition, updatedMission))
            MissionPickerBottomSheet.dismissIfVisible(parentFragmentManager)
            dismiss()
        }

        previewBtn.setOnClickListener {
            val selectedIndex = shakeRoundsPicker.value
            val selectedRounds = shakeRoundsPicker.displayedValues[selectedIndex].toInt()
            val previewMission = mission.copy( rounds = selectedRounds)
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