package com.example.smartalarm.feature.alarm.presentation.adapter

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.R
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel
import com.example.smartalarm.databinding.AlarmItemLayoutBinding
import com.example.smartalarm.feature.alarm.domain.enums.DayOfWeek
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.domain.model.MissionType
import com.example.smartalarm.feature.alarm.presentation.model.home.AlarmUiModel
import com.example.smartalarm.feature.alarm.utility.getDrawableOrFallback

/**
 * Adapter for binding alarm items in a [RecyclerView], handling alarm data, switch states, and user interactions.
 *
 * - **Efficient Data Binding**: Uses [DiffUtil] to optimize updates by only refreshing the items that have changed, improving performance.
 * - **Selective UI Updates**: By leveraging payloads, only the relevant parts of the item (e.g., switch state) are updated, preventing unnecessary UI re-renders and enhancing smoothness.
 * - **Minimized Redundant Listeners**: Listeners are set up only once during initial binding, ensuring proper handling of interactions without redundant re-bindings.
 *
 * @param onAlarmItemClick A lambda function to handle clicks on an alarm item.
 * @param onAlarmSwitchToggle A lambda function to handle the switch toggle state change for alarms.
 */
class AlarmAdapter(
    private val onAlarmItemClick: (alarmId: Int) -> Unit,
    private val onAlarmSwitchToggle: (alarmId: Int, isEnabled: Boolean) -> Unit
) : ListAdapter<AlarmUiModel, AlarmAdapter.AlarmViewHolder>(AlarmDiffCallback())
{

    companion object {

        /**
         * DiffUtil callback for comparing and checking if the alarm items are the same
         * and if their contents are the same.
         */
        class AlarmDiffCallback : DiffUtil.ItemCallback<AlarmUiModel>() {

            /**
             * Checks whether two items represent the same alarm by comparing their IDs.
             */
            override fun areItemsTheSame(oldItem: AlarmUiModel, newItem: AlarmUiModel) = oldItem.id == newItem.id

            /**
             * Checks whether the contents of two alarm items are the same.
             */
            override fun areContentsTheSame(oldItem: AlarmUiModel, newItem: AlarmUiModel) = oldItem == newItem

            /**
             * Returns a payload (a small data object) if only some content of the item has changed.
             * This is used to optimize item updates.
             */
            override fun getChangePayload(oldItem: AlarmUiModel, newItem: AlarmUiModel): Any? {
                return if (oldItem.isEnabled != newItem.isEnabled) {
                    // Return just the switch state (Boolean) as the payload
                    newItem.isEnabled
                } else {
                    null // No payload if there’s no change in specific parts
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = AlarmItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding, onAlarmItemClick, onAlarmSwitchToggle)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        Log.d("TAG","onBindViewHolder item : $position")
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int, payloads: MutableList<Any>) {
        val item = getItem(position)
        holder.bind(item, payloads)
    }

    class AlarmViewHolder(
        private val binding: AlarmItemLayoutBinding,
        private val onAlarmItemClick: (alarmId: Int) -> Unit,
        private val onAlarmSwitchToggle: (alarmId: Int, isEnabled: Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the [alarm] data to the view, using payloads to optimize UI updates and prevent unnecessary redraws.
         *
         * - **Efficient Updates**: Uses the [payloads] parameter to selectively update only the changed parts of the item (e.g., switch state) without rebinding the entire view.
         * - **Switch Handling**: The switch state is updated based on the payload if present; otherwise, it uses [AlarmModel.isEnabled]. The listener is always attached, ensuring toggles work reliably.
         * - **Avoid Redundant Listeners**: Removes the switch listener before programmatically updating its state to avoid triggering callbacks, then re-attaches it.
         * - **Minimize UI Flicker**: Only updates other UI components (time, mission icons, weekdays, root click listener) when [payloads] is empty, reducing unnecessary redraws.
         *
         * @param alarm The [AlarmUiModel] containing the alarm data to bind.
         * @param payloads A list of changes. If empty, a full bind occurs. If it contains a Boolean, only the switch state is updated.
         */
        fun bind(alarm: AlarmUiModel, payloads: List<Any> = emptyList()) = with(binding) {

            // Remove listener first to prevent triggering it when setting isChecked
            alarmSwitch.setOnCheckedChangeListener(null)

            // Update the switch state: payload Boolean overrides alarm.isEnabled if present
            val newSwitchState = payloads.firstOrNull { it is Boolean } as? Boolean ?: alarm.isEnabled
            alarmSwitch.isChecked = newSwitchState

            // Always attach the listener
            alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
                onAlarmSwitchToggle(alarm.id, isChecked)
            }

            // Full bind for other views only if payloads is empty
            if (payloads.isEmpty()) {
                setWeekdaysStyled(alarm.selectedDays)
                alarmTimeTv.text = alarm.formattedAlarmTime
                setUpMissionIcon(alarm.missionIconResId)
                missionText.text = getFormattedMissionNames(alarm.alarmMissions, isLandscape())
                root.setOnClickListener { onAlarmItemClick(alarm.id) }
            }
        }


        /**
         * Styles the weekdays text view to highlight selected days with color.
         *
         * - **Improved Readability**: Highlights selected days with different colors for easy visual identification.
         * - **Efficient**: Uses `SpannableStringBuilder` to apply styles only where needed, minimizing overhead.
         * - **Edge Case Handling**: Displays "One-time" if no days are selected, providing clear context.
         *
         * @param selectedDays Set of selected [DayOfWeek] values to be styled.
         */
        private fun setWeekdaysStyled(selectedDays: Set<DayOfWeek>) = with(binding.weekdaysTv) {

            when {

                selectedDays.isEmpty() -> {
                    text = context.getString(R.string.one_time)
                    return
                }

                else -> {

                    val weekdayAbbreviations = context.resources.getStringArray(R.array.alarm_weekday)
                    val spannable = SpannableStringBuilder()

                    DayOfWeek.entries.forEachIndexed { index, dayOfWeek ->

                        val day = weekdayAbbreviations.getOrNull(index) ?: return@forEachIndexed

                        val isSelected = selectedDays.contains(dayOfWeek)
                        val color = ContextCompat.getColor(
                            context,
                            if (isSelected) R.color.selected_weekdays_color else R.color.unselected_weekdays_color
                        )

                        val start = spannable.length
                        spannable.append(day)
                        spannable.setSpan(
                            ForegroundColorSpan(color),
                            start,
                            start + day.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        if (index < DayOfWeek.entries.lastIndex) {
                            spannable.append(" ")
                        }

                    }

                    text = spannable

                }
            }
        }

        /**
         * Sets the mission icon drawable with a tint color.
         *
         * - Falls back to a default icon if [iconResId] is null.
         * - Applies a purple tint to the icon.
         *
         * @param iconResId The resource ID of the mission icon, or null.
         */
        private fun setUpMissionIcon(iconResId: Int?) = with(binding.missionIconImg) {
            val context = this.context
            val drawable = context.getDrawableOrFallback(iconResId, R.drawable.ic_close)
            setImageDrawable(drawable)
        }

        /**
         * Formats the mission names into a readable string based on the landscape mode.
         *
         * - Filters out null missions and returns "None" if there are no selected missions.
         * - If in landscape mode, all mission names are joined by a comma.
         * - In portrait mode, only the first two mission names are shown, followed by a count of additional missions.
         *
         * @param missions The list of [Mission] objects, which may contain null values.
         * @param isLandscape A boolean indicating whether the device is in landscape orientation.
         * @return A formatted string containing the mission names.
         */

        fun getFormattedMissionNames(missions: List<Mission?>, isLandscape: Boolean): String {

            val selectedMissions = missions.filterNotNull()

            if (selectedMissions.isEmpty()) return binding.root.context.getString(R.string.none)

            val missionNames = selectedMissions.map { MissionType.getLocalizedName(binding.root.context,it.type) }

            return if (isLandscape) {
                missionNames.joinToString(", ")
            } else {
                val limit = 2
                if (missionNames.size > limit) {
                    val shown = missionNames.take(limit).joinToString(", ")
                    "$shown +${missionNames.size - limit}"
                } else {
                    missionNames.joinToString(", ")
                }
            }
        }

        /**
         * Checks if the device is in landscape orientation.
         *
         * @return `true` if the device is in landscape mode, `false` otherwise.
         */
        fun isLandscape(): Boolean {
            val orientation = binding.root.resources.configuration.orientation
            return orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
        }

    }

}
