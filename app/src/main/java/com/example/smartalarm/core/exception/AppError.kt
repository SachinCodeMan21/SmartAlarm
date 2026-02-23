package com.example.smartalarm.core.exception

sealed class AppError {
    sealed class Database : AppError() {
        object Unavailable : Database()         // Generic fallback
        object DiskFull : Database()            // Specific: No space left
        object DeviceError : Database()         // Specific: SQLiteDiskIOException (Hardware/OS)
        object Busy : Database()                // Specific: Locked/Concurrency
        object Corrupted : Database()           // Data integrity
        object ConstraintViolation : Database() // Key/Schema conflict
        object NotFound : Database()            // Query returned nothing
        object MigrationFailed : Database()     // Version mismatch
    }

    sealed class Network : AppError() {
        object NoInternet : Network()
        object Unauthorized : Network()
        object Timeout : Network()
        data class Server(val code: Int) : Network()
    }

    sealed class Validation : AppError() {
        data class InvalidState(val reason: String) : Validation()
        object InvalidInput : Validation()
    }

    data class Unknown(val throwable: Throwable) : AppError()
}