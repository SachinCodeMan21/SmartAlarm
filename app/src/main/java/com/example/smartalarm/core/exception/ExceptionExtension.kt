package com.example.smartalarm.core.exception

import android.content.Context
import com.example.smartalarm.R

fun AppError.toMessage(context: Context): String {
    return when (this) {

        // ---- Network Errors ----
        is AppError.Network.NoInternet ->
            context.getString(R.string.error_no_internet)

        is AppError.Network.Unauthorized ->
            context.getString(R.string.error_unauthorized)

        is AppError.Network.Timeout ->
            context.getString(R.string.error_timeout)

        is AppError.Network.Server ->
            context.getString(R.string.error_server, this.code)

        // ---- Database Errors ----
        is AppError.Database.Unavailable ->
            context.getString(R.string.error_db_unavailable)

        is AppError.Database.Corrupted ->
            context.getString(R.string.error_db_corrupted)

        is AppError.Database.ConstraintViolation ->
            context.getString(R.string.error_db_constraint_violation)

        is AppError.Database.NotFound ->
            context.getString(R.string.error_db_not_found)

        is AppError.Database.MigrationFailed ->
            context.getString(R.string.error_db_migration_failed)

        // ---- Unknown Errors ----
        is AppError.Unknown ->
            context.getString(R.string.error_unknown)

        // Catch-all for any unexpected error
        else -> context.getString(R.string.error_generic)
    }
}


fun DataError.asUiText(): UiText {
    return when (this) {

        // ---- Local / Database Errors ----
        DataError.Local.DISK_FULL ->
            UiText.StringResource(R.string.error_storage_full)

        DataError.Local.DEVICE_ERROR ->
            UiText.StringResource(R.string.error_hardware_failure)

        DataError.Local.BUSY ->
            UiText.StringResource(R.string.error_database_busy)

        DataError.Local.CORRUPTED ->
            UiText.StringResource(R.string.error_database_corrupted)

        DataError.Local.CONSTRAINT_VIOLATION ->
            UiText.StringResource(R.string.error_data_conflict)

        DataError.Local.NOT_FOUND ->
            UiText.StringResource(R.string.error_item_not_found)

        DataError.Local.MIGRATION_FAILED ->
            UiText.StringResource(R.string.error_app_update_issue)

        DataError.Local.DATABASE ->
            UiText.StringResource(R.string.error_generic_database)

        // ---- Network Errors ----
        DataError.Network.NO_CONNECTION ->
            UiText.StringResource(R.string.error_no_internet)

        DataError.Network.UNAUTHORIZED ->
            UiText.StringResource(R.string.error_unauthorized)

        DataError.Network.FORBIDDEN ->
            UiText.StringResource(R.string.error_forbidden)

        DataError.Network.NOT_FOUND ->
            UiText.StringResource(R.string.error_server_not_found)

        DataError.Network.TIMEOUT ->
            UiText.StringResource(R.string.error_network_timeout)

        DataError.Network.PAYLOAD_TOO_LARGE ->
            UiText.StringResource(R.string.error_file_too_large)

        DataError.Network.SERVER_ERROR ->
            UiText.StringResource(R.string.error_server_down)

        DataError.Network.UNKNOWN ->
            UiText.StringResource(R.string.error_generic_network)

        // ---- Unexpected Wrapper ----
        is DataError.Unexpected ->
            UiText.StringResource(R.string.error_unexpected_system)
    }
}