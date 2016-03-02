package com.czbix.klog.handler.backend

import com.czbix.klog.http.NStringEntityEx
import com.czbix.klog.soy.Index1SoyInfo as BackendSoyInfo
import com.czbix.klog.template.SoyHelper
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.protocol.HttpContext

class AdminHandler : BackendHandler() {
    override fun getPattern() = "/admin/"

    override fun get(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        response.entity = NStringEntityEx.fromHtml(SoyHelper.newRenderer(BackendSoyInfo.INDEX, context).render())
    }
}
