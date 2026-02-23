package com.example.smartalarm.core.model

import com.example.smartalarm.core.exception.AppError

/**
 * A sealed class representing the result of an operation, which can either succeed with data or fail with an error.
 *
 * This is commonly used to model success/failure outcomes from repositories, use cases, or API calls.
 *
 * @param T The type of data expected in case of success.
 */
sealed class Result<out T> {

    /**
     * Represents a successful result containing the expected data.
     *
     * @param T The type of the successful result.
     * @property data The result data.
     */
    data class Success<T>(val data: T) : Result<T>()


    /**
     * Represents a failed result containing an exception.
     *
     * @property exception The exception that caused the failure.
     */
    data class Error(val error: AppError) : Result<Nothing>()

}