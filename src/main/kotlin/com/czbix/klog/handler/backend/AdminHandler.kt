package com.czbix.klog.handler.backend

import com.czbix.klog.handler.BaseHttpHandler
import com.czbix.klog.http.HttpContext
import com.czbix.klog.http.core.DefaultHttpResponse
import com.czbix.klog.template.SoyHelper
import io.netty.handler.codec.http.HttpRequest
import com.czbix.klog.soy.Index1SoyInfo as BackendSoyInfo

class AdminHandler : BaseHttpHandler() {
    override fun getPattern() = "/admin/"

    override fun get(request: HttpRequest, context: HttpContext): DefaultHttpResponse {
        return DefaultHttpResponse().apply {
            fullContent = newContent(SoyHelper.newRenderer(BackendSoyInfo.INDEX, context).render())
        }
    }
}
