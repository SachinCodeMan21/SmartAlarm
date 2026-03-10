package com.example.smartalarm.feature.stopwatch.presentation.view

import android.content.res.Resources
import android.util.TypedValue
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.example.smartalarm.R
import com.example.smartalarm.databinding.FragmentStopwatchBinding
import com.example.smartalarm.core.utility.extension.getDimenRawFloat

/**
 * Layout Transition Coordinator responsible for orchestrating complex UI animations
 * within the Stopwatch screen.
 *
 * This class leverages the [ConstraintSet] and [TransitionManager] APIs to perform
 * declarative layout updates. It centralizes the logic for structural UI changes—such
 * as guideline shifts, bias adjustments, and text scaling—ensuring smooth,
 * interruptible transitions between "Default" and "Lap Active" view states.
 *
 * ### Architectural Role:
 * Acts as a UI helper to keep the [StopwatchFragment] focused on lifecycle and state
 * observation, isolating the imperative logic required for manual layout manipulation.
 */
class StopwatchLayoutAnimator(private val binding: FragmentStopwatchBinding) {


    val res: Resources = binding.root.context.resources


    /**
     * Triggers portrait-specific layout reconciliations.
     * * Dynamically repositions horizontal guidelines and adjusts the vertical
     * focal point (bias) of the progress indicator to accommodate the lap list.
     * * @param isLapTimeAvailable Determines if the layout should contract to show the list.
     */
    fun animateStopWatchLayoutPortrait(isLapTimeAvailable: Boolean) {
        animateStopWatchLayout(isLapTimeAvailable) {

            setGuidelinePercent(
                R.id.stopwatch_guideline_start,
                getStartGuidelinePercent(isLapTimeAvailable)
            )

            setGuidelinePercent(
                R.id.stopwatch_guideline_end,
                getEndGuidelinePercent(isLapTimeAvailable)
            )

            setVerticalBias(
                R.id.stopwatch_progress_bar_indicator,
                getProgressBarVerticalBias(isLapTimeAvailable)
            )
        }
    }

    /**
     * Triggers landscape-specific layout reconciliations.
     * * Optimizes screen real-estate by shifting vertical guidelines and horizontal
     * bias, ensuring the stopwatch timer remains legible alongside the lap data.
     */
    fun animateStopWatchLayoutLandscape(isLapTimeAvailable: Boolean) {
        animateStopWatchLayout(isLapTimeAvailable) {

            setGuidelinePercent(
                R.id.stopwatch_guideline_top,
                getTopGuidelinePercent(isLapTimeAvailable)
            )

            setGuidelinePercent(
                R.id.stopwatch_guideline_bottom,
                getBottomGuidelinePercent(isLapTimeAvailable)
            )

            setHorizontalBias(
                R.id.stopwatch_progress_bar_indicator,
                getProgressBarHorizontalBias(isLapTimeAvailable)
            )
        }
    }


    /**
     * Internal engine for applying atomic layout transitions.
     *
     * Clones the current [androidx.constraintlayout.widget.ConstraintLayout] state into a [ConstraintSet],
     * applies mutations via [applyConstraints], and utilizes [TransitionManager]
     * to interpolate between the current and target states.
     *
     * @param isLapTimeAvailable State flag used to derive visual metrics like text size.
     * @param applyConstraints Target constraints to be applied to the cloned set.
     */
    private inline fun animateStopWatchLayout(
        isLapTimeAvailable: Boolean,
        crossinline applyConstraints: ConstraintSet.() -> Unit
    ) = with(binding) {

        val constraintSet = ConstraintSet().apply {
            clone(stopwatchFragmentRoot)
            applyConstraints()
        }

        // Apply text size (common)
        val textSize = getStopwatchTextSize(isLapTimeAvailable)
        stopwatchSecondsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)

        TransitionManager.beginDelayedTransition(stopwatchFragmentRoot)
        constraintSet.applyTo(stopwatchFragmentRoot)
    }




    //=====================================
    // Guideline Percentage Getters
    //=====================================


    /**
     * Resolves the target guideline percentage based on the active View State.
     * Leverages dimension resources to support varying screen densities.
     */
    private fun getTopGuidelinePercent(isLapTimeAvailable: Boolean): Float {
        val defaultTopPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_top)
        val animTopPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_anim_top)
        return if (isLapTimeAvailable) animTopPercentage else defaultTopPercentage
    }

    private fun getStartGuidelinePercent(isLapTimeAvailable: Boolean): Float {
        val defaultStartPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_start)
        val animStartPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_anim_start)
        return if (isLapTimeAvailable) animStartPercentage else defaultStartPercentage
    }

    private fun getEndGuidelinePercent(isLapTimeAvailable: Boolean): Float {
        val defaultEndPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_end)
        val animEndPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_anim_end)
        return if (isLapTimeAvailable) animEndPercentage else defaultEndPercentage
    }

    private fun getBottomGuidelinePercent(isLapTimeAvailable: Boolean): Float {
        val defaultBottomPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_bottom)
        val animBottomPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_anim_bottom)
        return if (isLapTimeAvailable) animBottomPercentage else defaultBottomPercentage
    }




    //=====================================
    // Bias Getters
    //=====================================

    /**
     * Derives the optimal vertical positioning (0.0 to 1.0) for the timer.
     * Pulls the timer toward the top (0.05f) when laps are visible to maximize
     * scrolling real-estate.
     */
    private fun getProgressBarVerticalBias(isLapTimeAvailable: Boolean): Float {
        return if (isLapTimeAvailable) 0.05f else 0.50f
    }
    private fun getProgressBarHorizontalBias(isLapTimeAvailable: Boolean): Float {
        return if (isLapTimeAvailable) 0.05f else 0.50f
    }




    //=====================================
    // TextSize Getter
    //=====================================

    /**
     * Returns the stopwatch seconds text size (in sp) based on smallest screen size and lap availability.
     */
    private fun getStopwatchTextSize(isLapTimeAvailable: Boolean): Float {
        val textSizeResId = if (isLapTimeAvailable) R.dimen.stopwatch_elapsed_time_trans_size else R.dimen.stopwatch_elapsed_time_size
        return res.getDimension(textSizeResId) / res.displayMetrics.density
    }



}