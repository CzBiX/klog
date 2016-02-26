package com.czbix.klog.utils

@Suppress("NOTHING_TO_INLINE")
inline fun <T> T?.or(default: T): T = if (this == null) default else this
inline fun <T> T?.or(compute: () -> T): T = if (this == null) compute() else this
