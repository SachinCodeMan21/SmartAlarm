package com.example.smartalarm.feature.alarm.presentation.view.handler

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.smartalarm.R
import com.example.smartalarm.databinding.FragmentShowAlarmBinding
import com.example.smartalarm.feature.alarm.framework.manager.contract.VibrationManager
import com.example.smartalarm.feature.alarm.presentation.event.mission.ShowAlarmEvent
import com.example.smartalarm.feature.alarm.presentation.viewmodel.mission.ShowAlarmViewModel
import kotlin.math.abs

/**
 * Lifecycle-aware handler for alarm swipe gesture interactions.
 *
 * This class manages the drag slider logic for snoozing and stopping/completing alarms.
 * It implements [DefaultLifecycleObserver] to properly handle fragment lifecycle events
 * and ensure clean state management.
 *
 * Key responsibilities:
 * - Set up and manage swipe gesture detection
 * - Handle thumb position animations
 * - Update hint icon opacities based on drag position
 * - Trigger alarm actions (snooze/stop/complete) when threshold is met
 * - Reset thumb position when returning from mission fragments
 *
 * @param binding The view binding for the fragment
 * @param context The application context
 * @param viewModel The ViewModel to send events to
 * @param vibrationManager Manager for haptic feedback
 * @param onShowToast Callback to show toast messages
 */
class AlarmSwipeHandler(
    private val binding: FragmentShowAlarmBinding,
    private val context: Context,
    private val vibrationManager: VibrationManager,
    private val onSnooze: () -> Unit,
    private val onStopOrStartMission: () -> Unit,
    private val onShowToast: (String) -> Unit
) : DefaultLifecycleObserver {

    companion object {
        // Threshold percentage for completing swipe action (80% of width)
        private const val SWIPE_THRESHOLD = 0.8f

        // Animation duration for returning thumb to center
        private const val RETURN_ANIMATION_DURATION = 200L
    }

    // Swipe tracking variables
    private var initialX = 0f
    private var initialThumbX = 0f
    private var containerWidth = 0
    private var thumbWidth = 0
    private var isDragging = false

    // State flags
    private var hasSnoozeLeft = true
    private var thumbMovedFromCenter = false
    private var isMissionAvailable = false

    /**
     * Called when the lifecycle owner resumes.
     * Resets thumb position if it was previously moved from center
     * (e.g., when returning from mission fragments).
     */
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        // Only reset if thumb was actually moved to an edge
        if (thumbMovedFromCenter) {
            binding.swipeContainer.post {
                resetThumbPosition()
                thumbMovedFromCenter = false
            }
        }
    }

    /**
     * Sets up the swipe gesture for the draggable thumb.
     * Allows user to drag left for snooze or right for stop/complete mission.
     */
    @SuppressLint("ClickableViewAccessibility")
    fun setupSwipeGesture() {
        // Post to ensure views are measured
        binding.swipeContainer.post {
            containerWidth = binding.swipeContainer.width
            thumbWidth = binding.dragThumb.width

            // Calculate center position
            val centerX = (containerWidth - thumbWidth) / 2f
            binding.dragThumb.x = centerX

            binding.dragThumb.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = event.rawX
                        initialThumbX = view.x
                        isDragging = true
                        vibrateShort()
                        true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        if (isDragging) {
                            val deltaX = event.rawX - initialX
                            var newX = initialThumbX + deltaX

                            // Constrain within bounds
                            val minX = 0f
                            val maxX = (containerWidth - thumbWidth).toFloat()
                            newX = newX.coerceIn(minX, maxX)

                            view.x = newX

                            // Update hint opacity based on position
                            updateHintOpacity(newX)
                        }
                        true
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        if (isDragging) {
                            handleSwipeComplete(view.x)
                            isDragging = false
                        }
                        true
                    }

                    else -> false
                }
            }
        }
    }

    /**
     * Updates the UI state based on the new alarm state.
     *
     * @param snoozeCount Number of snoozes remaining
     * @param isMissionAvailable Whether missions are configured
     */
    fun updateState(snoozeCount: Int, isMissionAvailable: Boolean) {
        this.hasSnoozeLeft = snoozeCount > 0
        this.isMissionAvailable = isMissionAvailable

        // Update snooze count visibility and opacity
        if (hasSnoozeLeft) {
            binding.snoozeCountText.isVisible = true
            binding.snoozeCountText.alpha = 0.7f
            binding.hintSnooze.alpha = 0.4f
        } else {
            binding.snoozeCountText.isVisible = false
            binding.snoozeCountText.alpha = 0f
            binding.hintSnooze.alpha = 0.2f
        }

        // Update right hint icon based on mission availability
        val actionIcon = if (isMissionAvailable) {
            R.drawable.ic_mission
        } else {
            R.drawable.ic_stop_thumb
        }
        binding.hintAction.setImageDrawable(
            ContextCompat.getDrawable(context, actionIcon)
        )
    }

    /**
     * Updates the opacity of hint icons based on thumb position.
     */
    private fun updateHintOpacity(thumbX: Float) {
        val centerX = (containerWidth - thumbWidth) / 2f
        val maxDistance = centerX
        val distanceFromCenter = thumbX - centerX
        val absDistance = abs(distanceFromCenter)

        if (distanceFromCenter < 0) {
            // Moving left (snooze)
            if (hasSnoozeLeft) {
                val alpha = (absDistance / maxDistance).coerceIn(0f, 1f) * 0.8f + 0.4f
                binding.hintSnooze.alpha = alpha

                // Hide snooze count text when thumb moves over it
                val textFadeThreshold = centerX * 0.5f
                if (thumbX < textFadeThreshold) {
                    binding.snoozeCountText.alpha = 0f
                } else {
                    binding.snoozeCountText.alpha = alpha
                }
            }
            binding.hintAction.alpha = 0.2f
        } else {
            // Moving right (stop/complete)
            val alpha = (absDistance / maxDistance).coerceIn(0f, 1f) * 0.8f + 0.4f
            binding.hintAction.alpha = alpha

            if (hasSnoozeLeft) {
                binding.hintSnooze.alpha = 0.2f
                binding.snoozeCountText.alpha = 0.7f
            }
        }
    }

    /**
     * Handles the completion of a swipe gesture.
     * Triggers action if threshold is met, otherwise returns to center.
     */
    private fun handleSwipeComplete(finalX: Float) {
        val centerX = (containerWidth - thumbWidth) / 2f
        val swipeDistance = finalX - centerX
        val swipePercentage = abs(swipeDistance) / centerX

        if (swipePercentage >= SWIPE_THRESHOLD) {
            // Threshold met - trigger action
            vibrateShort()

            if (swipeDistance < 0) {
                if (hasSnoozeLeft) {
                    onSnooze()
                    animateThumbToEnd(isLeftSwipe = true)
                } else {
                    onShowToast(context.getString(R.string.no_snooze_left))
                    resetThumbPosition()
                }
            } else {
                onStopOrStartMission()
                animateThumbToEnd(isLeftSwipe = false)
            }
        } else {
            // Threshold not met - return to center
            resetThumbPosition()
        }
    }

    /**
     * Animates the thumb to the end position (left or right) after successful action.
     * This provides visual feedback that the action was completed.
     *
     * @param isLeftSwipe True if swiped left (snooze), false if swiped right (stop/complete)
     */
    private fun animateThumbToEnd(isLeftSwipe: Boolean) {
        val targetX = if (isLeftSwipe) {
            0f // Left end
        } else {
            (containerWidth - thumbWidth).toFloat() // Right end
        }

        ObjectAnimator.ofFloat(binding.dragThumb, "x", binding.dragThumb.x, targetX).apply {
            duration = RETURN_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
            start()
        }

        // Set flag that thumb has moved from center
        thumbMovedFromCenter = true

        // Update hint opacities to show completed action
        if (isLeftSwipe) {
            // Snooze completed - brighten left side
            binding.hintSnooze.alpha = 1.0f
            binding.snoozeCountText.alpha = 0f
            binding.hintAction.alpha = 0.2f
        } else {
            // Stop/Complete action - brighten right side
            binding.hintAction.alpha = 1.0f
            if (hasSnoozeLeft) {
                binding.hintSnooze.alpha = 0.2f
                binding.snoozeCountText.alpha = 0.7f
            }
        }
    }

    /**
     * Animates the thumb back to the center position.
     */
    private fun resetThumbPosition() {
        if (containerWidth == 0 || thumbWidth == 0) return

        val centerX = (containerWidth - thumbWidth) / 2f

        ObjectAnimator.ofFloat(binding.dragThumb, "x", binding.dragThumb.x, centerX).apply {
            duration = RETURN_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
            start()
        }

        // Reset hint opacities based on snooze availability
        if (hasSnoozeLeft) {
            binding.hintSnooze.alpha = 0.4f
            binding.snoozeCountText.alpha = 0.7f
        } else {
            binding.hintSnooze.alpha = 0.2f
            binding.snoozeCountText.alpha = 0.0f
        }
        binding.hintAction.alpha = 0.4f
    }

    /**
     * Triggers a short vibration feedback.
     */
    private fun vibrateShort() {
        vibrationManager.vibrateOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE)
    }
}