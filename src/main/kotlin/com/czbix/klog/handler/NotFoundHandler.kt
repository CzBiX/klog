package com.czbix.klog.handler

import com.czbix.klog.common.SoyHelper
import com.czbix.klog.http.NStringEntityEx
import com.czbix.klog.soy.NotFoundSoyInfo
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.protocol.HttpContext

class NotFoundHandler : BaseRequestHandler() {
    override fun getPattern() = "/*"

    override fun get(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        response.setStatusCode(HttpStatus.SC_NOT_FOUND)
        response.entity = NStringEntityEx.fromHtml(SoyHelper.render(NotFoundSoyInfo.NOT_FOUND))
    }
}
