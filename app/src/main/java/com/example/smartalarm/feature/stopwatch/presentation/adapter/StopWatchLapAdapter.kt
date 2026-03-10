package com.example.smartalarm.feature.stopwatch.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.R
import com.example.smartalarm.core.utility.formatter.number.NumberFormatter
import com.example.smartalarm.databinding.StopWatchItemBinding
import com.example.smartalarm.feature.stopwatch.presentation.model.StopwatchLapUiModel
import com.example.smartalarm.feature.stopwatch.utility.StopwatchTimeFormatter


/**
 * RecyclerView adapter for displaying a list of stopwatch laps.
 *
 * This adapter efficiently handles lap updates by leveraging [DiffUtil.ItemCallback] to detect
 * changes between [StopwatchLapUiModel] items. It supports both full and partial updates to
 * optimize UI performance, especially for the latest lap entry.
 *
 * @param numberFormatter Utility to format lap numbers in a localized style.
 * @param stopwatchTimeFormatter Utility to format durations for display in a stopwatch-friendly format.
 *
 * ### Key Features:
 * - Uses [ListAdapter] to automatically handle updates to the lap list.
 * - Supports **partial updates**: only the elapsed time and end time of the latest lap are refreshed
 *   when they change, reducing unnecessary UI redraws.
 * - Each lap is represented by a [StopwatchLapUiModel] and bound to a [StopWatchItemBinding] view holder.
 *
 * ### DiffUtil Details:
 * - `areItemsTheSame`: Determines if two laps are the same based on `lapIndex`.
 * - `areContentsTheSame`: Determines if all fields of two laps are identical.
 * - `getChangePayload`: Returns a [StopwatchLapUiModel] payload if either `lapElapsedMillis` or
 *   `lapEndTimeMillis` changed, enabling partial UI updates.
 */
class StopWatchLapAdapter(
    private val numberFormatter: NumberFormatter,
    private val stopwatchTimeFormatter: StopwatchTimeFormatter
) : ListAdapter<StopwatchLapUiModel, StopWatchLapAdapter.StopWatchVH>(diffUtil) {

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<StopwatchLapUiModel>() {

            override fun areItemsTheSame(
                oldItem: StopwatchLapUiModel,
                newItem: StopwatchLapUiModel
            ): Boolean = oldItem.lapIndex == newItem.lapIndex

            override fun areContentsTheSame(
                oldItem: StopwatchLapUiModel,
                newItem: StopwatchLapUiModel
            ): Boolean = oldItem == newItem

            override fun getChangePayload(
                oldItem: StopwatchLapUiModel,
                newItem: StopwatchLapUiModel
            ): Any? {
                return if (oldItem.lapElapsedMillis != newItem.lapElapsedMillis ||
                    oldItem.lapEndTimeMillis != newItem.lapElapsedMillis
                ) newItem else null
            }
        }
    }

    class StopWatchVH(val binding: StopWatchItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopWatchVH {
        return StopWatchVH(
            StopWatchItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: StopWatchVH, position: Int) {
        val lap = getItem(position)
        holder.binding.apply {
            lapIndexTv.text = holder.itemView.context.getString(
                R.string.lap_index,
                numberFormatter.formatLocalizedNumber(lap.lapIndex.toLong(), false)
            )
            lapElapsedTimeTv.text = stopwatchTimeFormatter.formatMainDisplay(lap.lapElapsedMillis, true)
            lapEndTimeTv.text = stopwatchTimeFormatter.formatMainDisplay(lap.lapEndTimeMillis, true)
        }
    }

    override fun onBindViewHolder(holder: StopWatchVH, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position) // Full bind
        } else {
            // Partial update for the latest lap
            if (position == itemCount - 1) {
                payloads.forEach { payload ->
                    if (payload is StopwatchLapUiModel) {
                        holder.binding.apply {
                            lapElapsedTimeTv.text = stopwatchTimeFormatter.formatMainDisplay(payload.lapElapsedMillis, true)
                            lapEndTimeTv.text = stopwatchTimeFormatter.formatMainDisplay(payload.lapEndTimeMillis, true)
                        }
                    }
                }
            }
        }
    }
}