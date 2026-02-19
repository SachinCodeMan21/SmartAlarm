package com.example.smartalarm.feature.timer.domain.manager

import com.example.smartalarm.feature.timer.domain.model.TimerModel
import kotlinx.coroutines.flow.StateFlow



/**
 * Interface for managing a collection of timers.
 *
 * This interface defines the contract for managing timers, including retrieving, updating, restoring, and removing timers.
 * Implementers of this interface should ensure thread-safety when modifying or updating the list of timers.
 */
interface ShowTimerStateManager {


    fun getTimers(): List<TimerModel>

    fun getTimer(timerId : Int) : TimerModel



    fun getTimersFlow(): StateFlow<List<TimerModel>>

    fun restoreTimers(timers: List<TimerModel>)

    fun tickAllRunningTimers()




    fun hasRunningTimers(): Boolean


    // Has All Active & Completed Timers

    fun hasActiveTimers(): Boolean
    fun hasCompletedTimers(): Boolean


    // Get All Active & Completed Timers
    fun getActiveTimers() : List<TimerModel>
    fun getCompletedTimers() : List<TimerModel>



    // Running Timer Helpers //

    // Check Has Active & Completed Timers
    fun hasRunningActiveTimers(): Boolean
    fun hasRunningCompletedTimers(): Boolean

    // Get All Running Active & Completed Timers
    fun getRunningActiveTimers(): List<TimerModel>
    fun getRunningCompletedTimers(): List<TimerModel>
}




///**
// * Interface for managing a collection of timers.
// *
// * This interface defines the contract for managing timers, including retrieving, updating, restoring, and removing timers.
// * Implementers of this interface should ensure thread-safety when modifying or updating the list of timers.
// */
//interface ShowTimerStateManager {
//
//    /**
//     * Retrieves the current list of timers.
//     *
//     * @return A list of [com.example.smartalarm.feature.timer.domain.model.TimerModel] representing all active timers.
//     */
//    fun getTimers(): List<TimerModel>
//
//    fun getTimer(timerId : Int) : TimerModel
//
//    fun getActiveTimers() : List<TimerModel>
//
//    fun getCompletedTimers() : List<TimerModel>
//
//
//
//    /**
//     * Retrieves a [kotlinx.coroutines.flow.StateFlow] that emits the current list of timers.
//     * This allows other components to observe and react to changes in the timer state.
//     *
//     * @return A [kotlinx.coroutines.flow.StateFlow] that emits the current list of [TimerModel] objects.
//     */
//    fun getTimersFlow(): StateFlow<List<TimerModel>>
//
//    /**
//     * Restores a list of timers to the state manager.
//     * Each timer is updated to reflect the current remaining time, if applicable.
//     *
//     * @param timers The list of timers to restore.
//     */
//    fun restoreTimers(timers: List<TimerModel>)
//
//
//    /**
//     * Updates an existing timer or adds a new one to the collection.
//     *
//     * @param timer The [TimerModel] to update or add.
//     */
//    fun updateTimer(timer: TimerModel)
//
//    /**
//     * Updates the remaining time for a timer by a single tick.
//     *
//     * This method should be called periodically to update the state of running timers.
//     *
//     * @param timerId The ID of the timer to update.
//     */
//    fun tickTimer(timerId: Int)
//
//    fun tickAllRunningTimers()
//
//
//    /**
//     * Removes a timer identified by its ID from the collection.
//     *
//     * @param timerId The ID of the timer to remove.
//     */
//    fun removeTimer(timerId: Int)
//
//    /**
//     * Clears all timers from the state manager, resetting the timer list to empty.
//     */
//    fun clearTimers()
//
//
//    /**
//     * Checks if any timers are currently running and have remaining time.
//     *
//     * @return True if there are timers running, false otherwise.
//     */
//    fun hasRunningTimers(): Boolean
//
//    fun hasActiveTimers(): Boolean
//
//    fun hasCompletedTimers(): Boolean
//
//
//
//    fun hasRunningActiveTimers(): Boolean
//
//    fun hasRunningCompletedTimers(): Boolean
//
//
//
//    fun getRunningActiveTimers(): List<TimerModel>
//
//    fun getRunningCompletedTimers(): List<TimerModel>
//}