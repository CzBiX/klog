package com.czbix.klog.http.interceptor

import com.czbix.klog.http.HttpContextKey
import com.czbix.klog.http.getAttributeWithType
import com.czbix.klog.utils.encodeUrl
import org.apache.http.HttpResponse
import org.apache.http.HttpResponseInterceptor
import org.apache.http.cookie.SM
import org.apache.http.protocol.HttpContext
import java.net.HttpCookie
import java.net.URI
import java.util.*

class ResponseCookies : HttpResponseInterceptor {
    override fun process(response: HttpResponse, context: HttpContext) {
        val map = context.getAttributeWithType<Map<String, HttpCookie>?>(HttpContextKey.COOKIES_SET.key) ?: return
        val cookies = map.values.map { convertCookie(it)} .joinToString("\n")
        response.addHeader(SM.SET_COOKIE, cookies)
    }

    fun convertCookie(cookie: HttpCookie): String {
        return buildString {
            append(encodeUrl(cookie.name))
            append("=")
            append(encodeUrl(cookie.value))

            append(";Path=")
            append(URI(cookie.path).toASCIIString())

            if (!cookie.domain.isNullOrBlank()) {
                append(";Domain=")
                append(cookie.domain.toLowerCase(Locale.ENGLISH))
            }

            if (cookie.maxAge >= 0) {
                append(";Max-Age=")
                append(cookie.maxAge)
            }

            if (cookie.version > 0) {
                append(";Version=")
                append(cookie.version)
                if (!cookie.comment.isNullOrBlank()) {
                    append(";Comment=")
                    append(encodeUrl(cookie.comment))
                }
            }

            if (cookie.secure) {
                append(";Secure")
            }
            if (cookie.isHttpOnly) {
                append(";HttpOnly")
            }
        }
    }
}
