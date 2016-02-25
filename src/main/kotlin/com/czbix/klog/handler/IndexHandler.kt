package com.czbix.klog.handler

import com.czbix.klog.common.SoyHelper
import com.czbix.klog.http.NStringEntityEx
import com.czbix.klog.soy.IndexSoyInfo
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.protocol.HttpContext


class IndexHandler : BasicAsyncRequestHandler() {
    override fun getPattern() = "/"

    override fun handle(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        response.entity = NStringEntityEx.fromHtml(SoyHelper.render(IndexSoyInfo.INDEX))
    }
}