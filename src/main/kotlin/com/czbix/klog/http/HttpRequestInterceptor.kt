package com.czbix.klog.http

import io.netty.handler.codec.http.HttpRequest

interface HttpRequestInterceptor {
    fun process(request: HttpRequest, context: HttpContext)
}