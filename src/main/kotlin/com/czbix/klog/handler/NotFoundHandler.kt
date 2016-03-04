package com.czbix.klog.handler

import com.czbix.klog.http.core.DefaultHttpResponse
import com.czbix.klog.http.HttpContext
import com.czbix.klog.soy.NotFoundSoyInfo
import com.czbix.klog.template.SoyHelper
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponseStatus

class NotFoundHandler : BaseHttpHandler() {
    override fun getPattern() = "/404"

    override fun get(request: HttpRequest, context: HttpContext): DefaultHttpResponse {
        val renderer = SoyHelper.newRenderer(NotFoundSoyInfo.NOT_FOUND, context)
        return newHtmlResponse(renderer.render(), HttpResponseStatus.NOT_FOUND)
    }
}
