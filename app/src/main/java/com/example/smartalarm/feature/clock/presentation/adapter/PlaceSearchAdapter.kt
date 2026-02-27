package com.example.smartalarm.feature.clock.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.databinding.SearchItemLayoutBinding
import com.example.smartalarm.feature.clock.presentation.model.PlaceUiModel

/**
 * Adapter for displaying a list of [PlaceUiModel] items in a RecyclerView, typically for search results.
 *
 * Uses [ListAdapter] with [DiffUtil] for efficient updates.
 *
 * @param onPlaceSelected Callback invoked when a user selects a place.
 */
class PlaceSearchAdapter(
    private val onPlaceSelected: (Long) -> Unit
) : ListAdapter<PlaceUiModel, PlaceSearchAdapter.ViewHolder>(diffUtil) {

    companion object {

        /**
         * DiffUtil callback for efficiently computing differences between lists of [PlaceUiModel].
         */
        private val diffUtil = object : DiffUtil.ItemCallback<PlaceUiModel>() {

            override fun areItemsTheSame(oldItem: PlaceUiModel, newItem: PlaceUiModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PlaceUiModel, newItem: PlaceUiModel): Boolean =
                oldItem == newItem
        }
    }

    /**
     * ViewHolder for binding [SearchItemLayoutBinding] to each item view.
     *
     * @param binding The view binding instance associated with the item layout.
     */
    class ViewHolder(val binding: SearchItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * Inflates the item layout and creates a [ViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    /**
     * Binds a [PlaceUiModel] to the ViewHolder.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = getItem(position)
        with(holder.binding) {
            name.text = place.name
            root.setOnClickListener { onPlaceSelected(place.id) }
        }
    }
}