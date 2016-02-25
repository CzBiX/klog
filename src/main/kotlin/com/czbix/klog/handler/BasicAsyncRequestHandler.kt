package com.czbix.klog.handler

import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer
import org.apache.http.nio.protocol.HttpAsyncExchange
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer
import org.apache.http.nio.protocol.HttpAsyncRequestHandler
import org.apache.http.protocol.HttpContext

abstract class BasicAsyncRequestHandler : HttpAsyncRequestHandler<HttpRequest> {
    abstract fun getPattern(): String

    override fun processRequest(request: HttpRequest, context: HttpContext): HttpAsyncRequestConsumer<HttpRequest> {
        return BasicAsyncRequestConsumer()
    }

    override fun handle(request: HttpRequest, httpExchange: HttpAsyncExchange, context: HttpContext) {
        handle(request, httpExchange.response, context)
        httpExchange.submitResponse()
    }

    abstract fun handle(request: HttpRequest, response: HttpResponse, context: HttpContext)
}