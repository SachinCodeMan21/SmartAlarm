package com.example.smartalarm.core.exception

typealias RootError = Error

sealed interface MyResult<out D, out E : RootError> {
    data class Success<out D, out E : RootError>(val data: D) : MyResult<D, E>
    data class Error<out D, out E : RootError>(val error: E) : MyResult<D, E>
}