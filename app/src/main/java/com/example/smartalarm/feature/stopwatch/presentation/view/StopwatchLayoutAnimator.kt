package com.example.smartalarm.feature.stopwatch.presentation.view

import android.content.res.Resources
import android.util.TypedValue
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.example.smartalarm.R
import com.example.smartalarm.databinding.FragmentStopwatchBinding
import com.example.smartalarm.core.utility.extension.getDimenRawFloat

/**
 * Handles animated ConstraintLayout updates for the stopwatch screen.
 *
 * Centralizes layout animation logic for portrait and landscape orientations,
 * adjusting guidelines, progress bar bias, and text size based on lap availability.
 *
 * Uses ConstraintSet transitions to ensure smooth and consistent UI updates
 * across different screen sizes and orientations.
 */
class StopwatchLayoutAnimator(private val binding: FragmentStopwatchBinding) {


    val res: Resources = binding.root.context.resources


    /**
     * Animates stopwatch layout changes for portrait orientation.
     *
     * Updates guidelines and progress bar vertical bias based on lap availability.
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
     * Animates stopwatch layout changes for landscape orientation.
     *
     * Updates guidelines and progress bar horizontal bias based on lap availability.
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
     * Animates stopwatch layout changes using ConstraintSet transitions.
     *
     * Applies common animation logic (text size update + delayed transition)
     * while allowing callers to provide orientation-specific constraint updates.
     *
     * @param isLapTimeAvailable Whether lap data is present, affecting layout values
     * @param applyConstraints Lambda for applying custom ConstraintSet changes
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
     * Returns the top guideline percentage based on smallest screen width size and lap availability.
     */
    private fun getTopGuidelinePercent(isLapTimeAvailable: Boolean): Float {
        val defaultTopPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_top)
        val animTopPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_anim_top)
        return if (isLapTimeAvailable) animTopPercentage else defaultTopPercentage
    }

    /**
     * Returns the start guideline percentage based on smallest screen width size and lap availability.
     */
    private fun getStartGuidelinePercent(isLapTimeAvailable: Boolean): Float {
        val defaultStartPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_start)
        val animStartPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_anim_start)
        return if (isLapTimeAvailable) animStartPercentage else defaultStartPercentage
    }

    /**
     * Returns the end guideline percentage based on smallest screen width size and lap availability.
     */
    private fun getEndGuidelinePercent(isLapTimeAvailable: Boolean): Float {
        val defaultEndPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_end)
        val animEndPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_anim_end)
        return if (isLapTimeAvailable) animEndPercentage else defaultEndPercentage
    }

    /**
     * Returns the bottom guideline percentage based on smallest screen width size and lap availability.
     */
    private fun getBottomGuidelinePercent(isLapTimeAvailable: Boolean): Float {
        val defaultBottomPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_bottom)
        val animBottomPercentage = res.getDimenRawFloat(R.dimen.guideline_stopwatch_anim_bottom)
        return if (isLapTimeAvailable) animBottomPercentage else defaultBottomPercentage
    }




    //=====================================
    // Bias Getters
    //=====================================

    /**
     * Returns the vertical bias for the progress bar based on lap availability.
     */
    private fun getProgressBarVerticalBias(isLapTimeAvailable: Boolean): Float {
        return if (isLapTimeAvailable) 0.05f else 0.50f
    }


    /**
     * Returns the horizontal bias for the progress bar based on lap availability.
     */
    private fun getProgressBarHorizontalBias(isLapTimeAvailable: Boolean): Float {
        return if (isLapTimeAvailable) 0.05f else 0.40f
    }




    //=====================================
    // TextSize Getter
    //=====================================

    /**
     * Returns the stopwatch seconds text size (in sp) based on smallest screen size and lap availability.
     */
    private fun getStopwatchTextSize(isLapTimeAvailable: Boolean): Float {
        val textSizeResId = if (isLapTimeAvailable)
            R.dimen.stopwatch_seconds_tv_tran_textSize
        else
            R.dimen.text_size_l
        return res.getDimension(textSizeResId) / res.displayMetrics.density
    }



}