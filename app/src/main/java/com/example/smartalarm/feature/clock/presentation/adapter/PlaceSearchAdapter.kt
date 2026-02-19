package com.example.smartalarm.feature.clock.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.databinding.ItemTimezoneLayoutBinding
import com.example.smartalarm.databinding.SearchItemLayoutBinding
import com.example.smartalarm.feature.clock.domain.model.PlaceModel

/**
 * [PlaceSearchAdapter] is a [RecyclerView.Adapter] implementation for displaying a list of [PlaceModel]s
 * in a RecyclerView, typically used for place search results.
 *
 * This adapter uses a [ListAdapter] with a [DiffUtil.ItemCallback] to efficiently manage updates.
 *
 * @param onPlaceSelected A callback triggered when a user taps on a place item.
 */
class PlaceSearchAdapter(
    private val onPlaceSelected: (PlaceModel) -> Unit
) : ListAdapter<PlaceModel, PlaceSearchAdapter.ViewHolder>(diffUtil) {

    companion object {
        /**
         * [DiffUtil.ItemCallback] implementation for efficiently computing differences between lists of [PlaceModel].
         */
        private val diffUtil = object : DiffUtil.ItemCallback<PlaceModel>() {
            override fun areItemsTheSame(oldItem: PlaceModel, newItem: PlaceModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PlaceModel, newItem: PlaceModel): Boolean =
                oldItem == newItem
        }
    }

    /**
     * [ViewHolder] for binding [ItemTimezoneLayoutBinding] to each item view.
     *
     * @param binding The view binding instance associated with the item layout.
     */
    class ViewHolder(val binding: SearchItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * Creates a new [ViewHolder] for the RecyclerView item.
     *
     * @param parent The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new View.
     * @return A new instance of [ViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    /**
     * Binds a [PlaceModel] to the ViewHolder.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the adapter.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = getItem(position)
        with(holder.binding) {
            name.text = place.fullName
            root.setOnClickListener { onPlaceSelected(place) }
        }
    }
}
