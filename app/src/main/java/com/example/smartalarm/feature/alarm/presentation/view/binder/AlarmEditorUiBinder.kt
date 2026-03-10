package com.example.smartalarm.feature.alarm.presentation.view.binder

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartalarm.R
import com.example.smartalarm.databinding.FragmentAlarmEditorBinding
import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import com.example.smartalarm.feature.alarm.presentation.adapter.AlarmMissionAdapter
import com.example.smartalarm.feature.alarm.presentation.event.editor.AlarmEditorUserEvent
import com.example.smartalarm.feature.alarm.presentation.model.editor.AlarmEditorHomeUiModel
import com.example.smartalarm.feature.alarm.presentation.viewmodel.editor.AlarmEditorViewModel
import com.example.smartalarm.feature.alarm.utility.onProgressChangedListener
import com.example.smartalarm.feature.alarm.utility.setBackgroundDrawableIfDifferent
import com.example.smartalarm.feature.alarm.utility.setCheckedIfDifferent
import com.example.smartalarm.feature.alarm.utility.setProgressIfDifferent
import com.example.smartalarm.feature.alarm.utility.setTextColorIfDifferent
import com.example.smartalarm.feature.alarm.utility.setTextIfDifferent
import com.example.smartalarm.feature.alarm.utility.setValueIfDifferent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Binds and manages the UI of the Alarm Editor Fragment, connecting [FragmentAlarmEditorBinding]
 * with [AlarmEditorViewModel] and handling all user interactions.
 *
 * <p>This class implements the <b>DefaultLifecycleObserver</b> interface and registers itself
 * with the provided [lifecycleOwner] to ensure lifecycle-aware setup and teardown.</p>
 *
 * <p><b>Responsibilities:</b></p>
 * 1. **UI Setup** – Initializes static views such as time pickers, weekday selectors, and missions RecyclerView.
 * 2. **User Interaction Handling** – Delegates all user events (text changes, time changes, toggle switches,
 *    button clicks) to the [onUserEvent] callback.
 * 3. **State Rendering** – Observes [AlarmEditorViewModel.uiState] and updates UI elements efficiently,
 *    minimizing unnecessary redraws.
 * 4. **Memory Safety** – Cleans up references, including detaching the RecyclerView adapter in [onDestroy],
 *    to prevent memory leaks.
 *
 * @param binding The view binding for [FragmentAlarmEditorBinding].
 * @param viewModel The shared [AlarmEditorViewModel] that provides UI state and handles events.
 * @param lifecycleOwner The lifecycle owner to tie observers and cleanup to.
 * @param onUserEvent Lambda invoked for all user-generated events, sending [AlarmEditorUserEvent] objects.
 */
class AlarmEditorUiBinder(
    private val binding: FragmentAlarmEditorBinding,
    private val viewModel: AlarmEditorViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onUserEvent: (AlarmEditorUserEvent) -> Unit
) : DefaultLifecycleObserver {

    /** References for the weekday selection views (Mon–Sun) */
    private lateinit var weekdayViews: List<TextView>

    /** Adapter managing the mission items RecyclerView */
    private lateinit var missionAdapter: AlarmMissionAdapter

    /** Flags to prevent initial unwanted triggers for Switch and SeekBar listeners */
    private var isInitialSwitchTriggerFlag = true
    private var isInitialSeekBarTriggerFlag = true

    init {
        // Register this binder with the lifecycle to ensure proper setup and teardown
        lifecycleOwner.lifecycle.addObserver(this)
    }

    // ---------------------------------------------------------------------
    // Lifecycle Callbacks
    // ---------------------------------------------------------------------

    /**
     * Called when the lifecycle reaches the ON_CREATE state.
     * Sets up the static UI, registers listeners, and observes the ViewModel state.
     */
    override fun onCreate(owner: LifecycleOwner) {
        setupStaticUI()
        setupListeners()
        observeState()
    }

    /**
     * Called when the lifecycle is destroyed.
     * Detaches the RecyclerView adapter to prevent memory leaks.
     */
    override fun onDestroy(owner: LifecycleOwner) {
        binding.missionBlock.missionRecyclerView.adapter = null
        super.onDestroy(owner)
    }

    // ---------------------------------------------------------------------
    // UI Setup
    // ---------------------------------------------------------------------

    /**
     * Initializes static UI components such as:
     * - Weekday selection views
     * - Time picker formatters
     * - Mission RecyclerView adapter
     */
    private fun setupStaticUI() = with(binding) {
        // Weekday views
        weekdayViews = with(weekdaysBlock) {
            listOf(day1, day2, day3, day4, day5, day6, day7)
        }

        // Time picker formatting
        timePickerBlock.apply {
            hoursPicker.setFormatter { viewModel.getLocalizedNumber(it) }
            minutePicker.setFormatter { viewModel.getLocalizedNumber(it) }
            amPmPicker.apply {
                val amPmList = root.resources.getStringArray(R.array.meri_diem_list)
                minValue = 0
                maxValue = amPmList.lastIndex
                displayedValues = amPmList
            }
        }

        // Mission adapter setup
        missionAdapter = AlarmMissionAdapter(
            onMissionItemPlaceholderClick = {
                removeLabelFocus()
                onUserEvent(AlarmEditorUserEvent.MissionEvent.PlaceholderClicked(it))
            },
            onMissionItemClick = { position, mission ->
                removeLabelFocus()
                onUserEvent(AlarmEditorUserEvent.MissionEvent.ItemClicked(position, mission))
            },
            onRemoveMissionClick = {
                removeLabelFocus()
                onUserEvent(AlarmEditorUserEvent.MissionEvent.RemoveClicked(it))
            }
        )

        missionBlock.missionRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = missionAdapter
        }
    }

    /**
     * Registers listeners for:
     * - Alarm label text changes
     * - Time picker value changes
     * - Weekday selection and daily checkbox
     * - Alarm sound volume and vibration toggle
     * - Snooze edit button
     */
    private fun setupListeners() = with(binding) {
        // Alarm label
        alarmLabelET.doAfterTextChanged {
            onUserEvent(AlarmEditorUserEvent.AlarmEvent.LabelChanged(it?.trim().toString()))
        }

        // Time pickers
        timePickerBlock.apply {
            val notifyTimeChanged = {
                removeLabelFocus()
                onUserEvent(
                    AlarmEditorUserEvent.AlarmEvent.TimeChanged(
                        hoursPicker.value, minutePicker.value, amPmPicker.value
                    )
                )
            }
            listOf(hoursPicker, minutePicker, amPmPicker).forEach { picker ->
                picker.setOnValueChangedListener { _, _, _ -> notifyTimeChanged() }
            }
        }

        // Weekday selection
        weekdaysBlock.apply {
            isDailyCheckBox.setOnCheckedChangeListener { _, isChecked ->
                removeLabelFocus()
                onUserEvent(AlarmEditorUserEvent.AlarmEvent.IsDailyChanged(isChecked))
            }
            weekdayViews.forEachIndexed { index, dayView ->
                dayView.setOnClickListener {
                    removeLabelFocus()
                    onUserEvent(AlarmEditorUserEvent.AlarmEvent.DayToggled(index))
                }
            }
        }

        // Sound block
        soundBlock.apply {
            alarmVolumeSeekBar.onProgressChangedListener { progress, _ ->
                if (!isInitialSeekBarTriggerFlag) {
                    onUserEvent(AlarmEditorUserEvent.SoundEvent.VolumeChanged(progress))
                }
                isInitialSeekBarTriggerFlag = false
            }

            vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (!isInitialSwitchTriggerFlag) {
                    onUserEvent(AlarmEditorUserEvent.SoundEvent.VibrationToggled(isChecked))
                }
                isInitialSwitchTriggerFlag = false
            }

            alarmSoundTv.setOnClickListener {
                removeLabelFocus()
                onUserEvent(AlarmEditorUserEvent.SoundEvent.LaunchPicker)
            }
        }

        // Snooze block
        snoozeBlock.alarmSnoozeTv.setOnClickListener {
            removeLabelFocus()
            onUserEvent(AlarmEditorUserEvent.ActionEvent.EditSnooze)
        }
    }

    // ---------------------------------------------------------------------
    // State Rendering
    // ---------------------------------------------------------------------

    /**
     * Observes [AlarmEditorViewModel.uiState] and triggers UI rendering
     * whenever the state updates. Uses lifecycle-aware collection.
     */
    private fun observeState() {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    render(state)
                }
            }
        }
    }

    /**
     * Renders the UI based on [AlarmEditorHomeUiModel].
     * Efficiently updates only the fields that have changed.
     */
    private fun render(state: AlarmEditorHomeUiModel) = with(binding) {
        alarmLabelET.setTextIfDifferent(state.label)

        timePickerBlock.apply {
            hoursPicker.setValueIfDifferent(state.hour)
            minutePicker.setValueIfDifferent(state.minute)
            amPmPicker.setValueIfDifferent(state.amPm)
        }

        weekdaysBlock.isDailyCheckBox.setCheckedIfDifferent(state.isDailyAlarm)
        updateWeekdaysSelection(state.selectedDays)

        missionBlock.apply {
            missionAdapter.submitList(state.missionItemList)
            missionCount.text = state.formattedMissionSlotText
        }

        soundBlock.apply {
            alarmVolumeSeekBar.setProgressIfDifferent(state.volume)
            vibrateSwitch.setCheckedIfDifferent(state.isVibrateEnabled)
            alarmSoundTv.setTextIfDifferent(state.alarmSoundTitle)
        }

        snoozeBlock.alarmSnoozeTv.setTextIfDifferent(state.formattedSnoozedText)
    }

    /**
     * Updates the weekday selector views based on [selectedDays].
     *
     * @param selectedDays Set of days that should be visually marked as selected.
     */
    private fun updateWeekdaysSelection(selectedDays: Set<DayOfWeek>) {
        weekdayViews.forEachIndexed { i, view ->
            val day = DayOfWeek.getDayAtPositionOrNull(i) ?: return@forEachIndexed
            val isSelected = day in selectedDays

            val bgRes = if (isSelected) R.drawable.selected_circular_background
            else R.drawable.unselected_circular_background
            val textColor = if (isSelected) android.R.color.white
            else android.R.color.darker_gray

            view.setBackgroundDrawableIfDifferent(bgRes)
            view.setTextColorIfDifferent(textColor)
        }
    }


    // ---------------------------------------------------------------------
    // Public Helpers
    // ---------------------------------------------------------------------

    /**
     * Removes focus from the alarm label EditText, if it is currently focused.
     */
    fun removeLabelFocus() {
        if (binding.alarmLabelET.isFocused) binding.alarmLabelET.clearFocus()
    }

    /**
     * Shows or hides the save progress bar and updates the visibility
     * of the save/update button based on [isLoading].
     *
     * @param isLoading True if saving is in progress, false otherwise.
     */
    fun handleLoading(isLoading: Boolean) {
        binding.saveProgressBar.isVisible = isLoading
        binding.saveOrUpdateAlarmBtn.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
    }
}