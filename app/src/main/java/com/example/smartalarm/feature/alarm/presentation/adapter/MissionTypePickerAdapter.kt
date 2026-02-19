package com.example.smartalarm.feature.alarm.presentation.adapter

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.databinding.BottomSheetMissionItemLayoutBinding
import com.example.smartalarm.feature.alarm.domain.model.MissionType

/**
 * Adapter for displaying a list of [MissionType] items in a bottom sheet,
 * allowing the user to select one for use in an alarm mission.
 *
 * It highlights the currently selected mission type and disables mission types
 * that are already used (unless currently selected).
 *
 * @param selectedMissionType The mission type currently selected (if any).
 * @param usedTypes A set of mission types that are already in use and should be disabled.
 * @param onMissionSelection Callback invoked when a selectable mission type is clicked.
 */
class MissionTypePickerAdapter(
    private val selectedMissionType: MissionType?,
    private val usedTypes: Set<MissionType>,
    private val onMissionSelection: (MissionType) -> Unit
) : ListAdapter<MissionType, MissionTypePickerAdapter.MissionViewHolder>(DIFF_CALLBACK) {

    /**
     * Inflates the [BottomSheetMissionItemLayoutBinding] and creates a [MissionViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val binding = BottomSheetMissionItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MissionViewHolder(binding)
    }

    /**
     * Binds the [MissionType] item at the given position to the [MissionViewHolder].
     */
    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder for displaying a single [MissionType] item in the list.
     *
     * Handles setting the icon, name, selection highlight, and disabled state
     * based on current selection and used types.
     *
     * @param binding ViewBinding for the mission item layout.
     */
    inner class MissionViewHolder(
        private val binding: BottomSheetMissionItemLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds a [MissionType] to the item view, applying:
         * - Label and icon
         * - Color tint based on mission type
         * - Selection highlight
         * - Disable state if already used
         *
         * @param missionType The mission type being bound to this view.
         */
        fun bind(missionType: MissionType) {

            val context = binding.root.context
            val isSelected = missionType == selectedMissionType
            val isAlreadyUsed = missionType in usedTypes && !isSelected
            val missionIconColor = ContextCompat.getColor(context, missionType.getColorRes())

            binding.apply {
                missionName.text = MissionType.getLocalizedName(context, missionType)
                missionIcon.setImageResource(missionType.getIconRes())
                missionIcon.setColorFilter(missionIconColor, PorterDuff.Mode.SRC_IN)
                tickIcon.visibility = if (isSelected) View.VISIBLE else View.GONE

                root.apply {
                    isEnabled = !isAlreadyUsed
                    alpha = if (isAlreadyUsed) 0.4f else 1.0f
                    setOnClickListener {
                        if (!isAlreadyUsed) onMissionSelection(missionType)
                    }
                }
            }
        }

    }

    companion object {
        /**
         * DiffUtil callback for efficiently detecting changes in the list of [MissionType]s.
         *
         * Items are compared by class type (assuming uniqueness), and full equality for content comparison.
         */
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MissionType>() {
            override fun areItemsTheSame(oldItem: MissionType, newItem: MissionType): Boolean {
                return oldItem::class == newItem::class
            }

            override fun areContentsTheSame(oldItem: MissionType, newItem: MissionType): Boolean {
                return oldItem == newItem
            }
        }
    }
}
