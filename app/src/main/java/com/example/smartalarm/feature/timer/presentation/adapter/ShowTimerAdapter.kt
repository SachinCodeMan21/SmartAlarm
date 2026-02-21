package com.example.smartalarm.feature.timer.presentation.adapter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.R
import com.example.smartalarm.databinding.TimerItemLayoutBinding
import com.example.smartalarm.feature.timer.data.mapper.TimerMapper
import com.example.smartalarm.feature.timer.presentation.event.ShowTimerEvent
import com.example.smartalarm.feature.timer.presentation.model.ShowTimerUiModel
import com.example.smartalarm.feature.timer.utility.toFormattedTimerTime
import com.google.android.material.color.MaterialColors

/**
 * [ShowTimerAdapter] is a [ListAdapter] that displays a list of timers using [ShowTimerUiModel].
 *
 * It provides UI binding logic for each timer item including visual updates,
 * progress indication, and button interactions.
 *
 * @param onTimerEvent Callback to handle timer-related UI actions like start, pause, snooze, etc.
 */
class ShowTimerAdapter(
    private val onTimerEvent: (ShowTimerEvent) -> Unit
) : ListAdapter<ShowTimerUiModel, ShowTimerAdapter.TimerViewHolder>(DIFF_CALLBACK) {

    // ----------------------------------------------------
    // Payloads
    // ----------------------------------------------------
    sealed interface TimerPayload {
        data object TimeAndProgress : TimerPayload
        data object RunningState : TimerPayload
        data object CardColor : TimerPayload
    }

    companion object {

        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<ShowTimerUiModel>() {

                override fun areItemsTheSame(
                    oldItem: ShowTimerUiModel,
                    newItem: ShowTimerUiModel
                ): Boolean = oldItem.timerId == newItem.timerId

                override fun areContentsTheSame(
                    oldItem: ShowTimerUiModel,
                    newItem: ShowTimerUiModel
                ): Boolean = oldItem == newItem

                override fun getChangePayload(
                    oldItem: ShowTimerUiModel,
                    newItem: ShowTimerUiModel
                ): Any? {
                    val payloads = mutableListOf<TimerPayload>()

                    if (oldItem.remainingTime != newItem.remainingTime) {
                        payloads += TimerPayload.TimeAndProgress
                    }

                    if (oldItem.isRunning != newItem.isRunning) {
                        payloads += TimerPayload.RunningState
                    }

                     // Check if we crossed the 0 threshold
                    val wasFinished = oldItem.remainingTime <= 0
                    val isFinished = newItem.remainingTime <= 0
                    if (wasFinished != isFinished) {
                        payloads += TimerPayload.CardColor
                    }

                    return payloads.takeIf { it.isNotEmpty() }
                }
            }
    }


    // ----------------------------------------------------
    // ViewHolder
    // ----------------------------------------------------
    class TimerViewHolder(
        private val binding: TimerItemLayoutBinding,
        private val onEvent: (ShowTimerEvent) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context get() = itemView.context

        /** ALWAYS the latest item */
        private var currentItem: ShowTimerUiModel? = null

        // ---------------------------
        // Full bind
        // ---------------------------
        fun bind(item: ShowTimerUiModel) {
            currentItem = item

            binding.timerItemTitle.text = item.targetTime.toFormattedTimerTime(context)
            updateTimerProgress(item)
            updateRunningAndSnoozeUI(item.isRunning)
            updateBackgroundColor(item.remainingTime > 0)
            setButtonListeners()
        }

        // ---------------------------
        // Partial bind
        // ---------------------------
        fun partialBind(payloads: List<Any>, item: ShowTimerUiModel) {
            currentItem = item

            payloads
                .filterIsInstance<List<TimerPayload>>()
                .flatten()
                .forEach { payload ->
                    when (payload) {
                        TimerPayload.TimeAndProgress ->
                            updateTimerProgress(item)

                        TimerPayload.RunningState ->
                            updateRunningAndSnoozeUI(item.isRunning)

                        TimerPayload.CardColor ->
                            updateBackgroundColor(item.remainingTime > 0)
                    }
                }
        }

        // ----------------------------------------------------
        // UI updates
        // ----------------------------------------------------
        private fun updateTimerProgress(item: ShowTimerUiModel) = with(binding) {
            remainingTimerItemTime.text = item.remainingTime.toFormattedTimerTime(context)
            circularProgressBar.progress = item.getProgress()
        }

        private fun updateRunningAndSnoozeUI(isRunning: Boolean) = with(binding) {
            val icon = if (isRunning) R.drawable.ic_pause else R.drawable.ic_play

            if (playPauseTimerItemBtn.tag != icon) {
                playPauseTimerItemBtn.setImageResource(icon)
                playPauseTimerItemBtn.tag = icon
            }

            snoozeTimerItemBtn.visibility = if (isRunning) View.VISIBLE else View.GONE
        }

        private fun updateBackgroundColor(hasTimeLeft: Boolean) {
            val attr = if (hasTimeLeft)
                    com.google.android.material.R.attr.colorSurfaceContainerHighest
                else
                    com.google.android.material.R.attr.colorErrorContainer

            val newColor = MaterialColors.getColor(binding.timerCard, attr)
            val current = (binding.timerCard.background as? ColorDrawable)?.color

            if (current != newColor) {
                binding.timerCard.setBackgroundColor(newColor)
            }
        }

        // ----------------------------------------------------
        // Click listeners (NO stale model)
        // ----------------------------------------------------
        private fun setButtonListeners() = with(binding) {

            playPauseTimerItemBtn.setOnClickListener {
                currentItem?.let { item ->
                    val model = TimerMapper.toDomainModel(item)
                    onEvent(
                        if (item.isRunning)
                            ShowTimerEvent.PauseTimer(model)
                        else
                            ShowTimerEvent.StartTimer(model)
                    )
                }
            }

            snoozeTimerItemBtn.setOnClickListener {
                currentItem?.let {
                    onEvent(
                        ShowTimerEvent.SnoozeTimer(
                            TimerMapper.toDomainModel(it)
                        )
                    )
                }
            }

            restartTimerItemBtn.setOnClickListener {
                currentItem?.let {
                    onEvent(
                        ShowTimerEvent.RestartTimer(
                            TimerMapper.toDomainModel(it)
                        )
                    )
                }
            }

            finishTimerItemBtn.setOnClickListener {
                currentItem?.let {
                    onEvent(
                        ShowTimerEvent.StopTimer(
                            TimerMapper.toDomainModel(it)
                        )
                    )
                }
            }
        }
    }

    // ----------------------------------------------------
    // Adapter overrides
    // ----------------------------------------------------

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val binding = TimerItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TimerViewHolder(binding, onTimerEvent)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: TimerViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            holder.bind(getItem(position))
        } else {
            holder.partialBind(payloads, getItem(position))
        }
    }

}