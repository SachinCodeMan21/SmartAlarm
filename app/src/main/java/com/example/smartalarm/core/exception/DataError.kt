package com.example.smartalarm.core.exception

sealed interface DataError : Error {

    enum class Local : DataError {
        DATABASE,             // Generic SQL issues
        DISK_FULL,             // Out of space
        DEVICE_ERROR,          // Hardware/IO issues
        BUSY,                  // Database locked
        CORRUPTED,             // Database file mangled
        CONSTRAINT_VIOLATION,  // Foreign key/Unique conflict
        NOT_FOUND,             // Item doesn't exist
        MIGRATION_FAILED       // Schema version mismatch
    }

    enum class Network : DataError {
        NO_CONNECTION,
        UNAUTHORIZED,      // 401
        FORBIDDEN,         // 403
        NOT_FOUND,         // 404
        TIMEOUT,           // 408
        PAYLOAD_TOO_LARGE, // 413
        SERVER_ERROR,      // 5xx
        UNKNOWN            // Fallback
    }

    /** Used to wrap unexpected errors while preserving the stack trace for logging */
    data class Unexpected(val throwable: Throwable) : DataError
}