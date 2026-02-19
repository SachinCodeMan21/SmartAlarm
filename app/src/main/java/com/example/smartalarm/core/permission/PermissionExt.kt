package com.example.smartalarm.core.permission

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun FragmentActivity.createPermissionRequester(checker: PermissionChecker) =
    PermissionRequester(
        caller = this,
        lifecycleOwner = this,
        context = this,
        permissionChecker = checker,
        rationaleProvider = { shouldShowRequestPermissionRationale(it) }
    )

fun Fragment.createPermissionRequester(checker: PermissionChecker) =
    PermissionRequester(
        caller = this,
        lifecycleOwner = viewLifecycleOwner, // Crucial for Fragments!
        context = requireContext(),
        permissionChecker = checker,
        rationaleProvider = { shouldShowRequestPermissionRationale(it) }
    )