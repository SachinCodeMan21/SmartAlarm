package com.example.smartalarm.feature.alarm.presentation.view.fragment.editor

import android.app.Activity
import androidx.core.view.isVisible
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.Constants.BINDING_NULL
import com.example.smartalarm.core.utility.Constants.PACKAGE
import com.example.smartalarm.core.utility.extension.showToast
import com.example.smartalarm.core.permission.PermissionManager
import com.example.smartalarm.databinding.FragmentAlarmEditorBinding
import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.domain.model.MissionItem
import com.example.smartalarm.feature.alarm.domain.model.SnoozeSettings
import com.example.smartalarm.feature.alarm.domain.model.MissionType
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmKeys
import com.example.smartalarm.feature.alarm.presentation.adapter.AlarmMissionAdapter
import com.example.smartalarm.feature.alarm.presentation.effect.editor.AlarmEditorEffect
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorSystemEvent
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorUserEvent
import com.example.smartalarm.feature.alarm.presentation.view.activity.AlarmActivity
import com.example.smartalarm.feature.alarm.presentation.view.bottomSheet.BaseMissionBottomSheet
import com.example.smartalarm.feature.alarm.presentation.view.bottomSheet.MissionPickerBottomSheet
import com.example.smartalarm.feature.alarm.presentation.view.handler.PermissionHandler
import com.example.smartalarm.feature.alarm.presentation.viewmodel.editor.AlarmEditorViewModel
import com.example.smartalarm.feature.alarm.utility.getParcelableExtraCompat
import com.example.smartalarm.feature.alarm.utility.onProgressChangedListener
import com.example.smartalarm.feature.alarm.utility.setBackgroundDrawableIfDifferent
import com.example.smartalarm.feature.alarm.utility.setCheckedIfDifferent
import com.example.smartalarm.feature.alarm.utility.setProgressIfDifferent
import com.example.smartalarm.feature.alarm.utility.setTextColorIfDifferent
import com.example.smartalarm.feature.alarm.utility.setTextIfDifferent
import com.example.smartalarm.feature.alarm.utility.setValueIfDifferent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * `AlarmEditorHomeFragment` is responsible for providing the user interface and managing user interactions
 * for creating or editing an alarm. It serves as the central hub for setting up alarm details such as time, label,
 * repeat days, sound, vibration, mission assignments, and snooze settings.
 *
 * The fragment communicates with the ViewModel to manage alarm data and reacts to user inputs by dispatching events
 * to the ViewModel. It observes the state of the alarm and updates the UI accordingly. Additionally, the fragment handles
 * one-time UI effects such as navigating to other screens or showing toasts.
 *
 * The core responsibilities of this fragment include:
 * - Allowing users to create or edit an alarm by providing a detailed form for time, label, sound, and other settings.
 * - Managing the state and handling UI updates for various components, including time pickers, weekdays, and sound settings.
 * - Reacting to user actions such as changing the alarm time, selecting days, modifying the label, and configuring snooze settings.
 * - Navigating to related screens, like snooze settings or mission previews, and responding to permission requests.
 * - Observing lifecycle events to ensure efficient handling of user input and UI updates.
 *
 * This fragment utilizes a flow-based architecture and works with Jetpack Navigation and ViewModel to ensure that user
 * interactions are processed reactively, keeping the UI in sync with the underlying data.
 */
@AndroidEntryPoint
class AlarmEditorHomeFragment : Fragment() {

    companion object {

        // Tag for logging within AlarmEditorHomeFragment
        private const val TAG = "AlarmEditorHomeFragment"

        // Error message for null view binding reference
        private const val BINDING_NULL_ERROR = "$TAG $BINDING_NULL"

        // Key for passing mission item holder position in the bundle
        const val MISSION_ITEM_HOLDER_POSITION_KEY = "$PACKAGE.MISSION_ITEM_HOLDER_POSITION_KEY"
    }

    private var _binding: FragmentAlarmEditorBinding? = null

    // Final Instance Fields
    private val binding get() = _binding ?: error(BINDING_NULL_ERROR)
    private val viewModel: AlarmEditorViewModel by activityViewModels()
    private val args: AlarmEditorHomeFragmentArgs by navArgs()


    // Non-Final Instance Fields
    private lateinit var weekdayViews: List<TextView>
    private lateinit var missionAdapter: AlarmMissionAdapter
    private lateinit var alarmEditorPermissionHandler: PermissionHandler
    private lateinit var alarmRingtonePickerLauncher: ActivityResultLauncher<Intent>


    // Injected Dependencies
    @Inject
    lateinit var permissionManager: PermissionManager


    // Flag to track if the Switch has been initially triggered (to prevent handling the default state first event)
    private var isInitialSwitchTriggerFlag : Boolean = true

    // Flag to track if the SeekBar has been initially triggered (to prevent handling the default state first event)
    private var isInitialSeekBarTriggerFlag : Boolean = true


    // ---------------------------------------------------------------------
    // 1] Lifecycle Methods
    // ---------------------------------------------------------------------

    /**
     * Initializes the alarm editor state when the fragment is created.
     *
     * If the fragment is created for the first time (`savedInstanceState` is null), it checks `args.existingAlarmId`:
     * - Loads existing alarm data if `existingAlarmId` > 0.
     * - Initializes a new alarm if not.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            viewModel.handleSystemEvent(AlarmEditorSystemEvent.InitializeAlarmEditorState(args.existingAlarmId))
        }
    }

    /**
     * Called to inflate the fragment's view hierarchy.
     *
     * Uses view binding to inflate and return the root layout.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAlarmEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Sets up the fragment's UI and event handling once the view is created.
     * This includes binding UI elements, attaching listeners, observing ViewModel state,
     * and preparing for external interactions like permission requests and activity results.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setUpListeners()
        setUpUIStateObserver()
        setUpUIEffectObserver()
        setUpAlarmEditorPermissionHandler()
        registerAlarmSoundPickerLauncher()
    }

    /**
     * Called when the fragment's is being destroyed.
     *
     * - Clears the binding reference to prevent memory leaks.
     */
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



    // ---------------------------------------------------------------------
    //  2] Initial UI Setup Methods
    // ---------------------------------------------------------------------

    /**
     * Sets up the fragment's UI components, including:
     * - Initializing weekday views and time picker.
     * - Configuring the alarm mission RecyclerView and adapter.
     * - Setting the save/update button text based on alarm state.
     */
    private fun setupUI() = with(binding) {

        // Initializing weekday views
        weekdayViews = with(weekdaysBlock) { listOf(day1, day2, day3, day4, day5, day6, day7) }

        // Configuring the alarm time picker block
        timePickerBlock.apply {
            hoursPicker.setFormatter { viewModel.getLocalizedNumber(it) }
            minutePicker.setFormatter { viewModel.getLocalizedNumber(it) }
            amPmPicker.apply {
                val amPmList = resources.getStringArray(R.array.meri_diem_list)
                minValue = 0
                maxValue = amPmList.lastIndex
                displayedValues = amPmList
            }
        }

        // Setting up the mission adapter
        missionAdapter = AlarmMissionAdapter(
            onMissionItemPlaceholderClick = {
                viewModel.handleUserEvent(AlarmEditorUserEvent.HandleMissionItemPlaceHolderClick(it))
            },
            onMissionItemClick = { position, mission ->
                viewModel.handleUserEvent(AlarmEditorUserEvent.HandleMissionItemClick(position, mission))
            },
            onRemoveMissionClick = {
                viewModel.handleUserEvent(AlarmEditorUserEvent.HandleRemoveMissionClick(it))
            }
        )

        // Setting up the alarm mission RecyclerView
        missionBlock.missionRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = missionAdapter
        }

        // Setting up correct saveAlarmButton title
        val saveUpdateBtnTitle = getString(if (args.existingAlarmId !=0 ) R.string.update else R.string.save)
        saveOrUpdateAlarmBtn.text =  saveUpdateBtnTitle

    }


    /**
     * Sets up event listeners to handle user interactions and trigger corresponding ViewModel events.
     * This includes handling changes to alarm label, time, weekdays, volume, vibration, sound, snooze, and save/update actions.
     */

    private fun setUpListeners() = with(binding) {

        alarmLabelET.doAfterTextChanged {
            viewModel.handleUserEvent(AlarmEditorUserEvent.LabelChanged(it?.trim().toString()))
        }

        timePickerBlock.apply {
            val notifyTimeChanged = {
                viewModel.handleUserEvent(
                    AlarmEditorUserEvent.TimeChanged(hoursPicker.value, minutePicker.value, amPmPicker.value)
                )
            }
            listOf(hoursPicker, minutePicker, amPmPicker).forEach { picker ->
                picker.setOnValueChangedListener { _, _, _ -> notifyTimeChanged() }
            }
        }

        weekdaysBlock.apply {

            isDailyCheckBox.setOnCheckedChangeListener { _, isChecked ->
                viewModel.handleUserEvent(AlarmEditorUserEvent.IsDailyChanged(isChecked))
            }

            weekdayViews.forEachIndexed { index, dayView ->
                dayView.setOnClickListener { viewModel.handleUserEvent(AlarmEditorUserEvent.DayToggled(index)) }
            }

        }

        soundBlock.apply {

            alarmVolumeSeekBar.onProgressChangedListener { progress, _ ->
                if (!isInitialSeekBarTriggerFlag){
                    viewModel.handleUserEvent(AlarmEditorUserEvent.VolumeChanged(progress))
                }
                isInitialSeekBarTriggerFlag = false
            }

            vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (!isInitialSwitchTriggerFlag){
                    viewModel.handleUserEvent(AlarmEditorUserEvent.VibrationToggled(isChecked))
                }
                isInitialSwitchTriggerFlag = false
            }

            alarmSoundTv.setOnClickListener {
                viewModel.handleUserEvent(AlarmEditorUserEvent.LaunchAlarmSoundPicker)
            }

        }

        snoozeBlock.alarmSnoozeTv.setOnClickListener {
            viewModel.handleUserEvent(AlarmEditorUserEvent.EditSnoozeClick)
        }

        saveOrUpdateAlarmBtn.setOnClickListener {
            viewModel.handleUserEvent(AlarmEditorUserEvent.SaveOrUpdateAlarmClick)
        }

    }


    /**
     * Observes and updates the UI state in response to changes from the ViewModel.
     * Ensures the UI reflects the latest state for alarm label, time, weekdays, missions, sound, and snooze options.
     */
    private fun setUpUIStateObserver() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collectLatest { newState ->
                    with(newState){
                        updateAlarmLabel(label)
                        updateTimePickerBlock(hour,minute,amPm)
                        updateWeekdaysBlock(isDailyAlarm,selectedDays)
                        updateMissionBlock(missionItemList,formattedMissionSlotText)
                        updateSoundBlock(volume,isVibrateEnabled,alarmSoundTitle)
                        updateSnoozeBlock(formattedSnoozedText)
                    }
                }
            }
        }
    }


    /**
     * Observes and handles UI effects from the ViewModel, triggering appropriate actions like navigation,
     * permission requests, showing bottom sheets for mission selection, showing toasts, loaders, and finish activity .
     */
    private fun setUpUIEffectObserver() {
        lifecycleScope.launch {
           repeatOnLifecycle(Lifecycle.State.STARTED){
               viewModel.uiEffect.collectLatest { newEffect ->
                   when(newEffect){

                       // Navigation Effects
                       is AlarmEditorEffect.NavigateToSnoozeAlarmFragment -> navigateToAlarmSnoozeFragment(newEffect.snoozeSettings)
                       is AlarmEditorEffect.NavigateToAlarmActivityForMissionPreview -> navigateToAlarmMissionActivity(newEffect.previewAlarmModel)


                       // Permission & Ringtone Launchers
                       is AlarmEditorEffect.LaunchPostNotificationPermissionRequest -> alarmEditorPermissionHandler.requestPostNotification()
                       is AlarmEditorEffect.LaunchFullScreenNotificationPermissionRequest -> alarmEditorPermissionHandler.requestFullScreenNotificationPermission()
                       is AlarmEditorEffect.LaunchExactAlarmPermissionRequest -> alarmEditorPermissionHandler.requestExactAlarmPermission()
                       is AlarmEditorEffect.LaunchAlarmSoundPicker -> alarmRingtonePickerLauncher.launch(createRingtonePickerIntent(newEffect.existingAlarmSound))


                       // Show Mission Picker, SelectedMission BottomSheet
                       is AlarmEditorEffect.ShowMissionPickerBottomSheet -> {
                           newEffect.apply { showMissionPickerBottomSheet(position, existingMission, usedMissions) }
                       }
                       is AlarmEditorEffect.ShowSelectedMissionBottomSheet -> {
                           newEffect.apply { showSelectedMissionBottomSheet(position, selectedMission)  }
                       }

                       // Show Loader, Toast
                       is AlarmEditorEffect.ShowSaveUpdateLoadingIndicator -> handleShowSaveUpdateLoading(newEffect.isLoading)
                       is AlarmEditorEffect.ShowToastMessage -> showToastMessage(newEffect.toastMessage)
                       is AlarmEditorEffect.ShowError -> showToastMessage(newEffect.message)

                       // Finish Editor Activity
                       is AlarmEditorEffect.FinishEditorActivity ->  activity?.finish()

                   }
               }
           }
        }
    }


    /**
     * Initializes the alarm editor permission handler for managing permissions
     * required during the alarm editing process (e.g., exact alarm, post notification permissions).
     * This handler ensures that permissions are requested and managed properly.
     */
    private fun setUpAlarmEditorPermissionHandler() {
        alarmEditorPermissionHandler = PermissionHandler(
            fragment = this,
            myPermissionManager = permissionManager,
            onPostNotificationGranted = { viewModel.handleSystemEvent(AlarmEditorSystemEvent.RetryPendingSaveAction) },
        )
    }

    /**
     * Registers the alarm sound picker launcher to allow the user to pick a ringtone
     * for the alarm. This launcher listens for the result of the ringtone picker activity
     * and handles the selected URI to notify the ViewModel with the user's choice.
     */
    private fun registerAlarmSoundPickerLauncher() {
        alarmRingtonePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.getParcelableExtraCompat<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                uri?.let {
                    viewModel.handleUserEvent(AlarmEditorUserEvent.RingtoneSelected(it.toString()))
                }
            }
        }
    }




    // ---------------------------------------------------------------------
    // 3] Handles UI Updates
    // ---------------------------------------------------------------------

    private fun updateAlarmLabel(label: String?) {
        binding.alarmLabelET.setTextIfDifferent(label)
    }

    private fun updateTimePickerBlock(hour:Int, minute:Int, amPm:Int) {
        binding.timePickerBlock.apply {
            hoursPicker.setValueIfDifferent(hour)
            minutePicker.setValueIfDifferent(minute)
            amPmPicker.setValueIfDifferent(amPm)
        }
    }

    private fun updateWeekdaysBlock(isDailyAlarm: Boolean, selectedDays:Set<DayOfWeek>) {
        binding.weekdaysBlock.apply {

            isDailyCheckBox.setCheckedIfDifferent(isDailyAlarm)

            weekdayViews.forEachIndexed { i, view ->
                val day = DayOfWeek.getDayAtPositionOrNull(i)
                day?.let {
                    val isSelected = day in selectedDays
                    val bgRes = if (isSelected) R.drawable.selected_circular_background else R.drawable.unselected_circular_background
                    val textColor = if (isSelected) android.R.color.white else android.R.color.darker_gray
                    view.setBackgroundDrawableIfDifferent(bgRes)
                    view.setTextColorIfDifferent(textColor)
                }
            }
        }
    }

    private fun updateMissionBlock(missionItemList:List<MissionItem>, formattedMissionSlotText: String ) {
        binding.missionBlock.apply {
            missionAdapter.submitList(missionItemList)
            missionCount.text = formattedMissionSlotText
        }
    }

    private fun updateSoundBlock(volume:Int, isVibrateEnabled: Boolean, alarmSoundTitle:String) {
        binding.soundBlock.apply {
            alarmVolumeSeekBar.setProgressIfDifferent(volume)
            vibrateSwitch.setCheckedIfDifferent(isVibrateEnabled)
            alarmSoundTv.setTextIfDifferent(alarmSoundTitle)
        }
    }

    private fun updateSnoozeBlock(formattedSnoozedText: String) {
        binding.snoozeBlock.alarmSnoozeTv.setTextIfDifferent(formattedSnoozedText)
    }





    // ---------------------------------------------------------------------
    //  4] Handles UI Effects
    // ---------------------------------------------------------------------


    /**
     * Launches the AlarmActivity to preview a specific alarm mission.
     * This is triggered when the user selects a mission preview, passing the alarm data to the AlarmActivity
     * for display and interaction, allowing the user to review the alarm details and mission before saving.
     */
    private fun navigateToAlarmMissionActivity(alarmPreviewModel : AlarmModel){
        val intent = Intent(requireContext(), AlarmActivity::class.java)
        intent.putExtra(AlarmKeys.ALARM_ID, alarmPreviewModel.id)
        intent.putExtra(AlarmActivity.PREVIEW_MISSION_KEY, alarmPreviewModel)
        requireContext().startActivity(intent)
    }

    /**
     * Navigates to the SnoozeFragment to allow the user to adjust the snooze settings for the alarm.
     * This provides a dedicated screen for managing snooze behavior before finalizing the alarm setup.
     */
    private fun navigateToAlarmSnoozeFragment(snoozeSettings: SnoozeSettings) {
        val action = AlarmEditorHomeFragmentDirections.actionAlarmEditorFragmentToSnoozeFragment(snoozeSettings)
        findNavController().navigate(action)
    }

    /** Toggles the visibility of the save progress bar and button based on the loading state. */
    private fun handleShowSaveUpdateLoading(isLoading: Boolean) {
        binding.saveProgressBar.isVisible = isLoading
        binding.saveOrUpdateAlarmBtn.isVisible = !isLoading
    }

    /** Displays a toast message with the provided text.*/
    private fun showToastMessage(toastMessage: String){
        requireContext().showToast(toastMessage)
    }

    /**
     * Shows the [MissionPickerBottomSheet] for selecting or editing a mission.
     *
     * It builds and launches the picker, filtering out mission types already used
     * in other slots. The current mission (if any) and the index of the slot being
     * edited are passed to the sheet.
     *
     * @param position The index of the mission slot being edited.
     * @param existingMission The mission currently assigned to this slot, or `null` if none.
     * @param usedMissions Missions assigned to other slots, used to filter out unavailable types.
     */
    private fun showMissionPickerBottomSheet(position: Int, existingMission: Mission?, usedMissions: List<Mission>) {
        val usedTypes = usedMissions.map { it.type }.toSet()
        MissionPickerBottomSheet.newInstance(
            existingMission = existingMission,
            usedTypes = usedTypes,
            itemHolderPosition = position
        ).show(childFragmentManager, MissionPickerBottomSheet.TAG)
    }

    /**
     * Shows the appropriate details bottom sheet for the provided mission.
     *
     * The sheet type is determined by the mission’s [MissionType]
     *
     * @param position The index of the mission slot being edited.
     * @param selectedMission The mission to view or edit.
     */
    private fun showSelectedMissionBottomSheet(position: Int, selectedMission: Mission) {
        selectedMission.type
            .getMissionBottomSheet(selectedMission, position)
            .show(childFragmentManager, BaseMissionBottomSheet.TAG)
    }



    // ---------------------------------------------------------------------
    //  5] Helper Method
    // ---------------------------------------------------------------------

    /**
     * Creates an Intent to launch the ringtone picker for selecting a new alarm sound.
     * Allows the user to choose a new ringtone, use the default, or keep the existing one.
     *
     * @param existingAlarmSound URI of the current ringtone, or `null` if none exists.
     * @return Configured Intent to open the ringtone picker.
     */
    fun createRingtonePickerIntent(existingAlarmSound: String): Intent {
        return Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.select_alarm_ringtone))
            putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, existingAlarmSound.toUri())
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
        }
    }

}