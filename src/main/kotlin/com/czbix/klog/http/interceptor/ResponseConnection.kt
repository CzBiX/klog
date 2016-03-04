package com.czbix.klog.http.interceptor

import com.czbix.klog.http.HttpContext
import com.czbix.klog.http.HttpResponseInterceptor
import com.czbix.klog.http.request
import io.netty.handler.codec.http.HttpHeaderUtil
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.HttpResponseStatus.*

class ResponseConnection : HttpResponseInterceptor {
    override fun process(response: HttpResponse, context: HttpContext) {
        val status = response.status()
        when (status) {
            BAD_REQUEST, REQUEST_TIMEOUT, LENGTH_REQUIRED, REQUEST_ENTITY_TOO_LARGE,
            REQUEST_URI_TOO_LONG, SERVICE_UNAVAILABLE, NOT_IMPLEMENTED -> {
                HttpHeaderUtil.setKeepAlive(response, false)
                return
            }
        }

        val request = context.request
        HttpHeaderUtil.isKeepAlive(request).let {
            HttpHeaderUtil.setKeepAlive(response, it)
        }
    }
}
