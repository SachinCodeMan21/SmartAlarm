package com.example.smartalarm.core.utility.exception

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.database.sqlite.SQLiteDatabaseLockedException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection.HTTP_CLIENT_TIMEOUT
import java.net.HttpURLConnection.HTTP_ENTITY_TOO_LARGE
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.NoSuchElementException

object GeneralErrorMapper {

    /**
     * Maps network-related exceptions to [DataError.Network].
     */
    fun mapNetworkException(e: Throwable): DataError {
        return when (e) {
            // 1. Connection-level issues (No response from server)
            is UnknownHostException, is ConnectException ->
                DataError.Network.NO_CONNECTION

            is SocketTimeoutException ->
                DataError.Network.TIMEOUT

            // 2. HTTP-level issues (Server responded with an error code)
            is HttpException -> {
                when (e.code()) {
                    HTTP_UNAUTHORIZED -> DataError.Network.UNAUTHORIZED
                    HTTP_FORBIDDEN -> DataError.Network.FORBIDDEN
                    HTTP_NOT_FOUND -> DataError.Network.NOT_FOUND
                    HTTP_CLIENT_TIMEOUT -> DataError.Network.TIMEOUT
                    HTTP_ENTITY_TOO_LARGE -> DataError.Network.PAYLOAD_TOO_LARGE
                    in 500..599 -> DataError.Network.SERVER_ERROR
                    else -> DataError.Network.UNKNOWN
                }
            }

            else -> DataError.Unexpected(e)
        }
    }

    /**
     * Maps database-related exceptions to [DataError.Local].
     * Uses specific Android SQLite exceptions for precise error reporting.
     */
    fun mapDatabaseException(e: Throwable): DataError {
        return when (e) {

            // 1. High Priority Specific SQLite Exceptions
            is SQLiteFullException ->
                DataError.Local.DISK_FULL

            is SQLiteDatabaseCorruptException ->
                DataError.Local.CORRUPTED

            is SQLiteDatabaseLockedException ->
                DataError.Local.BUSY

            is SQLiteDiskIOException ->
                DataError.Local.DEVICE_ERROR

            is SQLiteConstraintException ->
                DataError.Local.CONSTRAINT_VIOLATION

            // 2. Room/General Persistence Exceptions

            is IllegalStateException -> {
                if (e.message?.contains("A migration from", ignoreCase = true) == true) {
                    DataError.Local.MIGRATION_FAILED
                } else {
                    DataError.Unexpected(e)
                }
            }

            is NoSuchElementException ->
                DataError.Local.NOT_FOUND

            is SQLiteException ->
                DataError.Local.DATABASE

            // 3. System-level Fallback
            is IOException ->
                DataError.Local.DEVICE_ERROR

            else -> DataError.Unexpected(e)
        }
    }

}