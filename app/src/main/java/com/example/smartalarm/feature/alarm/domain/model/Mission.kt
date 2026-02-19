package com.example.smartalarm.feature.alarm.domain.model

import android.os.Parcelable
import com.example.smartalarm.feature.alarm.domain.enums.Difficulty
import kotlinx.parcelize.Parcelize

/**
 * Represents a task or challenge (mission) associated with an alarm.
 *
 * Missions are used to require the user to perform a mental or physical task in order
 * to dismiss or stop the alarm. This is intended to ensure the user is fully awake.
 *
 * Each mission has a type (e.g., memory, math), difficulty level, and number of rounds to complete.
 * Missions can also track whether they've been completed.
 *
 * This class implements [Parcelable] to support passing between Android components (e.g., via Intents or Bundles).
 *
 * @property type The [MissionType] of the mission (e.g., Memory, Math, etc.).
 * @property difficulty The [Difficulty] level of the mission (EASY, NORMAL, HARD, EXPERT).
 * @property rounds The number of rounds the user must complete to finish the mission.
 * @property iconResId Resource ID for the icon representing this mission.
 * @property isCompleted Indicates whether the mission has been completed.
 */
@Parcelize
data class Mission(
    val type: MissionType = MissionType.Memory,
    val difficulty: Difficulty = Difficulty.EASY,
    val rounds: Int = 3,
    val iconResId: Int,
    val isCompleted: Boolean = false
) : Parcelable