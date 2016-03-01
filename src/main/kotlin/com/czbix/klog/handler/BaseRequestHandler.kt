package com.czbix.klog.handler

import com.czbix.klog.http.NStringEntityEx
import com.czbix.klog.soy.ForbiddenSoyInfo
import com.czbix.klog.template.SoyHelper
import org.apache.http.HttpHeaders
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer
import org.apache.http.nio.protocol.HttpAsyncExchange
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer
import org.apache.http.nio.protocol.HttpAsyncRequestHandler
import org.apache.http.protocol.HttpContext

abstract class BaseRequestHandler : HttpAsyncRequestHandler<HttpRequest> {
    abstract fun getPattern(): String

    override fun processRequest(request: HttpRequest, context: HttpContext): HttpAsyncRequestConsumer<HttpRequest> {
        return BasicAsyncRequestConsumer()
    }

    override fun handle(request: HttpRequest, httpExchange: HttpAsyncExchange, context: HttpContext) {
        val response = httpExchange.response
        try {
            handleRequest(request, response, context)
        } catch (e: Exception) {
            response.entity = NStringEntityEx.fromText(e.toString())
        }
        httpExchange.submitResponse()
    }

    open fun handleRequest(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        val method = request.requestLine.method
        when (method) {
            "GET" -> get(request, response, context)
            "POST" -> post(request, response, context)
        }
    }

    fun redirect(response: HttpResponse, url: String, temporary: Boolean = true) {
        require(url.startsWith('/')) {
            "$url is not absolute url"
        }
        response.setStatusCode(if (temporary) HttpStatus.SC_MOVED_TEMPORARILY else HttpStatus.SC_MOVED_PERMANENTLY)
        response.addHeader(HttpHeaders.LOCATION, url)
    }

    fun accessDenied(response: HttpResponse, context: HttpContext) {
        response.setStatusCode(HttpStatus.SC_FORBIDDEN)
        response.entity = NStringEntityEx.fromHtml(SoyHelper.newRenderer(ForbiddenSoyInfo.INDEX, context).render())
    }

    open fun get(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        throw NotImplementedError()
    }

    open fun post(request: HttpRequest, response: HttpResponse, context: HttpContext) {
        throw NotImplementedError()
    }
}
