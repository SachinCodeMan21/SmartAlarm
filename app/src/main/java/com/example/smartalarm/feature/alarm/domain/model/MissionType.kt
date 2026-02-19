package com.example.smartalarm.feature.alarm.domain.model

import android.content.Context
import android.os.Parcelable
import com.example.smartalarm.R
import com.example.smartalarm.feature.alarm.presentation.view.bottomSheet.MathMissionBottomSheet
import com.example.smartalarm.feature.alarm.presentation.view.bottomSheet.MemoryMissionBottomSheet
import com.example.smartalarm.feature.alarm.presentation.view.bottomSheet.ShakeMissionBottomSheet
import com.example.smartalarm.feature.alarm.presentation.view.bottomSheet.StepMissionBottomSheet
import com.example.smartalarm.feature.alarm.presentation.view.bottomSheet.TypingMissionBottomSheet
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.parcelize.Parcelize

/**
 * Sealed class representing different types of missions available in the app.
 * Each mission type defines how it is displayed, its icon, color, and provides a
 * BottomSheetDialogFragment to present the mission UI.
 *
 * Implements [Parcelable] for easy passing between Android components.
 */
@Parcelize
sealed class MissionType : Parcelable {

    /**
     * Returns the display name of the mission type.
     *
     * @return A user-friendly name for the mission type.
     */
    abstract fun getName(): String

    /**
     * Returns a [BottomSheetDialogFragment] that corresponds to this mission type,
     * initialized with the provided [mission] data.
     *
     * @param mission The mission data to display.
     * @return A [BottomSheetDialogFragment] instance for the mission UI.
     */
    abstract fun getMissionBottomSheet(mission: Mission, missionHolderPosition : Int): BottomSheetDialogFragment

    /**
     * Returns the navigation destination ID for this mission type.
     */
    abstract fun getNavigationActionId(): Int

    /**
     * Returns the resource ID for the icon representing this mission type.
     *
     * @return Drawable resource ID of the mission icon.
     */
    abstract fun getIconRes(): Int

    /**
     * Returns the resource ID for the color associated with this mission type.
     *
     * @return Color resource ID representing the mission.
     */
    abstract fun getColorRes(): Int

    /** Represents a memory-based mission type. */
    @Parcelize
    object Memory : MissionType() {
        override fun getName() = "memory"
        override fun getMissionBottomSheet(mission: Mission,missionHolderPosition : Int) =
            MemoryMissionBottomSheet.newInstance(mission,missionHolderPosition)
        override fun getNavigationActionId() = R.id.memoryMissionFragment
        override fun getIconRes() = R.drawable.ic_memory
        override fun getColorRes() = android.R.color.holo_blue_bright
    }

    /** Represents a typing-based mission type. */
    @Parcelize
    object Typing : MissionType() {
        override fun getName() = "typing"
        override fun getMissionBottomSheet(mission: Mission,missionHolderPosition : Int) =
            TypingMissionBottomSheet.newInstance(mission,missionHolderPosition)
        override fun getNavigationActionId() = R.id.typingMissionFragment
        override fun getIconRes() = R.drawable.ic_typing
        override fun getColorRes() = android.R.color.holo_orange_light
    }

    /** Represents a math-based mission type. */
    @Parcelize
    object Maths : MissionType() {
        override fun getName() = "maths"
        override fun getMissionBottomSheet(mission: Mission,missionHolderPosition : Int) =
            MathMissionBottomSheet.newInstance(mission,missionHolderPosition)
        override fun getNavigationActionId() = R.id.mathMissionFragment
        override fun getIconRes() = R.drawable.ic_math
        override fun getColorRes() = android.R.color.holo_green_light
    }

    /** Represents a shake-based mission type. */
    @Parcelize
    object Shake : MissionType() {
        override fun getName() = "shake"
        override fun getMissionBottomSheet(mission: Mission,missionHolderPosition : Int) =
            ShakeMissionBottomSheet.newInstance(mission,missionHolderPosition)
        override fun getNavigationActionId() = R.id.shakeMissionFragment
        override fun getIconRes() = R.drawable.ic_shake
        override fun getColorRes() = android.R.color.holo_red_light
    }

    /** Represents a step-based mission type. */
    @Parcelize
    object Step : MissionType() {
        override fun getName() = "step"
        override fun getMissionBottomSheet(mission: Mission,missionHolderPosition : Int) =
            StepMissionBottomSheet.newInstance(mission,missionHolderPosition)
        override fun getNavigationActionId() = R.id.stepMissionFragment
        override fun getIconRes() = R.drawable.ic_step
        override fun getColorRes() = android.R.color.holo_purple
    }


    companion object {

        /**
         * A helper function to fetch the localized mission name.
         */
        fun getLocalizedName(context: Context, missionType: MissionType): String {
            return when (missionType) {
                Memory -> context.getString(R.string.memory)
                Typing -> context.getString(R.string.typing)
                Maths -> context.getString(R.string.maths)
                Shake -> context.getString(R.string.shake)
                Step -> context.getString(R.string.step)
            }
        }


        /**
         * Returns the [MissionType] corresponding to a given name (non-localized).
         */
        fun fromName(name: String): MissionType {
            return when (name.lowercase()) {
                Memory.getName() -> Memory
                Typing.getName()-> Typing
                Maths.getName() -> Maths
                Shake.getName() -> Shake
                Step.getName() -> Step
                else -> throw IllegalArgumentException("Unknown mission type: $name")
            }
        }


        /**
         * Returns a list of all available mission types.
         *
         * @return List of [MissionType] objects representing all mission types.
         */
        fun getAllAvailableMissionTypes(): List<MissionType> {
            return listOf(Memory, Typing, Maths, Shake, Step)
        }

        /**
         * Returns the count of all available mission types.
         *
         * @return Number of available mission types.
         */
        fun getAvailableMissionCount(): Int {
            return getAllAvailableMissionTypes().size
        }
    }
}
