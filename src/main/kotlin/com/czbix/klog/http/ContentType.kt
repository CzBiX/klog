package com.czbix.klog.http

import java.nio.charset.Charset

object ContentType {
    val PLAIN_TEXT = create("text/plain")
    val HTML = create("text/html")
    val FORM_URLENCODED = "application/x-www-form-urlencoded"

    fun create(type: String, charset: Charset = Charsets.UTF_8): String {
        return create(type, charset.name())
    }

    fun create(type: String, charset: String): String {
        return "$type; charset=$charset"
    }
}
