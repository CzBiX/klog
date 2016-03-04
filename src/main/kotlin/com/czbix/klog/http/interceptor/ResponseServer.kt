package com.czbix.klog.http.interceptor

import com.czbix.klog.http.HttpContext
import com.czbix.klog.http.HttpRequestInterceptor
import com.czbix.klog.http.HttpResponseInterceptor
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponse

class ResponseServer : HttpResponseInterceptor {
    override fun process(response: HttpResponse, context: HttpContext) {
        val headers = response.headers()
        if (!headers.contains(HttpHeaderNames.SERVER)) {
            headers.add(HttpHeaderNames.SERVER, "Klog")
        }
    }
}
