package com.example.smartalarm.core.utility.extension

import com.example.smartalarm.core.model.Result

/**
 * Executes the given suspending [block] and wraps its result in a [Result].
 *
 * If the block completes successfully, the returned [Result] will be
 * [Result.Success] containing the value produced by the block.
 * If the block throws an exception, the exception is caught and wrapped in
 * [Result.Error].
 *
 * This helper simplifies error handling by converting exceptions into a
 * unified result type.
 *
 * @param block A suspending function to execute.
 * @return A [Result] representing either the successful output or the error.
 */
suspend fun <T> runCatchingResult(block: suspend () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Exception) {
    Result.Error(e)
}