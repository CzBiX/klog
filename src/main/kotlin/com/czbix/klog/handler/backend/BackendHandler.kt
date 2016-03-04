package com.czbix.klog.handler.backend

import com.czbix.klog.handler.BaseHttpHandler
import com.czbix.klog.http.HttpContext
import com.czbix.klog.http.core.DefaultHttpResponse
import com.czbix.klog.http.user
import io.netty.handler.codec.http.HttpRequest

abstract class BackendHandler : BaseHttpHandler() {
    override fun handleRequest(request: HttpRequest, context: HttpContext): DefaultHttpResponse {
        if (context.user == null) {
            return accessDenied()
        }

        return super.handleRequest(request, context)
    }
}
