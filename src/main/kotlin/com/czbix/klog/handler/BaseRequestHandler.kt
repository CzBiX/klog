package com.czbix.klog.handler

import com.czbix.klog.http.HttpContextKey
import com.czbix.klog.http.interceptor.request
import com.czbix.klog.utils.or
import com.google.common.collect.ImmutableMap
import org.apache.http.Consts
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.client.utils.URLEncodedUtils
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
        handle(request, httpExchange.response, context)
        httpExchange.submitResponse()
    }

    abstract fun handle(request: HttpRequest, response: HttpResponse, context: HttpContext)

    fun getQueryData(context: HttpContext): Map<String, String> {
        @Suppress("UNCHECKED_CAST")
        val cachedData = context.getAttribute(HttpContextKey.QUERY_DATA.key)
                as Map<String, String>?
        return cachedData.or {
            val uri = context.request.requestLine.uri
            val data = URLEncodedUtils.parse(uri, Consts.UTF_8)
                    .fold(ImmutableMap.builder<String, String>(), { builder, pair ->
                        builder.apply { put(pair.name, pair.value) }
                    }).build()
            context.setAttribute(HttpContextKey.QUERY_DATA.key, data)

            return@or data
        }
    }
}
