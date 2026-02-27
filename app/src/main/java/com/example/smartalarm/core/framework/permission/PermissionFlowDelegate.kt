package com.example.smartalarm.core.framework.permission

import android.util.Log
import androidx.fragment.app.Fragment
import com.example.smartalarm.core.framework.permission.model.AppFeature
import com.example.smartalarm.core.framework.permission.model.MyAppPermission
import com.example.smartalarm.core.framework.permission.model.MyPermissionStatus
import com.example.smartalarm.core.framework.permission.model.PermissionResult
import com.example.smartalarm.core.framework.permission.model.Requirement
import com.example.smartalarm.core.utility.extension.showToast

class PermissionFlowDelegate(
    private val fragment: Fragment,
    private val checker: MyPermissionChecker,
    private val requester: MyAppPermissionRequester
) {

    // -----------------------------
    // Entry
    // -----------------------------

    /**
     * Starts the sequential permission flow.
     *
     * Processes all requirements one by one and
     * invokes [onAllGranted] when every permission
     * has been successfully granted.
     */
    fun run(requirements: List<Requirement>, onAllGranted: () -> Unit) {
        processQueue(requirements, onAllGranted)
    }


    // -----------------------------
    // Queue Processor
    // -----------------------------
    private fun processQueue(
        queue: List<Requirement>,
        onAllGranted: () -> Unit
    ) {

        val current = queue.firstOrNull() ?: return onAllGranted()
        val remaining = queue.drop(1)

        val onNext = { processQueue(remaining, onAllGranted) }
        val onDeny = { fragment.requireContext().showToast(current.toastOnDeny) }

        val status = when (val permission = current.permission) {
            is MyAppPermission.Runtime -> checker.checkRuntimeStatus(fragment.requireActivity(), permission)
            is MyAppPermission.Special -> checker.checkSpecialStatus(permission)
        }

        when (status) {

            is MyPermissionStatus.RuntimeStatus.Granted -> { onNext() }
            is MyPermissionStatus.RuntimeStatus.ShowRationale -> { showRuntimePermissionRationale(current, onNext, onDeny) }
            is MyPermissionStatus.RuntimeStatus.Denied -> handleRuntimePermissionDenied( current,(current.permission as MyAppPermission.Runtime), onNext, onDeny)

            is MyPermissionStatus.SpecialStatus.Granted -> { onNext() }
            is MyPermissionStatus.SpecialStatus.Denied -> handleSpecialDenied(current, onNext, onDeny)

        }
    }





    // -----------------------------
    // Execute Request
    // -----------------------------

    private fun executeRequest(req: Requirement, onNext: () -> Unit, onDeny: () -> Unit) {
        when (val permission = req.permission) {

            // ---------- Runtime ----------
            is MyAppPermission.Runtime -> {
                requester.requestRuntimePermission(permission) { result ->
                    when (result) {
                        is PermissionResult.RuntimePermissionResult.Granted ->  onNext()
                        is PermissionResult.RuntimePermissionResult.Denied ->  onDeny()
                        is PermissionResult.RuntimePermissionResult.PermanentlyDenied -> showRuntimePermanentDeniedDialog(req, onNext, onDeny)
                    }
                }
            }


            // ---------- Special ----------
            is MyAppPermission.Special -> {
                requester.requestSpecialPermission(permission) { result ->
                    when (result) {
                        is PermissionResult.SpecialPermissionResult.Granted -> onNext()
                        is PermissionResult.SpecialPermissionResult.Denied -> onDeny()
                    }
                }
            }

        }
    }


    // -----------------------------
    // Special Permission Handling
    // -----------------------------

    private fun handleRuntimePermissionDenied(req: Requirement, permission: MyAppPermission.Runtime, onNext: () -> Unit, onDeny: () -> Unit) {

        Log.d("PermissionFlowDelegate", "handleRuntimePermissionDenied: $permission")

        // Check if the permission is runtime + has capability
        if (permission.hasCapability) {
            // Directly show the capability-denied dialog
            showRuntimePermanentDeniedDialog(req, onNext, onDeny)
        } else {
            // Normal runtime request flow
            executeRequest(req, onNext, onDeny)
        }
    }
    private fun handleSpecialDenied(req: Requirement, onNext: () -> Unit, onDeny: () -> Unit) {

        val feature = req.feature ?: return executeRequest(req, onNext, onDeny)

        val permissionKey = req.permission.permissionKey
        val alreadyShown = PermissionSessionTracker.hasBeenShown(feature, permissionKey)

        // Already explained once this session
//        if (alreadyShown) {
//            executeRequest(req, onNext, onDeny)
//            return
//        }

        showSpecialPermissionRationale(req, onNext, onDeny, feature, permissionKey)
    }





    // -----------------------------
    // Rationale Dialogs
    // -----------------------------

    private fun showRuntimePermissionRationale(req: Requirement, onNext: () -> Unit, onDeny: () -> Unit) {
        PermissionRationaleDialog.showRationale(
            fragmentManager = fragment.childFragmentManager,
            title = req.rationaleTitle,
            message = req.rationaleMessage,
            onPositive = { executeRequest(req, onNext, onDeny) },
            onNegative = onDeny
        )
    }
    private fun showSpecialPermissionRationale(req : Requirement, onNext : () -> Unit, onDeny : () -> Unit, feature : AppFeature, permissionKey : String) {
        PermissionRationaleDialog.showRationale(
            fragmentManager = fragment.childFragmentManager,
            title = req.rationaleTitle,
            message = req.rationaleMessage,
            positiveText = "Go To Settings",
            negativeText = "Cancel",
            onPositive = {
                PermissionSessionTracker.markAsShown(feature, permissionKey)
                executeRequest(req, onNext, onDeny)
            },

            onNegative = { PermissionSessionTracker.markAsShown(feature, permissionKey)
                onDeny()
            }
        )
    }




    // -----------------------------
    // Runtime Permanent Denied
    // -----------------------------
    private fun showRuntimePermanentDeniedDialog(req: Requirement, onNext: () -> Unit, onDeny: () -> Unit) {

        // Only Runtime permissions supported here
        val runtimePermission = req.permission as? MyAppPermission.Runtime
            ?: run {
                onDeny()
                return
            }

        val feature = req.feature

        val permissionKey = req.permission.permissionKey


        // Session already shown?
        if (feature != null && PermissionSessionTracker.hasBeenShown(feature, permissionKey)) {
            //onDeny()
            onNext()
            return
        }


        PermissionRationaleDialog.showGoToSettings(
            fragmentManager = fragment.childFragmentManager,
            title = req.permanentlyDeniedTitle ?: "Permission Blocked",
            message = req.permanentlyDeniedMessage ?: "Please enable permission from settings.",
            onPositive = {
                requester.requestAppSettings(runtimePermission) { granted ->
                    if (granted) {
                        processQueue(listOf(req), onNext)
                    } else {
                        feature?.let { PermissionSessionTracker.markAsShown(it, permissionKey) }
                        onDeny()
                    }
                }
            },

            onNegative = {
                feature?.let { PermissionSessionTracker.markAsShown(it, permissionKey) }
                onDeny()
            }
        )
    }



}