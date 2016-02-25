package com.czbix.klog.handler

import com.czbix.klog.http.NStringEntityEx
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.protocol.HttpContext


class IndexHandler : BasicAsyncRequestHandler() {
    companion object {
        const val PATTERN = "/"
    }

    override fun handle(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        response.entity = NStringEntityEx.fromHtml("Hello klog!")
    }
}