package com.example.smartalarm.feature.alarm.presentation.view.handler

import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.smartalarm.core.utility.extension.showToast
import com.example.smartalarm.feature.alarm.framework.broadcasts.constants.AlarmKeys
import com.example.smartalarm.feature.alarm.presentation.effect.editor.AlarmEditorEffect
import com.example.smartalarm.feature.alarm.presentation.view.activity.AlarmActivity
import com.example.smartalarm.feature.alarm.presentation.view.binder.AlarmEditorUiBinder
import com.example.smartalarm.feature.alarm.presentation.view.bottomSheet.MissionPickerBottomSheet
import com.example.smartalarm.feature.alarm.presentation.view.fragment.editor.AlarmEditorHomeFragmentDirections
import com.example.smartalarm.feature.alarm.presentation.viewmodel.editor.AlarmEditorViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Handles all one-time UI and navigation side-effects emitted by [AlarmEditorViewModel]
 * in the Alarm Editor feature.
 *
 * This class observes [AlarmEditorViewModel.uiEffect] and executes effects in a lifecycle-aware
 * manner, ensuring that UI updates and navigation occur only when the [lifecycleOwner]
 * is at least in the STARTED state. It decouples side-effect handling from UI rendering
 * logic, delegating view updates to [AlarmEditorUiBinder] and navigation to [fragment].
 *
 * Supported effects include:
 * 1. Navigation:
 *    - Navigate to the Snooze Alarm Fragment.
 *    - Navigate to the Alarm Activity for mission preview.
 * 2. UI Modifications:
 *    - Show or hide the save/update loading indicator.
 *    - Update views via [uiBinder].
 *    - Display toast messages and error messages.
 * 3. Bottom Sheets:
 *    - Launch mission picker or selected mission bottom sheets.
 * 4. System Intents:
 *    - Launch the ringtone picker using [ringtoneLauncher].
 * 5. Activity Lifecycle:
 *    - Finish the Alarm Editor activity.
 *
 * @property fragment The [Fragment] hosting the UI and navigation context.
 * @property uiBinder The [AlarmEditorUiBinder] responsible for updating view states.
 * @property viewModel The [AlarmEditorViewModel] emitting UI effects to handle.
 * @property lifecycleOwner The [LifecycleOwner] used to safely observe the ViewModel.
 * @property ringtoneLauncher The [ActivityResultLauncher] for launching the system ringtone picker.
 *
 * @see AlarmEditorViewModel
 * @see AlarmEditorEffect
 * @see AlarmEditorUiBinder
 */
class AlarmEditorEffectHandler(
    private val fragment: Fragment,
    private val uiBinder: AlarmEditorUiBinder,
    private val viewModel: AlarmEditorViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val ringtoneLauncher: ActivityResultLauncher<Intent>
) {

    init {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEffect.collectLatest {
                    Log.d("TAG","AlarmEditorEffectHandler collectLatest executed with = $it")
                    handle(it)
                }
            }
        }
    }


    /**
     * Dispatches the received [AlarmEditorEffect] to the corresponding private handler method.
     */
    private fun handle(effect: AlarmEditorEffect) {
        when (effect) {
            is AlarmEditorEffect.NavigateToSnoozeAlarmFragment -> navigateToSnoozeFragment(effect)
            is AlarmEditorEffect.NavigateToAlarmActivityForMissionPreview -> navigateToAlarmActivity(effect)
            is AlarmEditorEffect.LaunchAlarmSoundPicker -> launchAlarmSoundPicker(effect)
            is AlarmEditorEffect.ShowMissionPickerBottomSheet -> showMissionPickerBottomSheet(effect)
            is AlarmEditorEffect.ShowSelectedMissionBottomSheet -> showSelectedMissionBottomSheet(effect)
            is AlarmEditorEffect.ShowSaveUpdateLoadingIndicator -> showSaveUpdateLoading(effect)
            is AlarmEditorEffect.ShowToastMessage -> showToast(effect)
            is AlarmEditorEffect.FinishEditorActivity -> finishEditorActivity()
            is AlarmEditorEffect.ShowError -> showError(effect)
        }
    }

    /** Navigates to the Snooze Alarm Fragment with provided snooze settings. */
    private fun navigateToSnoozeFragment(effect: AlarmEditorEffect.NavigateToSnoozeAlarmFragment) {
        val action = AlarmEditorHomeFragmentDirections
            .actionAlarmEditorFragmentToSnoozeFragment(effect.snoozeSettings)
        fragment.findNavController().navigate(action)
    }

    /** Launches the Alarm Activity for mission preview with the given alarm ID and data. */
    private fun navigateToAlarmActivity(effect: AlarmEditorEffect.NavigateToAlarmActivityForMissionPreview) {
        val intent = Intent(fragment.requireContext(), AlarmActivity::class.java).apply {
            putExtra(AlarmKeys.ALARM_ID, effect.previewAlarmModel.id)
            putExtra(AlarmActivity.PREVIEW_MISSION_KEY, effect.previewAlarmModel)
        }
        fragment.startActivity(intent)
    }

    /** Displays the Mission Picker Bottom Sheet for mission selection. */
    private fun showMissionPickerBottomSheet(effect: AlarmEditorEffect.ShowMissionPickerBottomSheet) {
        MissionPickerBottomSheet.newInstance(
            existingMission = effect.existingMission,
            usedTypes = effect.usedMissions.map { it.type }.toSet(),
            itemHolderPosition = effect.position
        ).show(fragment.childFragmentManager, MissionPickerBottomSheet.TAG)
    }

    /** Displays the Bottom Sheet for the selected mission details. */
    private fun showSelectedMissionBottomSheet(effect: AlarmEditorEffect.ShowSelectedMissionBottomSheet) {
        effect.selectedMission.type
            .getMissionBottomSheet(effect.selectedMission, effect.position)
            .show(fragment.childFragmentManager, "MissionBottomSheet")
    }

    /** Updates the loading state of the save/update button via [uiBinder]. */
    private fun showSaveUpdateLoading(effect: AlarmEditorEffect.ShowSaveUpdateLoadingIndicator) {
        uiBinder.handleLoading(effect.isLoading)
    }

    /** Displays a toast message to the user. */
    private fun showToast(effect: AlarmEditorEffect.ShowToastMessage) {
        fragment.requireContext().showToast(effect.toastMessage)
    }

    /** Displays an error toast message to the user. */
    private fun showError(effect: AlarmEditorEffect.ShowError) {
        fragment.requireContext().showToast(effect.errorMessage)
    }

    /** Finishes the parent activity of the fragment. */
    private fun finishEditorActivity() {
        fragment.activity?.finish()
    }

    /** Launches the system ringtone picker for alarm sound selection. */
    private fun launchAlarmSoundPicker(effect: AlarmEditorEffect.LaunchAlarmSoundPicker) {
        ringtoneLauncher.launch(createRingtonePickerIntent(effect.existingAlarmSound))
    }

    /** Builds the intent for launching the system ringtone picker with an existing URI. */
    private fun createRingtonePickerIntent(existingUri: String): Intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, existingUri.toUri())
        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
    }
}