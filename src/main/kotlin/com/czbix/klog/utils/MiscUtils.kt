package com.czbix.klog.utils

import org.apache.http.Consts
import java.net.URLDecoder
import java.net.URLEncoder

fun <T> T?.or(default: T): T = if (this == null) default else this
inline fun <T> T?.or(compute: () -> T): T = if (this == null) compute() else this

fun now() = System.currentTimeMillis()

fun encodeUrl(str: String) = URLEncoder.encode(str, "UTF-8")
fun decodeUrl(str: String) = URLDecoder.decode(str, "UTF-8")
