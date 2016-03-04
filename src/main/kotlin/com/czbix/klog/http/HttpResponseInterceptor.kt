package com.czbix.klog.http

import io.netty.handler.codec.http.HttpResponse

interface HttpResponseInterceptor {
    fun process(response: HttpResponse, context: HttpContext)
}
