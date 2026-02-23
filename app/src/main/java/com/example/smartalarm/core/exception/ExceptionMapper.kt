package com.example.smartalarm.core.exception

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.database.sqlite.SQLiteDatabaseLockedException
import android.database.sqlite.SQLiteDiskIOException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteFullException
import java.io.IOException


object ExceptionMapper {

    fun map(throwable: Throwable): AppError {
        return when (throwable) {
            // ---- Database / Physical Storage ----

            // The disk is literally out of space
            is SQLiteFullException -> AppError.Database.DiskFull

            // Hardware-level failure or OS-level file access issues
            is SQLiteDiskIOException -> AppError.Database.DeviceError

            // The database file is mangled (usually requires a fresh install/cache clear)
            is SQLiteDatabaseCorruptException -> AppError.Database.Corrupted

            // Concurrency issues (too many threads trying to write at once)
            is SQLiteDatabaseLockedException -> AppError.Database.Busy

            // Constraint violations (e.g., trying to insert a Lap with a non-existent Session ID)
            is SQLiteConstraintException -> AppError.Database.ConstraintViolation

            // Catch-all for other Room/SQLite issues
            is SQLiteException -> AppError.Database.Unavailable

            // ---- Business Logic & State ----

            // For example: trying to Pause a stopwatch that is already Deleted
            is IllegalStateException -> AppError.Validation.InvalidState(throwable.message ?: "Action not allowed")

            // For example: passing a negative timestamp
            is IllegalArgumentException -> AppError.Validation.InvalidInput

            // ---- Connectivity (If your Repository ever syncs to a cloud) ----
            is IOException -> AppError.Network.NoInternet

            // ---- Fallback ----
            else -> AppError.Unknown(throwable)
        }
    }
}