package com.czbix.klog.http.interceptor

import com.czbix.klog.http.HttpContextKey
import com.czbix.klog.utils.decodeUrl
import org.apache.commons.codec.net.URLCodec
import org.apache.http.HttpRequest
import org.apache.http.HttpRequestInterceptor
import org.apache.http.cookie.SM
import org.apache.http.protocol.HttpContext
import java.net.HttpCookie

class RequestCookies : HttpRequestInterceptor {
    override fun process(request: HttpRequest, context: HttpContext) {
        val value = request.getLastHeader(SM.COOKIE)?.value ?: return
        val cookies = HttpCookie.parse(value).map {
            it.value = decodeUrl(it.value)
            it.name to it
        }.toMap()
        context.setAttribute(HttpContextKey.COOKIES.key, cookies)
    }
}
