package com.example.smartalarm.feature.alarm.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.databinding.ItemMissionPlaceholderBinding
import com.example.smartalarm.databinding.ItemMissionSelectedBinding
import com.example.smartalarm.feature.alarm.domain.model.Mission
import com.example.smartalarm.feature.alarm.domain.model.MissionItem


/**
 * RecyclerView adapter to display a list of missions with support for mission items and placeholders.
 *
 * @property onMissionItemClick Lambda invoked when a mission item is clicked, passing the clicked item's position.
 * @property onMissionItemPlaceholderClick Lambda invoked when a placeholder item is clicked.
 * @property onRemoveMissionClick Lambda invoked when the remove icon of a mission item is clicked, passing the item's position.
 */
class AlarmMissionAdapter(
    private val onMissionItemPlaceholderClick: (Int) -> Unit,
    private val onMissionItemClick: (Int, Mission) -> Unit,
    private val onRemoveMissionClick: (Int) -> Unit
) : ListAdapter<MissionItem, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {

        /** View type constant for mission items */
        private const val TYPE_MISSION = 0

        /** View type constant for placeholder items */
        private const val TYPE_PLACEHOLDER = 1

        /** DiffUtil callback to efficiently update mission list changes */
        private val DiffCallback = object : DiffUtil.ItemCallback<MissionItem>() {

            /**
             * Checks whether two MissionItem objects represent the same item.
             * Missions are considered the same if their underlying missions match,
             * placeholders are considered the same if their IDs match.
             */
            override fun areItemsTheSame(oldItem: MissionItem, newItem: MissionItem): Boolean {
                return when {
                    oldItem is MissionItem.MissionData && newItem is MissionItem.MissionData -> oldItem.mission == newItem.mission
                    oldItem is MissionItem.Placeholder && newItem is MissionItem.Placeholder -> oldItem.id == newItem.id
                    else -> false
                }
            }

            /**
             * Checks whether the contents of two MissionItem objects are the same.
             * This compares all properties to detect content changes.
             */
            override fun areContentsTheSame(oldItem: MissionItem, newItem: MissionItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    /**
     * Returns the view type of the item at the given position.
     * Returns [TYPE_MISSION] for mission data items,
     * and [TYPE_PLACEHOLDER] for placeholder items.
     */
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MissionItem.MissionData -> TYPE_MISSION
            is MissionItem.Placeholder -> TYPE_PLACEHOLDER
        }
    }

    /**
     * Creates appropriate ViewHolder depending on the view type.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The type of the new view.
     * @return A ViewHolder of the appropriate type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_MISSION) {
            val binding = ItemMissionSelectedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MissionViewHolder(binding)
        } else {
            val binding = ItemMissionPlaceholderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            PlaceholderViewHolder(binding)
        }
    }

    /**
     * Binds the ViewHolder with the data at the given position.
     *
     * @param holder The ViewHolder to bind.
     * @param position The position of the item to bind.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is MissionItem.MissionData -> (holder as MissionViewHolder).bind(item, onMissionItemClick, onRemoveMissionClick)
            is MissionItem.Placeholder -> (holder as PlaceholderViewHolder).bind(onMissionItemPlaceholderClick)
        }
    }

    /**
     * ViewHolder class for mission data items.
     *
     * @property binding The ViewBinding for mission item layout.
     */
    class MissionViewHolder(private val binding: ItemMissionSelectedBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds a mission item to the view.
         *
         * @param item The mission data to bind.
         * @param onMissionClick Lambda called when mission item is clicked.
         * @param onRemoveClick Lambda called when remove icon is clicked.
         */
        fun bind(item: MissionItem.MissionData, onMissionClick: (Int, Mission) -> Unit, onRemoveClick: (Int) -> Unit) {
            binding.apply {

                selectedImageIcon.setImageResource(item.mission.iconResId)

                val color = ContextCompat.getColor(itemView.context, item.mission.type.getColorRes())
                selectedMissionContainer.setBackgroundColor(color)

                root.setOnClickListener {
                    onMissionClick(bindingAdapterPosition,item.mission)
                }

                removeMissionIcon.setOnClickListener {
                    onRemoveClick(bindingAdapterPosition)
                }
            }
        }
    }

    /**
     * ViewHolder class for placeholder items.
     *
     * @property binding The ViewBinding for placeholder item layout.
     */
    class PlaceholderViewHolder(private val binding: ItemMissionPlaceholderBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the placeholder view with click listener.
         *
         * @param onPlaceholderClick Lambda called when placeholder is clicked.
         */
        fun bind(onPlaceholderClick: (Int) -> Unit) {
            binding.root.setOnClickListener { onPlaceholderClick(bindingAdapterPosition) }
        }
    }


}