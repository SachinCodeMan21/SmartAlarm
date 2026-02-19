package com.example.smartalarm.core.utility.extension

import android.content.Context
import android.os.Build


// Top-level properties (no receiver) from Lollipop (21) to latest

val Context.isSdk21AndAbove: Boolean
    get() = true

val Context.isSdk22AndAbove: Boolean
    get() = true

val Context.isSdk23AndAbove: Boolean
    get() = true

val Context.isSdk24AndAbove: Boolean
    get() = true

val Context.isSdk26AndAbove: Boolean
    get() = true

val Context.isSdk27AndAbove: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
val Context.isSdk28AndAbove: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

val Context.isSdk29AndAbove: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

val Context.isSdk30AndAbove: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

val Context.isSdk31AndAbove: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

val Context.isSdk32AndAbove: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2

val Context.isSdk33AndAbove: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

val Context.isSdk34AndAbove: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE


