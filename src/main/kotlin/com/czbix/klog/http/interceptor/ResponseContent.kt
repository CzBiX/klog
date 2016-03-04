package com.czbix.klog.http.interceptor

import com.czbix.klog.http.HttpContext
import com.czbix.klog.http.HttpResponseInterceptor
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpHeaderUtil
import io.netty.handler.codec.http.HttpResponse

class ResponseContent : HttpResponseInterceptor {
    override fun process(response: HttpResponse, context: HttpContext) {
        if (response is FullHttpResponse) {
            val length = response.content().readableBytes()
            if (length >= 0) {
                HttpHeaderUtil.setContentLength(response, length.toLong())
            }
        }
    }
}
