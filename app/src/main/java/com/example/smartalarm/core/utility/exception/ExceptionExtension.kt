package com.example.smartalarm.core.utility.exception

import com.example.smartalarm.R

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