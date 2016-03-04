package com.czbix.klog.utils

@Suppress("NOTHING_TO_INLINE")
inline fun Long.toHexString() = java.lang.Long.toHexString(this)

fun now() = System.currentTimeMillis()
