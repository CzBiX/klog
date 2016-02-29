package com.czbix.klog.handler.backend

import com.czbix.klog.handler.BaseRequestHandler
import com.czbix.klog.http.user
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.protocol.HttpContext

abstract class BackendHandler : BaseRequestHandler() {
    override fun handleRequest(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        if (context.user == null) {
            accessDenied(response, context)
            return
        }

        super.handleRequest(request, response, context)
    }
}
