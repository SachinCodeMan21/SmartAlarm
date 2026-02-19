package com.example.smartalarm.feature.alarm.domain.model

/**
 * Represents an item in the missions list, which can either be a mission or a placeholder.
 *
 * This sealed class allows for handling different types of items uniformly, useful for UI lists
 * or adapters where missions and placeholders need to coexist.
 */
sealed class MissionItem {

    /**
     * Represents a mission item containing actual mission data.
     *
     * @property mission The [Mission] instance associated with this item.
     */
    data class MissionData(val mission: Mission) : MissionItem()

    /**
     * Represents a placeholder item used for spacing or temporary UI elements.
     *
     * @property id Unique identifier for the placeholder.
     */
    data class Placeholder(val id: Int) : MissionItem()
}
