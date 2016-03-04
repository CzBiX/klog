package com.czbix.klog.http

import com.czbix.klog.http.core.DefaultHttpResponse
import io.netty.handler.codec.http.HttpRequest

interface HttpHandler {
    fun getPattern(): String
    fun handleRequest(request: HttpRequest, context: HttpContext): DefaultHttpResponse
}