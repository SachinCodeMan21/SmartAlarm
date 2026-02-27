package com.example.smartalarm.core.utility.extension

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.core.utility.exception.GeneralErrorMapper
import com.example.smartalarm.core.utility.exception.MyResult
import kotlin.coroutines.cancellation.CancellationException

// Specify both T (Data) and DataError (Error)
suspend inline fun <T> myRunCatchingResult(
    block: suspend () -> T
): MyResult<T, DataError> { // <--- Added the second type argument
    return try {
        MyResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // Now the compiler knows that mapDatabaseException returns a DataError
        MyResult.Error(GeneralErrorMapper.mapDatabaseException(e))
    }
}