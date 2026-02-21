package com.example.smartalarm.feature.alarm.presentation.view.bottomSheet

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.Constants.PACKAGE
import com.example.smartalarm.core.utility.extension.getParcelableCompat
import com.example.smartalarm.databinding.BottomSheetMissionPickerLayoutBinding
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.domain.model.MissionType
import com.example.smartalarm.feature.alarm.presentation.adapter.MissionTypePickerAdapter
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorUserEvent
import com.example.smartalarm.feature.alarm.presentation.view.fragment.editor.AlarmEditorHomeFragment.Companion.MISSION_ITEM_HOLDER_POSITION_KEY
import com.example.smartalarm.feature.alarm.presentation.viewmodel.editor.AlarmEditorViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

/**
 * A [BottomSheetDialogFragment] for selecting a [MissionType] for an alarm.
 *
 * This bottom sheet displays a list of available mission types and allows the user to:
 * - Add a new mission by selecting from available types.
 * - Edit an existing mission with the current type pre-selected.
 * - Prevent selection of mission types that are already used elsewhere.
 *
 * When a mission is selected, the result (the chosen [Mission] and the associated item
 * position) is sent to the [AlarmEditorViewModel] via
 * [AlarmEditorUserEvent.MissionEvent.Selected] for further processing.
 *
 * The bottom sheet manages its own view binding lifecycle to prevent memory leaks and
 * ensures a fully expanded modal behavior when displayed.
 *
 * ## Usage
 * Use [newInstance] to create and display the bottom sheet:
 * ```kotlin
 * val bottomSheet = MissionPickerBottomSheet.newInstance(existingMission, usedTypes, position)
 * bottomSheet.show(supportFragmentManager, MissionPickerBottomSheet.TAG)
 * ```
 *
 * This fragment provides:
 * - Automatic expansion to full height on display.
 * - Exclusion of mission types that are already in use.
 * - Reuse of the current mission if the selected type matches.
 * - Callback handling via the [viewModel] for mission selection.
 *
 * @see Mission
 * @see MissionType
 * @see AlarmEditorViewModel
 * @see AlarmEditorUserEvent
 * @see MissionTypePickerAdapter
 */
@AndroidEntryPoint
class MissionPickerBottomSheet() : BottomSheetDialogFragment() {

    companion object {

        const val TAG = "MissionPickerBottomSheet"
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"
        private const val ARG_EXISTING_MISSION_KEY = "$PACKAGE.ARG_EXISTING_MISSION_KEY"
        private const val ARG_USED_MISSION_TYPES_KEY = "$PACKAGE.ARG_USED_MISSION_TYPES_KEY"


        /**
         * Dismisses the [MissionPickerBottomSheet] if it is currently visible.
         *
         * This safely checks whether a fragment with the [TAG] exists in the provided
         * [FragmentManager] and dismisses it if it is shown as a [DialogFragment].
         *
         * @param fragmentManager The [FragmentManager] used to look up and dismiss the bottom sheet.
         */
        fun dismissIfVisible(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as? DialogFragment)?.dismiss()
        }

        /**
         * Creates a new instance of [MissionPickerBottomSheet] with the necessary arguments.
         *
         * This factory method initializes a [MissionPickerBottomSheet] configured to display
         * available mission types while excluding those that are already in use. It also passes
         * along the currently assigned mission (if any) and the position of the item that is
         * being edited, so the selected result can later be associated with the correct context.
         *
         * @param existingMission The mission currently assigned to this position, or `null` if none.
         * @param usedTypes The set of [MissionType] values that are already used elsewhere and
         * should be excluded from the picker.
         * @param itemHolderPosition The position or index of the item (e.g., mission slot)
         * that initiated the picker. This value is returned in the result bundle when the
         * user selects a mission.
         *
         * @return A configured [MissionPickerBottomSheet] instance ready to be displayed.
         */
        fun newInstance(
            existingMission: Mission? = null,
            usedTypes: Set<MissionType> = emptySet(),
            itemHolderPosition : Int
        ): MissionPickerBottomSheet {

            val args = Bundle().apply {
                putParcelable(ARG_EXISTING_MISSION_KEY, existingMission)
                putParcelableArrayList(ARG_USED_MISSION_TYPES_KEY, ArrayList(usedTypes))
                putInt(MISSION_ITEM_HOLDER_POSITION_KEY,itemHolderPosition)
            }
            val fragment = MissionPickerBottomSheet()
            fragment.arguments = args
            return fragment
        }

    }

    private var _binding: BottomSheetMissionPickerLayoutBinding? = null
    private val binding get() = _binding?:error(BINDING_NULL_ERROR)
    private val viewModel: AlarmEditorViewModel by activityViewModels()
    private lateinit var missionTypePickerAdapter: MissionTypePickerAdapter
    private val existingMission: Mission? by lazy {
        arguments?.getParcelableCompat<Mission>(ARG_EXISTING_MISSION_KEY)
    }
    private val usedTypes: Set<MissionType> by lazy {
        val bundle = arguments
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle?.getParcelableArrayList(ARG_USED_MISSION_TYPES_KEY, MissionType::class.java)?.toSet()
                ?: emptySet()
        } else {
            @Suppress("DEPRECATION")
            bundle?.getParcelableArrayList<MissionType>(ARG_USED_MISSION_TYPES_KEY)?.toSet() ?: emptySet()
        }
    }
    private val missionHolderPosition : Int by lazy {
        arguments?.getInt(MISSION_ITEM_HOLDER_POSITION_KEY) ?: 0
    }



    // ---------------------------------------------------------------------
    // Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Inflates the layout for the bottom sheet using ViewBinding.
     *
     * @return The root view of the bottom sheet.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetMissionPickerLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called when the view has been created.
     *
     * Initializes the mission picker adapter, sets up the RecyclerView
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initMissionPickerAdapter()
        setUpMissionPickerRecyclerView()
    }

    /**
     * Called when the Fragment is visible to the user and actively running.
     *
     * In this override, the BottomSheet dialog is configured to:
     * - Automatically expand to full height (STATE_EXPANDED)
     * - Skip the collapsed state (skipCollapsed = true)
     * - Allow user interaction via drag gestures (isDraggable = true)
     *
     * These settings are applied to ensure the bottom sheet behaves like a fully expanded modal
     * when it appears, enhancing the user experience.
     */
    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.isDraggable = true
            }
        }
    }


    /**
     * Cleans up the binding to prevent memory leaks when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // ---------------------------------------------------------------------
    // SetUp UI
    // ---------------------------------------------------------------------

    /**
     * Initializes the [MissionTypePickerAdapter] with the currently selected mission (if any),
     * and the set of [usedTypes] that should be excluded from the list.
     *
     * The adapter is also configured with a callback to handle mission selection.
     */
    private fun initMissionPickerAdapter() {
        missionTypePickerAdapter = MissionTypePickerAdapter(
            selectedMissionType = existingMission?.type,
            usedTypes = usedTypes,
            onMissionSelection = ::handleMissionSelection
        )
    }

    /**
     * Sets up the RecyclerView that displays the list of selectable mission types.
     *
     * Applies a vertical [LinearLayoutManager], enables fixed sizing,
     * and assigns the initialized [missionTypePickerAdapter] as the adapter,
     * and submits the list of available mission types to the adapter.
     */
    private fun setUpMissionPickerRecyclerView() {
        binding.allMissionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = missionTypePickerAdapter
        }
        missionTypePickerAdapter.submitList(MissionType.getAllAvailableMissionTypes())
    }


    // ---------------------------------------------------------------------
    // Handle MissionPicker CLick Method
    // ---------------------------------------------------------------------

    /**
     * Handles the user's selection of a [MissionType] from the mission picker.
     *
     * Behavior:
     * - If there is no existing mission, or the selected type differs from the current mission's type,
     *   a new [Mission] instance is created with the selected type and its corresponding icon resource.
     * - If the selected type matches the current mission, the existing mission is reused.
     *
     * The resulting [Mission], along with the associated item position
     * ([MISSION_ITEM_HOLDER_POSITION_KEY]), is sent to the [viewModel] via
     * [AlarmEditorUserEvent.MissionEvent.Selected] for further processing.
     *
     * After handling the selection, the bottom sheet should be dismissed.
     *
     * @param selectedMissionType The [MissionType] chosen by the user from the UI.
     */
    private fun handleMissionSelection(selectedMissionType: MissionType) {

        val currentMission = existingMission

        val selectedMission = if (currentMission == null || currentMission.type != selectedMissionType) {
            Mission(type = selectedMissionType, iconResId = selectedMissionType.getIconRes())
        } else {
            currentMission
        }
        viewModel.handleUserEvent(AlarmEditorUserEvent.MissionEvent.Selected(missionHolderPosition, selectedMission))
    }



}
