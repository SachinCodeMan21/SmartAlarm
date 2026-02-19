package com.example.smartalarm.feature.stopwatch.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.databinding.StopWatchItemBinding
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchLapUiModel


/**
 * A [ListAdapter] implementation for displaying a list of stopwatch laps in the [RecyclerView].
 *
 * This adapter uses [StopwatchLapUiModel] as the data model and leverages [DiffUtil] to efficiently
 * detect changes. It supports **partial updates** of the latest lap using payloads, so only the
 * elapsed time and end time of the most recent lap are updated without rebinding the entire item.
 */
class StopWatchLapAdapter : ListAdapter<StopwatchLapUiModel, StopWatchLapAdapter.StopWatchVH>(diffUtil) {

    /**
     * ViewHolder for displaying a single stopwatch lap item.
     *
     * @param binding The view binding for the lap item layout.
     */
    class StopWatchVH(val binding: StopWatchItemBinding) : RecyclerView.ViewHolder(binding.root)


    companion object {

        /**
         * A [DiffUtil.ItemCallback] implementation to efficiently determine changes in the
         * [StopwatchLapUiModel] list.
         *
         * - [DiffUtil.ItemCallback.areItemsTheSame]: checks if two items represent the same lap based on `formattedLapIndex`.
         * - [DiffUtil.ItemCallback.areContentsTheSame]: checks if the entire content of two items is identical.
         * - [DiffUtil.ItemCallback.getChangePayload]: returns the new [StopwatchLapUiModel] object if either the
         *   `formattedLapElapsedTime` or `formattedLapEndTime` changed, enabling **partial updates**.
         */
        private val diffUtil = object : DiffUtil.ItemCallback<StopwatchLapUiModel>() {

            override fun areItemsTheSame(
                oldItem: StopwatchLapUiModel,
                newItem: StopwatchLapUiModel
            ): Boolean = oldItem.formattedLapIndex == newItem.formattedLapIndex

            override fun areContentsTheSame(
                oldItem: StopwatchLapUiModel,
                newItem: StopwatchLapUiModel
            ): Boolean = oldItem == newItem

            override fun getChangePayload(
                oldItem: StopwatchLapUiModel,
                newItem: StopwatchLapUiModel
            ): Any? {
                // Return the new object if elapsedTime or endTime changed
                return if (oldItem.formattedLapElapsedTime != newItem.formattedLapElapsedTime ||
                    oldItem.formattedLapEndTime != newItem.formattedLapEndTime
                ) newItem else null
            }
        }
    }

    /**
     * Inflates the lap item layout and returns a new [StopWatchVH].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopWatchVH {
        return StopWatchVH(
            StopWatchItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    /**
     * Binds the data from [StopwatchLapUiModel] to all UI views in the ViewHolder.
     * This is called for full item binds.
     */
    override fun onBindViewHolder(holder: StopWatchVH, position: Int) {
        val lap = getItem(position)
        holder.binding.apply {
            lapIndexTv.text = lap.formattedLapIndex
            lapElapsedTimeTv.text = lap.formattedLapElapsedTime
            lapEndTimeTv.text = lap.formattedLapEndTime
        }
    }

    /**
     * Handles partial updates using payloads.
     * Only updates the elapsed time and end time for the **latest lap** (last item).
     *
     * @param payloads Contains the new [StopwatchLapUiModel] if either elapsed time or end time changed.
     */
    override fun onBindViewHolder(holder: StopWatchVH, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            // Full bind
            onBindViewHolder(holder, position)
        } else {
            // Partial update for the latest lap
            if (position == itemCount - 1) {
                payloads.forEach { payload ->
                    if (payload is StopwatchLapUiModel) {
                        holder.binding.apply {
                            lapElapsedTimeTv.text = payload.formattedLapElapsedTime
                            lapEndTimeTv.text = payload.formattedLapEndTime
                        }
                    }
                }
            }
        }
    }
}
