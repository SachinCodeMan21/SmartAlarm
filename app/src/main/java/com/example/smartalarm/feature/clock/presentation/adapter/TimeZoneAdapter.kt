package com.example.smartalarm.feature.clock.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.databinding.ItemTimezoneLayoutBinding
import com.example.smartalarm.feature.clock.domain.model.PlaceModel


/**
 * Adapter for displaying a list of [PlaceModel] items representing different time zones.
 *
 * Uses [ListAdapter] with [DiffUtil] for efficient list updates.
 *
 * @constructor Creates a TimeZoneAdapter instance.
 */
class TimeZoneAdapter : ListAdapter<PlaceModel, TimeZoneAdapter.ViewHolder>(DiffCallback) {


    companion object {

        /**
         * DiffUtil callback to optimize list changes by comparing item IDs and contents.
         */
        private val DiffCallback = object : DiffUtil.ItemCallback<PlaceModel>() {

            /**
             * Checks whether two items represent the same place by comparing their IDs.
             */
            override fun areItemsTheSame(oldItem: PlaceModel, newItem: PlaceModel): Boolean {
                return oldItem.id == newItem.id
            }

            /**
             * Checks whether the content of two items is the same.
             */
            override fun areContentsTheSame(oldItem: PlaceModel, newItem: PlaceModel): Boolean {
                return oldItem == newItem
            }
        }

    }

    /**
     * ViewHolder for binding the global timezone item layout.
     *
     * @property binding The view binding for the item layout.
     */
    class ViewHolder(val binding: ItemTimezoneLayoutBinding) : RecyclerView.ViewHolder(binding.root)



    /**
     * Inflates the item layout and creates a [ViewHolder].
     *
     * @param parent The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new [ViewHolder] instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTimezoneLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }



    /**
     * Binds data from a [PlaceModel] to the item views.
     *
     * @param holder The [ViewHolder] to bind data to.
     * @param position The position of the item in the list.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = getItem(position)
        holder.binding.apply {
            name.text = place.primaryName
            clockTimeTv.text = place.currentTime
        }

    }

}
