package com.example.smartalarm.feature.clock.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.databinding.ItemTimezoneLayoutBinding
import com.example.smartalarm.feature.clock.presentation.model.PlaceUiModel


/**
 * Adapter for displaying a list of [PlaceUiModel] items representing different time zones.
 *
 * Uses [ListAdapter] with [DiffUtil] for efficient updates.
 */
class WorldTimeZoneAdapter : ListAdapter<PlaceUiModel, WorldTimeZoneAdapter.ViewHolder>(DiffCallback) {

    companion object {
        /**
         * DiffUtil callback to optimize list updates by comparing item IDs and contents.
         */
        private val DiffCallback = object : DiffUtil.ItemCallback<PlaceUiModel>() {

            /** Checks whether two items represent the same place by comparing their IDs. */
            override fun areItemsTheSame(oldItem: PlaceUiModel, newItem: PlaceUiModel): Boolean {
                return oldItem.id == newItem.id
            }

            /** Checks whether the content of two items is the same. */
            override fun areContentsTheSame(oldItem: PlaceUiModel, newItem: PlaceUiModel): Boolean {
                return oldItem == newItem
            }
        }
    }

    /**
     * ViewHolder for binding the timezone item layout.
     *
     * @property binding The view binding for the item layout.
     */
    class ViewHolder(val binding: ItemTimezoneLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * Inflates the item layout and creates a [ViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTimezoneLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    /**
     * Binds data from a [PlaceUiModel] to the item views.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = getItem(position)
        holder.binding.apply {
            name.text = place.name
            clockTimeTv.text = place.currentTime
            timeDiffTv.text = place.timeDifference
        }
    }
}